import type { AccountIdentities, AccountLinkInitiated, CustomerProfile } from "../domain/customer";

export interface CustomerApi {
  getCurrentProfile(): Promise<CustomerProfile>;
  getAccountIdentities(): Promise<AccountIdentities>;
  initiateTelegramLink(): Promise<AccountLinkInitiated>;
  confirmTelegramLink(token: string): Promise<AccountIdentities>;
}
