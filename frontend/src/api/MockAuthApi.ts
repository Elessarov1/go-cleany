import type { CurrentAuthentication } from "../domain/authentication";
import type { AuthApi } from "./AuthApi";

const ANONYMOUS: CurrentAuthentication = {
  authenticated: false,
  customerId: null,
  displayName: null,
  provider: null,
  roles: [],
};

const CUSTOMER: CurrentAuthentication = {
  authenticated: true,
  customerId: 900_002,
  displayName: "Preview Customer",
  provider: "GOOGLE",
  roles: [],
};

const ADMIN: CurrentAuthentication = {
  authenticated: true,
  customerId: 900_001,
  displayName: "Alex",
  provider: "TELEGRAM",
  roles: ["ADMIN"],
};

export class MockAuthApi implements AuthApi {
  private current: CurrentAuthentication;

  constructor(scenario = new URLSearchParams(window.location.search).get("scenario")) {
    const normalized = scenario?.toUpperCase();
    this.current = normalized === "WEB_UNAUTHENTICATED"
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

  googleLoginUrl(): string {
    return "/admin?preview=true&scenario=WEB_ADMIN";
  }

  googleAdminLoginUrl(): string {
    return "/admin?preview=true&scenario=WEB_ADMIN";
  }
}
