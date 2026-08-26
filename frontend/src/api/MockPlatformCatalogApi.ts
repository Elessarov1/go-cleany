import type {
  PlatformService,
  PlatformServiceState,
  PlatformServiceStatus,
} from "../domain/platformService";
import type { PlatformCatalogApi } from "./PlatformCatalogApi";

export class MockPlatformCatalogApi implements PlatformCatalogApi {
  private readonly adminCustomer: boolean;
  private states: PlatformServiceState[] = [
    state("CLEANING"),
    state("RENTAL"),
  ];

  constructor(scenario = new URLSearchParams(window.location.search).get("scenario")) {
    const normalized = scenario?.toUpperCase();
    this.adminCustomer = normalized !== "SERVICE_CATALOG_CLEANING_IN_TEST_CUSTOMER";
    if (normalized === "SERVICE_CATALOG_CLEANING_DISABLED") {
      this.states = this.withStatus("CLEANING", "DISABLED");
    } else if (normalized === "SERVICE_CATALOG_RENTAL_DISABLED") {
      this.states = this.withStatus("RENTAL", "DISABLED");
    } else if (normalized === "SERVICE_CATALOG_CLEANING_IN_TEST_ADMIN"
      || normalized === "SERVICE_CATALOG_CLEANING_IN_TEST_CUSTOMER") {
      this.states = this.withStatus("CLEANING", "IN_TEST");
    }
  }

  async getServices(): Promise<PlatformServiceState[]> {
    return this.states.filter(({ status }) => status === "ENABLED"
      || status === "IN_TEST" && this.adminCustomer);
  }

  async getAdminStates(): Promise<PlatformServiceState[]> {
    return [...this.states];
  }

  async updateStatus(
    service: PlatformService,
    status: PlatformServiceStatus,
  ): Promise<PlatformServiceState> {
    const current = this.states.find((item) => item.service === service);
    if (!current) throw new Error(`Unknown platform service: ${service}`);
    const updated = {
      ...current,
      status,
      updatedAt: new Date().toISOString(),
      version: current.version + 1,
    };
    this.states = this.states.map((item) => item.service === service ? updated : item);
    return updated;
  }

  private withStatus(
    service: PlatformService,
    status: PlatformServiceStatus,
  ): PlatformServiceState[] {
    return this.states.map((item) => item.service === service ? { ...item, status } : item);
  }
}

function state(service: PlatformService): PlatformServiceState {
  return {
    service,
    status: "ENABLED",
    updatedAt: new Date(0).toISOString(),
    updatedByCustomerId: null,
    version: 0,
  };
}
