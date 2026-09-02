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
  CleaningRepeatReminder,
  CleaningRepeatReminderSelection,
  CleaningRepeatPrefill,
  CreateCleaningOrderRequest,
  ReferralSummary,
} from "../domain/order";

export interface CleaningApi {
  hasAdminAccess(): Promise<boolean>;
  getAdminDashboard(limit?: number): Promise<AdminDashboard>;
  getAdminOrder(id: number): Promise<AdminOrderDetails>;
  getAdminIssuePhoto(orderId: number, photoId: number): Promise<Blob>;
  resolveAdminIssue(orderId: number, resolutionComment: string): Promise<AdminOrderDetails>;
  getAdminReferralOverview(): Promise<AdminReferralOverview>;
  createReferralPartner(name: string): Promise<ReferralPartner>;
  markPartnerPayoutPaid(id: number): Promise<PartnerPayout>;
  getConfiguration(): Promise<CleaningConfiguration>;
  quoteOrder(request: CleaningOrderQuoteRequest): Promise<CleaningOrderQuote>;
  createOrder(request: CreateCleaningOrderRequest): Promise<CleaningOrder>;
  getReferralSummary(): Promise<ReferralSummary>;
  getOrders(): Promise<CleaningOrder[]>;
  getOrder(id: number): Promise<CleaningOrder>;
  recordRepeatShown(id: number): Promise<void>;
  getRepeatPrefill(id: number): Promise<CleaningRepeatPrefill>;
  getRepeatReminder(id: number): Promise<CleaningRepeatReminder>;
  updateRepeatReminder(id: number, selection: CleaningRepeatReminderSelection): Promise<CleaningRepeatReminder>;
  getReportPhoto(orderId: number, mediaId: number): Promise<Blob>;
  cancelOrder(id: number): Promise<CleaningOrder>;
}

export { ApiError as CleaningApiError } from "./ApiError";
