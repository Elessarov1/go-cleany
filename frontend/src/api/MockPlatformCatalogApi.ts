import type {
  PlatformService,
  PlatformServiceState,
  PlatformServiceStatus,
} from "../domain/platformService";
import type { PlatformCatalogApi } from "./PlatformCatalogApi";

export class MockPlatformCatalogApi implements PlatformCatalogApi {
  private readonly adminCustomer: boolean;
  private states: PlatformServiceState[] = [
    state("CLEANING", 10),
    state("RENTAL", 20),
    state("TRANSFER", 30),
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
    return sorted(this.states.filter(({ status }) => status === "ENABLED"
      || status === "IN_TEST" && this.adminCustomer));
  }

  async getAdminStates(): Promise<PlatformServiceState[]> {
    return sorted(this.states);
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

  async updateDisplayOrder(
    service: PlatformService,
    displayOrder: number,
  ): Promise<PlatformServiceState> {
    const current = this.states.find((item) => item.service === service);
    if (!current) throw new Error(`Unknown platform service: ${service}`);
    const updated = {
      ...current,
      displayOrder,
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

function state(service: PlatformService, displayOrder: number): PlatformServiceState {
  return {
    service,
    status: "ENABLED",
    displayOrder,
    updatedAt: new Date(0).toISOString(),
    updatedByCustomerId: null,
    version: 0,
  };
}

function sorted(states: PlatformServiceState[]): PlatformServiceState[] {
  return [...states].sort((left, right) => left.displayOrder - right.displayOrder
    || left.service.localeCompare(right.service));
}
