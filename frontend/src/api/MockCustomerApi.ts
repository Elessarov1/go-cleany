import type { AccountIdentities, AccountLinkInitiated, CustomerProfile } from "../domain/customer";
import type { CustomerApi } from "./CustomerApi";

export class MockCustomerApi implements CustomerApi {
  private linked = false;
  async getCurrentProfile(): Promise<CustomerProfile> {
    await new Promise((resolve) => window.setTimeout(resolve, 120));
    return { phone: "+90 555 123 45 67" };
  }

  async getAccountIdentities(): Promise<AccountIdentities> {
    return { identities: [
      { provider: "GOOGLE", linked: true, username: null, writeAccessAllowed: false },
      { provider: "TELEGRAM", linked: this.linked, username: this.linked ? "browser_preview" : null, writeAccessAllowed: this.linked },
    ] };
  }

  async initiateTelegramLink(): Promise<AccountLinkInitiated> {
    return { deepLink: "https://t.me/example/app?startapp=preview", expiresAt: new Date(Date.now() + 600_000).toISOString() };
  }

  async confirmTelegramLink(): Promise<AccountIdentities> {
    this.linked = true;
    return this.getAccountIdentities();
  }
}
