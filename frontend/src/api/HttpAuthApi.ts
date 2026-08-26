import type { CurrentAuthentication } from "../domain/authentication";
import type { AuthApi } from "./AuthApi";
import { HttpApiClient } from "./HttpApiClient";

export class HttpAuthApi implements AuthApi {
  constructor(private readonly client: HttpApiClient) {}

  getCurrent(): Promise<CurrentAuthentication> {
    return this.client.request("/api/v1/auth/me");
  }

  logout(): Promise<void> {
    return this.client.request("/api/v1/auth/logout", { method: "POST" });
  }

  googleLoginUrl(): string {
    return this.client.resolveUrl("/oauth2/authorization/google");
  }

  googleAdminLoginUrl(): string {
    return this.client.resolveUrl("/api/v1/auth/google/admin");
  }
}
