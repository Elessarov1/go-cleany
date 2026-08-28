import type { AccountIdentities, AccountLinkInitiated, CustomerProfile } from "../domain/customer";
import type { CustomerApi } from "./CustomerApi";
import { HttpApiClient } from "./HttpApiClient";

export class HttpCustomerApi implements CustomerApi {
  constructor(private readonly client: HttpApiClient) {}

  getCurrentProfile(): Promise<CustomerProfile> {
    return this.client.request("/api/v1/customers/me");
  }

  getAccountIdentities(): Promise<AccountIdentities> {
    return this.client.request("/api/v1/account/identities");
  }

  initiateTelegramLink(): Promise<AccountLinkInitiated> {
    return this.client.request("/api/v1/account/link/telegram", { method: "POST" });
  }

  confirmTelegramLink(token: string): Promise<AccountIdentities> {
    return this.client.request("/api/v1/account/link/telegram/confirm", {
      method: "POST",
      body: JSON.stringify({ token }),
    });
  }
}
