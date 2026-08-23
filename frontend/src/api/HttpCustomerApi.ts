import type { CustomerProfile } from "../domain/customer";
import type { CustomerApi } from "./CustomerApi";
import { HttpApiClient } from "./HttpApiClient";

export class HttpCustomerApi implements CustomerApi {
  constructor(private readonly client: HttpApiClient) {}

  getCurrentProfile(): Promise<CustomerProfile> {
    return this.client.request("/api/v1/customers/me");
  }
}
