import type {
  PlatformService,
  PlatformServiceState,
  PlatformServiceStatus,
} from "../domain/platformService";
import { HttpApiClient } from "./HttpApiClient";
import type { PlatformCatalogApi } from "./PlatformCatalogApi";

export class HttpPlatformCatalogApi implements PlatformCatalogApi {
  constructor(private readonly client: HttpApiClient) {}

  getServices(): Promise<PlatformServiceState[]> {
    return this.client.request("/api/v1/catalog/services");
  }

  getAdminStates(): Promise<PlatformServiceState[]> {
    return this.client.request("/api/v1/admin/platform/services");
  }

  updateStatus(
    service: PlatformService,
    status: PlatformServiceStatus,
  ): Promise<PlatformServiceState> {
    return this.client.request(`/api/v1/admin/platform/services/${service}`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    });
  }

  updateDisplayOrder(
    service: PlatformService,
    displayOrder: number,
  ): Promise<PlatformServiceState> {
    return this.client.request(`/api/v1/admin/platform/services/${service}`, {
      method: "PATCH",
      body: JSON.stringify({ displayOrder }),
    });
  }
}
