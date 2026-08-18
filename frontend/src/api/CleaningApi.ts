import type { CleaningConfiguration } from "../domain/configuration";
import type { AdminDashboard, AdminOrderDetails } from "../domain/admin";
import type {
  CleaningOrder,
  CreateCleaningOrderRequest,
} from "../domain/order";

export interface CleaningApi {
  hasAdminAccess(): Promise<boolean>;
  getAdminDashboard(limit?: number): Promise<AdminDashboard>;
  getAdminOrder(id: number): Promise<AdminOrderDetails>;
  getConfiguration(): Promise<CleaningConfiguration>;
  createOrder(request: CreateCleaningOrderRequest): Promise<CleaningOrder>;
  getOrders(): Promise<CleaningOrder[]>;
  getOrder(id: number): Promise<CleaningOrder>;
  cancelOrder(id: number): Promise<CleaningOrder>;
}

export class CleaningApiError extends Error {
  constructor(
    message: string,
    readonly status?: number,
  ) {
    super(message);
    this.name = "CleaningApiError";
  }
}
