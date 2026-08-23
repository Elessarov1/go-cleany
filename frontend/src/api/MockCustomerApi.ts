import type { CustomerProfile } from "../domain/customer";
import type { CustomerApi } from "./CustomerApi";

export class MockCustomerApi implements CustomerApi {
  async getCurrentProfile(): Promise<CustomerProfile> {
    await new Promise((resolve) => window.setTimeout(resolve, 120));
    return { phone: "+90 555 123 45 67" };
  }
}
