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
  | "COMPLETED"
  | "CANCELLED_BY_CUSTOMER"
  | "CANCELLED_BY_CLEANER";

export type OrderActorType = "CUSTOMER" | "CLEANER" | "SYSTEM";

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
