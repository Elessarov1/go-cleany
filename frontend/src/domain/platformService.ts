export type PlatformService = "CLEANING" | "RENTAL";

export type PlatformServiceStatus = "ENABLED" | "IN_TEST" | "DISABLED";

export interface PlatformServiceState {
  service: PlatformService;
  status: PlatformServiceStatus;
  updatedAt: string;
  updatedByCustomerId: number | null;
  version: number;
}
