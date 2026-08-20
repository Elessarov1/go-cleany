import type {
  AcquisitionSource,
  CleaningOrder,
  CleaningOrderStatus,
  CleaningType,
  CustomerDiscountType,
  ServiceArea,
} from "./order";

export type OrderEventType =
  | "IMPORTED"
  | "CREATED"
  | "ACCEPTED"
  | "REPORT_STARTED"
  | "PHOTO_ADDED"
  | "COMMENT_UPDATED"
  | "ONSITE_ISSUE_REPORTED"
  | "ISSUE_PHOTO_ADDED"
  | "ISSUE_REPORT_SUBMITTED"
  | "ISSUE_CUSTOMER_NOTIFIED"
  | "ISSUE_RESOLVED"
  | "COMPLETED"
  | "CANCELLED_BY_CUSTOMER"
  | "CANCELLED_BY_CLEANER";

export type OrderActorType = "CUSTOMER" | "CLEANER" | "ADMIN" | "SYSTEM";

export type OnsiteIssueReason =
  | "APARTMENT_SIZE_MISMATCH"
  | "CLEANING_TYPE_MISMATCH"
  | "HEAVY_CONTAMINATION"
  | "ACCESS_PROBLEM"
  | "ADDRESS_MISMATCH"
  | "OTHER";

export interface AdminIssuePhoto {
  id: number;
  contentType: string;
  sizeBytes: number;
  sha256: string;
  createdAt: string;
}

export interface AdminOnsiteIssue {
  id: number;
  reason: OnsiteIssueReason;
  cleanerTelegramUserId: number;
  reportedAt: string;
  comment: string;
  photos: AdminIssuePhoto[];
  resolvedAt: string | null;
  resolvedBy: number | null;
  resolutionComment: string | null;
}

export interface AdminStats {
  totalOrders: number;
  ordersToday: number;
  newOrders: number;
  activeOrders: number;
  completedOrders: number;
  cancelledOrders: number;
  completedAmount: number;
  currency: string;
}

export interface AdminOrderSummary {
  id: number;
  customerName: string;
  area: ServiceArea;
  cleaningType: CleaningType;
  requestedDate: string;
  price: number;
  currency: string;
  status: CleaningOrderStatus;
  cleanerTelegramUserId?: number;
  createdAt: string;
}

export interface AdminDashboard {
  stats: AdminStats;
  recentOrders: AdminOrderSummary[];
}

export interface AdminOrderEvent {
  id: number;
  eventType: OrderEventType;
  fromStatus?: CleaningOrderStatus;
  toStatus: CleaningOrderStatus;
  actorType: OrderActorType;
  actorTelegramUserId?: number;
  details?: string;
  occurredAt: string;
}

export interface AdminOrderDetails {
  order: CleaningOrder;
  financial: AdminOrderFinancial;
  photoCount: number;
  onsiteIssue: AdminOnsiteIssue | null;
  events: AdminOrderEvent[];
}

export interface AdminOrderFinancial {
  basePrice: number;
  commissionRate: number;
  baseCommission: number;
  customerDiscount: number;
  partnerPayout: number;
  finalCustomerPrice: number;
  platformNet: number;
  acquisitionSource: AcquisitionSource;
  customerDiscountType: CustomerDiscountType;
}

export type PartnerPayoutStatus = "PAYABLE" | "PAID";

export interface ReferralPartner {
  id: number;
  name: string;
  referralCode: string;
  active: boolean;
  createdAt: string;
}

export interface PartnerPayout {
  id: number;
  partnerId: number;
  partnerName: string;
  sourceOrderId: number;
  amount: number;
  currency: string;
  status: PartnerPayoutStatus;
  createdAt: string;
  paidAt?: string;
}

export interface AdminReferralOverview {
  partners: ReferralPartner[];
  payouts: PartnerPayout[];
}
