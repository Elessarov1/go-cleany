import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { createMemoryRouter, RouterProvider } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import type { AuthApi } from "../../api/AuthApi";
import { AuthApiProvider } from "../../api/AuthApiProvider";
import type { Platform } from "../../platform/Platform";
import { PlatformProvider } from "../../platform/PlatformProvider";
import { AuthenticationRequiredState } from "./CustomerAccessGate";

describe("AuthenticationRequiredState", () => {
  it("never offers embedded Google login inside Telegram", async () => {
    const close = vi.fn();
    const openTelegramLink = vi.fn();
    const authApi = {
      getCurrent: vi.fn().mockResolvedValue({
        authenticated: false,
        customerId: null,
        displayName: null,
        provider: null,
        roles: [],
        loginProviders: { google: { available: true } },
      }),
      logout: vi.fn(),
      googleLoginUrl: vi.fn(() => "/google-login"),
      googleAdminLoginUrl: vi.fn(() => "/google-admin"),
    } as AuthApi;
    const platform = {
      kind: "TELEGRAM",
      close,
      openTelegramLink,
      getAuthData: () => null,
    } as unknown as Platform;
    const router = createMemoryRouter([
      { path: "/", element: <AuthenticationRequiredState /> },
    ]);

    render(
      <PlatformProvider platform={platform}>
        <AuthApiProvider api={authApi}>
          <RouterProvider router={router} />
        </AuthApiProvider>
      </PlatformProvider>,
    );

    expect(await screen.findByRole("heading", { name: "Open Loco Place as a Mini App" })).toBeInTheDocument();
    expect(screen.queryByRole("link", { name: "Continue with Google" })).not.toBeInTheDocument();

    await userEvent.click(screen.getByRole("button", { name: "Open Loco Place" }));
    expect(openTelegramLink).toHaveBeenCalledWith("https://t.me/go_cleany_bot?startapp");

    await userEvent.click(screen.getByRole("button", { name: "Close Mini App" }));
    expect(close).toHaveBeenCalledOnce();
  });

  it("distinguishes a rejected signed launch from a browser launch without Telegram proof", async () => {
    const getCurrent = vi.fn().mockResolvedValue({
      authenticated: false,
      customerId: null,
      displayName: null,
      provider: null,
      roles: [],
      loginProviders: { google: { available: true } },
    });
    const authApi = {
      getCurrent,
      logout: vi.fn(),
      googleLoginUrl: vi.fn(() => "/google-login"),
      googleAdminLoginUrl: vi.fn(() => "/google-admin"),
    } as AuthApi;
    const platform = {
      kind: "TELEGRAM",
      getAuthData: () => "signed-init-data",
    } as unknown as Platform;
    const router = createMemoryRouter([
      { path: "/", element: <AuthenticationRequiredState /> },
    ]);

    render(
      <PlatformProvider platform={platform}>
        <AuthApiProvider api={authApi}>
          <RouterProvider router={router} />
        </AuthApiProvider>
      </PlatformProvider>,
    );

    expect(await screen.findByRole("heading", {
      name: "Couldn't confirm your Telegram sign-in",
    })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Open Loco Place" })).not.toBeInTheDocument();

    await userEvent.click(screen.getByRole("button", { name: "Try again" }));
    await waitFor(() => expect(getCurrent).toHaveBeenCalledTimes(2));
  });
});
