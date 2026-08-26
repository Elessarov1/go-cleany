import type { CurrentAuthentication } from "../domain/authentication";
import type { AuthApi } from "./AuthApi";

const ANONYMOUS: CurrentAuthentication = {
  authenticated: false,
  customerId: null,
  displayName: null,
  provider: null,
  roles: [],
  loginProviders: { google: { available: true } },
};

const CUSTOMER: CurrentAuthentication = {
  authenticated: true,
  customerId: 900_002,
  displayName: "Preview Customer",
  provider: "GOOGLE",
  roles: [],
  loginProviders: { google: { available: true } },
};

const ADMIN: CurrentAuthentication = {
  authenticated: true,
  customerId: 900_001,
  displayName: "Alex",
  provider: "TELEGRAM",
  roles: ["ADMIN"],
  loginProviders: { google: { available: true } },
};

const GOOGLE_UNAVAILABLE: CurrentAuthentication = {
  ...ANONYMOUS,
  loginProviders: { google: { available: false } },
};

export class MockAuthApi implements AuthApi {
  private current: CurrentAuthentication;

  constructor(scenario = new URLSearchParams(window.location.search).get("scenario")) {
    const normalized = scenario?.toUpperCase();
    this.current = normalized === "WEB_GOOGLE_UNAVAILABLE"
      ? GOOGLE_UNAVAILABLE
      : normalized === "WEB_UNAUTHENTICATED"
        ? ANONYMOUS
      : normalized === "WEB_CUSTOMER"
        || normalized === "SERVICE_CATALOG_CLEANING_IN_TEST_CUSTOMER"
        ? CUSTOMER
        : ADMIN;
  }

  async getCurrent(): Promise<CurrentAuthentication> {
    return this.current;
  }

  async logout(): Promise<void> {
    this.current = ANONYMOUS;
  }

  googleLoginUrl(returnTo = "/"): string {
    const target = new URL(returnTo, window.location.origin);
    target.searchParams.set("preview", "true");
    target.searchParams.set("scenario", "WEB_CUSTOMER");
    return `${target.pathname}${target.search}`;
  }

  googleAdminLoginUrl(): string {
    return "/admin?preview=true&scenario=WEB_ADMIN";
  }
}
