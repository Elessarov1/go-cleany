import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import type { AuthApi } from "../../api/AuthApi";
import { AuthApiProvider } from "../../api/AuthApiProvider";
import { ApiError } from "../../api/ApiError";
import type { CustomerApi } from "../../api/CustomerApi";
import { CustomerApiProvider } from "../../api/CustomerApiProvider";
import type { CurrentAuthentication } from "../../domain/authentication";
import type { Platform, PlatformKind } from "../../platform/Platform";
import { PlatformProvider } from "../../platform/PlatformProvider";
import { AccountDeletedPage, AccountDeletionPage } from "./AccountDeletionPage";

const customer: CurrentAuthentication = {
  authenticated: true,
  customerId: 12,
  displayName: "Customer",
  provider: "TELEGRAM",
  roles: [],
  loginProviders: { google: { available: true } },
};

describe("AccountDeletionPage", () => {
  it("uses fresh Telegram init data, invalidates auth locally and shows success", async () => {
    const create = vi.fn().mockResolvedValue(challenge("TELEGRAM"));
    const confirm = vi.fn().mockResolvedValue(undefined);
    const getCurrent = vi.fn().mockResolvedValue(customer);
    renderPage({ create, confirm, getCurrent, kind: "TELEGRAM" });

    await userEvent.click(await screen.findByRole("button", { name: "Continue to deletion" }));
    await userEvent.dblClick(screen.getByRole("button", { name: "Delete permanently" }));

    expect(await screen.findByRole("heading", { name: "Account deleted" })).toBeInTheDocument();
    expect(create).toHaveBeenCalledTimes(1);
    expect(confirm).toHaveBeenCalledWith("challenge-id", "signed-telegram-init-data");
    expect(confirm).toHaveBeenCalledTimes(1);
    expect(getCurrent).toHaveBeenCalledTimes(1);
  });

  it("shows active-operation guidance without applying a partial success", async () => {
    const create = vi.fn().mockResolvedValue(challenge("TELEGRAM"));
    const confirm = vi.fn().mockRejectedValue(new ApiError(
      "blocked", 409, "account_deletion_blocked_by_active_operation",
    ));
    renderPage({ create, confirm, getCurrent: vi.fn().mockResolvedValue(customer), kind: "TELEGRAM" });

    await userEvent.click(await screen.findByRole("button", { name: "Continue to deletion" }));
    await userEvent.click(screen.getByRole("button", { name: "Delete permanently" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("active tasks");
    expect(screen.getByRole("link", { name: "Open activity" })).toHaveAttribute("href", "/account/activity");
    expect(screen.queryByText("Account deleted")).not.toBeInTheDocument();
  });

  it("accepts a recent Web reauthentication return and sends no provider token", async () => {
    window.sessionStorage.setItem(
      "loco-place.account-deletion-google-reauthenticated-at", String(Date.now()),
    );
    const create = vi.fn().mockResolvedValue(challenge("GOOGLE"));
    const confirm = vi.fn().mockResolvedValue(undefined);
    renderPage({
      create,
      confirm,
      getCurrent: vi.fn().mockResolvedValue({ ...customer, provider: "GOOGLE" }),
      kind: "WEB",
    });

    await userEvent.click(await screen.findByRole("button", { name: "Continue to deletion" }));
    await userEvent.click(screen.getByRole("button", { name: "Delete permanently" }));

    await waitFor(() => expect(confirm).toHaveBeenCalledWith("challenge-id", null));
  });

  it("keeps the account active after a network failure and supports a safe retry", async () => {
    const create = vi.fn().mockResolvedValue(challenge("TELEGRAM"));
    const confirm = vi.fn()
      .mockRejectedValueOnce(new TypeError("Network request failed"))
      .mockResolvedValueOnce(undefined);
    renderPage({
      create,
      confirm,
      getCurrent: vi.fn().mockResolvedValue(customer),
      kind: "TELEGRAM",
    });

    await userEvent.click(await screen.findByRole("button", { name: "Continue to deletion" }));
    await userEvent.click(screen.getByRole("button", { name: "Delete permanently" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("No partial deletion was applied");

    await userEvent.click(screen.getByRole("button", { name: "Try again" }));
    expect(await screen.findByRole("heading", { name: "Account deleted" })).toBeInTheDocument();
    expect(create).toHaveBeenCalledTimes(2);
    expect(confirm).toHaveBeenCalledTimes(2);
  });

  it("requires a fresh Web login again when backend proof has expired", async () => {
    window.sessionStorage.setItem(
      "loco-place.account-deletion-google-reauthenticated-at", String(Date.now()),
    );
    renderPage({
      create: vi.fn().mockResolvedValue(challenge("GOOGLE")),
      confirm: vi.fn().mockRejectedValue(new ApiError(
        "expired", 401, "auth_challenge_expired",
      )),
      getCurrent: vi.fn().mockResolvedValue({ ...customer, provider: "GOOGLE" }),
      kind: "WEB",
    });

    await userEvent.click(await screen.findByRole("button", { name: "Continue to deletion" }));
    await userEvent.click(screen.getByRole("button", { name: "Delete permanently" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Google verification expired");
    expect(screen.getAllByRole("button", { name: "Verify with Google" })).toHaveLength(2);
  });

  it("explains why an administrator cannot self-delete", async () => {
    renderPage({
      create: vi.fn(),
      confirm: vi.fn(),
      getCurrent: vi.fn().mockResolvedValue({ ...customer, roles: ["ADMIN"] }),
      kind: "TELEGRAM",
    });

    expect(await screen.findByRole("heading", { name: "Administrator account" })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Continue to deletion" })).not.toBeInTheDocument();
  });
});

function challenge(provider: "GOOGLE" | "TELEGRAM") {
  return {
    id: "challenge-id",
    provider,
    nonce: null,
    expiresAt: new Date(Date.now() + 300_000).toISOString(),
  } as const;
}

function renderPage(options: {
  create: ReturnType<typeof vi.fn>;
  confirm: ReturnType<typeof vi.fn>;
  getCurrent: ReturnType<typeof vi.fn>;
  kind: PlatformKind;
}) {
  const customerApi = {
    createAccountDeletionRequest: options.create,
    confirmAccountDeletion: options.confirm,
  } as unknown as CustomerApi;
  const authApi = {
    getCurrent: options.getCurrent,
    logout: vi.fn(),
    googleLoginUrl: vi.fn(() => "/google-login"),
    googleAdminLoginUrl: vi.fn(() => "/google-admin"),
  } as unknown as AuthApi;
  const platform = {
    kind: options.kind,
    getAuthData: () => options.kind === "TELEGRAM" ? "signed-telegram-init-data" : null,
    close: vi.fn(),
  } as unknown as Platform;
  const router = createMemoryRouter([
    { path: "/account/delete", element: <AccountDeletionPage /> },
    { path: "/account/deleted", element: <AccountDeletedPage /> },
  ], { initialEntries: ["/account/delete"] });

  return render(
    <PlatformProvider platform={platform}>
      <AuthApiProvider api={authApi}>
        <CustomerApiProvider api={customerApi}>
          <RouterProvider router={router} />
        </CustomerApiProvider>
      </AuthApiProvider>
    </PlatformProvider>,
  );
}
