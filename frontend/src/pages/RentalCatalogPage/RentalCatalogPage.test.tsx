import { act, render, screen, waitFor } from "@testing-library/react";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
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
});

function renderPage(api: RentalApi, initialEntry: string) {
  const router = createMemoryRouter([
    { path: "/rent", element: <RentalCatalogPage /> },
  ], { initialEntries: [initialEntry] });
  const rendered = render(
    <RentalApiProvider api={api}><RouterProvider router={router} /></RentalApiProvider>,
  );
  return { ...rendered, router };
}

function rentalApi(search: (request: RentalSearchRequest) => Promise<RentalSearchResponse>): RentalApi {
  return {
    getConfiguration: () => Promise.resolve(configuration),
    search,
    recordFirstCardRendered: () => Promise.resolve(),
  } as unknown as RentalApi;
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
