import type {
  PlatformService,
  PlatformServiceState,
  PlatformServiceStatus,
} from "../domain/platformService";

export interface PlatformCatalogApi {
  getServices(): Promise<PlatformServiceState[]>;
  getAdminStates(): Promise<PlatformServiceState[]>;
  updateStatus(
    service: PlatformService,
    status: PlatformServiceStatus,
  ): Promise<PlatformServiceState>;
}
