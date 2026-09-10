import type { CurrentAuthentication } from "../domain/authentication";
import type { AuthApi } from "./AuthApi";
import { generatedWire, HttpApiClient } from "./HttpApiClient";
import {
  AuthenticationApi as GeneratedAuthenticationApi,
  Configuration,
  CurrentAuthenticationToJSON,
} from "@locoplace/api-client";

export class HttpAuthApi implements AuthApi {
  private readonly generated: GeneratedAuthenticationApi;

  constructor(private readonly client: HttpApiClient) {
    this.generated = new GeneratedAuthenticationApi(new Configuration({
      basePath: client.basePath,
      fetchApi: client.generatedFetch,
    }));
  }

  getCurrent(): Promise<CurrentAuthentication> {
    return this.client.generated(this.generated.getCurrentAuthentication())
      .then((value) => generatedWire<CurrentAuthentication>(CurrentAuthenticationToJSON(value)));
  }

  logout(): Promise<void> {
    return this.client.generated(this.generated.logoutCurrentSession());
  }

  googleLoginUrl(returnTo = "/"): string {
    return this.client.resolveUrl(`/api/v1/auth/google/login?returnTo=${encodeURIComponent(returnTo)}`);
  }

  googleAdminLoginUrl(): string {
    return this.client.resolveUrl("/api/v1/auth/google/admin");
  }
}
