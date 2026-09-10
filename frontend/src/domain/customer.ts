export interface CustomerProfile {
  phone: string | null;
}

export type ExternalIdentityProvider = "GOOGLE" | "APPLE" | "TELEGRAM";

export interface AccountIdentity {
  identityId: number | null;
  provider: ExternalIdentityProvider;
  issuer: string | null;
  linked: boolean;
  username: string | null;
  writeAccessAllowed: boolean;
}

export interface AccountIdentities {
  identities: AccountIdentity[];
}

export interface AccountLinkInitiated {
  id: string;
  deepLink: string;
  expiresAt: string;
}

export const CUSTOMER_NOTIFICATION_TYPES = [
  "CLEANING_ORDER_ACCEPTED",
  "CLEANING_ORDER_CANCELLED",
  "CLEANING_ORDER_COMPLETED",
  "CLEANING_ONSITE_ISSUE_REPORTED",
  "RENTAL_BOOKING_CONFIRMED",
  "RENTAL_BOOKING_CANCELLED",
  "RENTAL_ADMIN_BOOKING_CHANGED",
  "TRANSFER_REQUESTED",
  "TRANSFER_CONFIRMED",
  "TRANSFER_REJECTED",
  "TRANSFER_CANCELLED",
  "TRANSFER_COMPLETED",
  "TRANSFER_ADMIN_REQUESTED",
  "SUPPORT_CASE_CREATED",
  "REFERRAL_UNLOCKED",
  "RENTAL_CLEANING_BENEFIT_AVAILABLE",
  "CLEANING_REPEAT_REMINDER",
  "RENTAL_CHECKOUT_TRANSFER_REMINDER",
  "TRANSFER_UPCOMING_REMINDER",
] as const;

export type CustomerNotificationType = (typeof CUSTOMER_NOTIFICATION_TYPES)[number];

export interface CustomerNotification {
  id: number;
  type: CustomerNotificationType;
  action: import("./action").ActionTarget;
  createdAt: string;
  readAt: string | null;
}

export interface CustomerNotificationPage {
  items: CustomerNotification[];
  nextCursor: string | null;
  hasMore: boolean;
}

export interface CustomerActivityItem {
  service: import("./platformService").PlatformService;
  entityId: number;
  status: string;
  titleRu: string;
  titleEn: string;
  subtitleRu: string;
  subtitleEn: string;
  scheduledDate: string;
  scheduledEndDate: string | null;
  scheduledTime: string | null;
  occurredAt: string;
  money: { amount: string; currency: string };
  action: import("./action").ActionTarget;
}

export interface CustomerActivity {
  activeAndUpcoming: CustomerActivityItem[];
  history: CustomerActivityItem[];
}

export type CustomerHomePrimaryActionType =
  | "RENTAL_TRANSFER_ARRIVAL"
  | "RENTAL_TRANSFER_CHECKOUT"
  | "RENTAL_CLEANING";

export interface CustomerHomePrimaryAction {
  type: CustomerHomePrimaryActionType;
  sourceService: import("./platformService").PlatformService;
  sourceEntityId: number;
  targetService: import("./platformService").PlatformService;
  relevantDate: string;
  eligibleFrom: string | null;
  expiresOn: string | null;
  action: import("./action").ActionTarget;
  benefit: import("./rental").RentalTransferBenefit | null;
}

export interface CustomerHomeRepeatOpportunity {
  service: import("./platformService").PlatformService;
  sourceEntityId: number;
  sourceCompletedAt: string;
  action: import("./action").ActionTarget;
}

export interface CustomerHome {
  hasActivity: boolean;
  activeTransaction: CustomerActivityItem | null;
  activeTransactionCount: number;
  primaryAction: CustomerHomePrimaryAction | null;
  repeatOpportunity: CustomerHomeRepeatOpportunity | null;
}
