export type PlatformService = "CLEANING" | "RENTAL" | "TRANSFER";

export type PlatformServiceStatus = "ENABLED" | "IN_TEST" | "DISABLED";

export interface PlatformServiceState {
  service: PlatformService;
  status: PlatformServiceStatus;
  displayOrder: number;
  updatedAt: string;
  updatedByCustomerId: number | null;
  version: number;
}
