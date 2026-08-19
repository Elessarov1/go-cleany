import type { CleaningConfiguration } from "../domain/configuration";
import type {
  AdminDashboard,
  AdminOrderDetails,
  AdminReferralOverview,
  PartnerPayout,
  ReferralPartner,
} from "../domain/admin";
import type {
  CleaningOrder,
  CleaningOrderQuote,
  CleaningOrderQuoteRequest,
  CreateCleaningOrderRequest,
  ReferralSummary,
} from "../domain/order";

export interface CleaningApi {
  hasAdminAccess(): Promise<boolean>;
  getAdminDashboard(limit?: number): Promise<AdminDashboard>;
  getAdminOrder(id: number): Promise<AdminOrderDetails>;
  getAdminReferralOverview(): Promise<AdminReferralOverview>;
  createReferralPartner(name: string): Promise<ReferralPartner>;
  markPartnerPayoutPaid(id: number): Promise<PartnerPayout>;
  getConfiguration(): Promise<CleaningConfiguration>;
  quoteOrder(request: CleaningOrderQuoteRequest): Promise<CleaningOrderQuote>;
  createOrder(request: CreateCleaningOrderRequest): Promise<CleaningOrder>;
  getReferralSummary(): Promise<ReferralSummary>;
  getOrders(): Promise<CleaningOrder[]>;
  getOrder(id: number): Promise<CleaningOrder>;
  cancelOrder(id: number): Promise<CleaningOrder>;
}

export class CleaningApiError extends Error {
  constructor(
    message: string,
    readonly status?: number,
    readonly code?: string,
    readonly fieldErrors: Record<string, string> = {},
  ) {
    super(message);
    this.name = "CleaningApiError";
  }
}
