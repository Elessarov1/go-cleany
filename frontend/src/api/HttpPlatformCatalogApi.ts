import type {
  PlatformService,
  PlatformServiceState,
  PlatformServiceStatus,
} from "../domain/platformService";
import { HttpApiClient } from "./HttpApiClient";
import type { PlatformCatalogApi } from "./PlatformCatalogApi";
import { Configuration, PlatformApi } from "@locoplace/api-client";

export class HttpPlatformCatalogApi implements PlatformCatalogApi {
  private readonly generated: PlatformApi;

  constructor(private readonly client: HttpApiClient) {
    this.generated = new PlatformApi(new Configuration({
      basePath: client.resolveUrl(""),
      credentials: "include",
      fetchApi: client.generatedFetch,
    }));
  }

  async getServices(): Promise<PlatformServiceState[]> {
    const result = await this.client.generated(this.generated.getPlatformServices());
    return result.map((state) => ({
      ...state,
      service: state.service as PlatformService,
      status: state.status as PlatformServiceStatus,
      updatedAt: state.updatedAt.toISOString(),
      updatedByCustomerId: state.updatedByCustomerId ?? null,
    }));
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
