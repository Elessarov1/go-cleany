import { act, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createMemoryRouter, RouterProvider, useLocation } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import type { RentalApi } from "../../api/RentalApi";
import { RentalApiProvider } from "../../api/RentalApiProvider";
import type {
  RentalConfiguration,
  RentalSearchRequest,
  RentalSearchResponse,
} from "../../domain/rental";
import { RentalCatalogPage } from "./RentalCatalogPage";

const configuration: RentalConfiguration = {
  minStayDays: 7,
  longTermMinDays: 30,
  longTermDiscountRate: 0.1,
  maxStayDays: 365,
  bookingStartMonthsAhead: 6,
  maxActiveBookingsPerCustomer: 3,
  today: "2026-09-09",
  latestCheckInDate: "2027-03-09",
};

describe("RentalCatalogPage", () => {
  it("does not search on the empty or invalid URL, but auto-searches a complete URL", async () => {
    const search = vi.fn().mockResolvedValue(response("valid", "Available home"));
    const api = rentalApi(search);
    const empty = renderPage(api, "/rent");
    await screen.findByText("Find a place for your dates");
    expect(search).not.toHaveBeenCalled();
    empty.unmount();

    const invalid = renderPage(api, "/rent?termType=DATE_RANGE&checkInDate=2026-10-01&guests=2");
    await screen.findByText("Complete this field.");
    expect(search).not.toHaveBeenCalled();
    invalid.unmount();

    renderPage(api, "/rent?termType=DATE_RANGE&checkInDate=2026-10-01&checkOutDate=2026-10-08&guests=2");
    expect(await screen.findByText("Available home")).toBeInTheDocument();
    expect(search).toHaveBeenCalledTimes(1);
  });

  it("ignores a late result from an aborted previous search", async () => {
    const first = deferred<RentalSearchResponse>();
    const second = deferred<RentalSearchResponse>();
    const search = vi.fn()
      .mockImplementationOnce(() => first.promise)
      .mockImplementationOnce(() => second.promise);
    const api = rentalApi(search);
    const { router } = renderPage(
      api,
      "/rent?termType=DATE_RANGE&checkInDate=2026-10-01&checkOutDate=2026-10-08&guests=2",
    );
    await waitFor(() => expect(search).toHaveBeenCalledTimes(1));
    await act(() => router.navigate(
      "/rent?termType=MONTHLY&checkInDate=2026-11-01&months=2&guests=3",
    ));
    await waitFor(() => expect(search).toHaveBeenCalledTimes(2));
    await act(async () => second.resolve(response("second", "Newer result", "MONTHLY")));
    expect(await screen.findByText("Newer result")).toBeInTheDocument();
    await act(async () => first.resolve(response("first", "Stale result")));
    expect(screen.queryByText("Stale result")).not.toBeInTheDocument();
    expect(screen.getByText("Newer result")).toBeInTheDocument();
  });

  it("appends and deduplicates cursor pages, then shows an exact final count", async () => {
    const recordFirstCardRendered = vi.fn().mockResolvedValue(undefined);
    const search = vi.fn()
      .mockResolvedValueOnce(page("execution", range(1, 20), true, "cursor-20"))
      .mockResolvedValueOnce(page("unexpected", [20, 21, 22, 23, 24, 25], false, null));
    const { router } = renderPage(
      rentalApi(search, recordFirstCardRendered),
      "/rent?termType=DATE_RANGE&checkInDate=2026-10-01&checkOutDate=2026-10-08&guests=2",
    );

    expect(await screen.findByRole("heading", { name: "20+ apartments" })).toBeInTheDocument();
    await userEvent.click(screen.getByRole("button", { name: "Show more" }));

    expect(await screen.findByRole("heading", { name: "25 apartments" })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Show more" })).not.toBeInTheDocument();
    expect(screen.getAllByRole("link")).toHaveLength(25);
    expect(search).toHaveBeenNthCalledWith(2, expect.anything(), expect.objectContaining({
      cursor: "cursor-20",
      size: 20,
    }));
    expect(router.state.location.search).not.toContain("cursor");
    await waitFor(() => expect(recordFirstCardRendered).toHaveBeenCalledTimes(1));

    await userEvent.click(screen.getByRole("heading", { name: "Apartment 25" }));
    expect(await screen.findByTestId("search-execution")).toHaveTextContent("execution");
  });

  it("keeps loaded cards and retries after a continuation error", async () => {
    const search = vi.fn()
      .mockResolvedValueOnce(page("execution", [1, 2], true, "cursor-2"))
      .mockRejectedValueOnce(new Error("network"))
      .mockResolvedValueOnce(page("execution", [3], false, null));
    renderPage(
      rentalApi(search),
      "/rent?termType=DATE_RANGE&checkInDate=2026-10-01&checkOutDate=2026-10-08&guests=2",
    );
    await screen.findByText("Apartment 1");

    await userEvent.click(screen.getByRole("button", { name: "Show more" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("next apartments");
    expect(screen.getByText("Apartment 1")).toBeInTheDocument();
    await userEvent.click(screen.getByRole("button", { name: "Try loading again" }));

    expect(await screen.findByText("Apartment 3")).toBeInTheDocument();
    expect(screen.getByText("Apartment 1")).toBeInTheDocument();
    expect(search).toHaveBeenCalledTimes(3);
  });

  it("coalesces a double click while a cursor request is pending", async () => {
    const continuation = deferred<RentalSearchResponse>();
    const search = vi.fn()
      .mockResolvedValueOnce(page("execution", [1], true, "cursor-1"))
      .mockImplementationOnce(() => continuation.promise);
    renderPage(
      rentalApi(search),
      "/rent?termType=DATE_RANGE&checkInDate=2026-10-01&checkOutDate=2026-10-08&guests=2",
    );

    await screen.findByText("Apartment 1");
    await userEvent.dblClick(screen.getByRole("button", { name: "Show more" }));
    expect(search).toHaveBeenCalledTimes(2);
    await act(async () => continuation.resolve(page("execution", [2], false, null)));
  });

  it("ignores a late cursor response after applying new criteria", async () => {
    const continuation = deferred<RentalSearchResponse>();
    const search = vi.fn()
      .mockResolvedValueOnce(page("first", [1], true, "cursor-1"))
      .mockImplementationOnce(() => continuation.promise)
      .mockResolvedValueOnce(response("second", "New criteria", "MONTHLY"));
    const { router } = renderPage(
      rentalApi(search),
      "/rent?termType=DATE_RANGE&checkInDate=2026-10-01&checkOutDate=2026-10-08&guests=2",
    );
    await screen.findByText("Apartment 1");
    await userEvent.click(screen.getByRole("button", { name: "Show more" }));
    await waitFor(() => expect(search).toHaveBeenCalledTimes(2));

    await act(() => router.navigate(
      "/rent?termType=MONTHLY&checkInDate=2026-11-01&months=2&guests=3",
    ));
    expect(await screen.findByText("New criteria")).toBeInTheDocument();
    await act(async () => continuation.resolve(page("first", [99], false, null)));

    expect(screen.queryByText("Apartment 99")).not.toBeInTheDocument();
    expect(screen.getByText("New criteria")).toBeInTheDocument();
  });
});

function renderPage(api: RentalApi, initialEntry: string) {
  const router = createMemoryRouter([
    { path: "/rent", element: <RentalCatalogPage /> },
    { path: "/rent/properties/:slug", element: <SearchExecutionProbe /> },
  ], { initialEntries: [initialEntry] });
  const rendered = render(
    <RentalApiProvider api={api}><RouterProvider router={router} /></RentalApiProvider>,
  );
  return { ...rendered, router };
}

function SearchExecutionProbe() {
  const location = useLocation();
  return <div data-testid="search-execution">{location.state?.rentalSearchExecutionId}</div>;
}

function rentalApi(
  search: (request: RentalSearchRequest) => Promise<RentalSearchResponse>,
  recordFirstCardRendered: () => Promise<void> = () => Promise.resolve(),
): RentalApi {
  return {
    getConfiguration: () => Promise.resolve(configuration),
    search,
    recordFirstCardRendered,
  } as unknown as RentalApi;
}

function page(
  executionId: string,
  ids: number[],
  hasMore: boolean,
  nextCursor: string | null,
): RentalSearchResponse {
  const value = response(executionId, "Apartment");
  const template = value.properties[0]!;
  return {
    ...value,
    properties: ids.map((id) => ({
      ...template,
      id,
      slug: `apartment-${id}`,
      titleRu: `Apartment ${id}`,
      titleEn: `Apartment ${id}`,
    })),
    nextCursor,
    hasMore,
  };
}

function range(from: number, to: number): number[] {
  return Array.from({ length: to - from + 1 }, (_, index) => from + index);
}

function response(
  id: string,
  title: string,
  termType: "DATE_RANGE" | "MONTHLY" = "DATE_RANGE",
): RentalSearchResponse {
  const monthly = termType === "MONTHLY";
  return {
    searchExecutionId: id,
    calculatedAt: "2026-09-09T10:00:00Z",
    nextCursor: null,
    hasMore: false,
    criteria: {
      mode: termType,
      termType,
      checkInDate: monthly ? "2026-11-01" : "2026-10-01",
      checkOutDate: monthly ? "2026-12-31" : "2026-10-08",
      rentalMonths: monthly ? 2 : null,
      durationDays: monthly ? 61 : 8,
      guests: monthly ? 3 : 2,
    },
    properties: [{
      id: 1,
      slug: "home",
      titleRu: title,
      titleEn: title,
      descriptionEn: "Description",
      area: "Mahmutlar",
      bedrooms: 1,
      maxGuests: 4,
      areaSqm: 60,
      baseDailyPrice: 100,
      currency: "TRY",
      coverUrl: null,
      price: {
        baseDailyPrice: 100,
        baseMonthlyPrice: monthly ? 3000 : null,
        monthlyPrice: monthly ? 2700 : null,
        baseAmount: monthly ? 6000 : 800,
        discountRate: monthly ? 0.1 : 0,
        discountAmount: monthly ? 600 : 0,
        longTermDiscountApplied: monthly,
        totalPrice: monthly ? 5400 : 800,
        currency: "TRY",
        rentalMonths: monthly ? 2 : null,
        durationDays: monthly ? 61 : 8,
      },
    }],
  };
}

function deferred<T>() {
  let resolve!: (value: T) => void;
  let reject!: (error: unknown) => void;
  const promise = new Promise<T>((resolvePromise, rejectPromise) => {
    resolve = resolvePromise;
    reject = rejectPromise;
  });
  return { promise, resolve, reject };
}
