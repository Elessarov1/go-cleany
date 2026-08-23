import type { CustomerProfile } from "../domain/customer";

export interface CustomerApi {
  getCurrentProfile(): Promise<CustomerProfile>;
}
