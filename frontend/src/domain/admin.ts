import type {
  CleaningOrder,
  CleaningOrderStatus,
  CleaningType,
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
  photoCount: number;
  events: AdminOrderEvent[];
}
