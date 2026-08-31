export interface CustomerProfile {
  phone: string | null;
}

export type ExternalIdentityProvider = "GOOGLE" | "TELEGRAM";

export interface AccountIdentity {
  provider: ExternalIdentityProvider;
  linked: boolean;
  username: string | null;
  writeAccessAllowed: boolean;
}

export interface AccountIdentities {
  identities: AccountIdentity[];
}

export interface AccountLinkInitiated {
  deepLink: string;
  expiresAt: string;
}

export type CustomerNotificationType =
  | "CLEANING_ORDER_ACCEPTED"
  | "CLEANING_ORDER_CANCELLED"
  | "CLEANING_ORDER_COMPLETED"
  | "CLEANING_ONSITE_ISSUE_REPORTED"
  | "RENTAL_BOOKING_CONFIRMED"
  | "RENTAL_BOOKING_CANCELLED"
  | "TRANSFER_REQUESTED"
  | "TRANSFER_CONFIRMED"
  | "TRANSFER_REJECTED"
  | "TRANSFER_CANCELLED"
  | "TRANSFER_COMPLETED"
  | "TRANSFER_ADMIN_REQUESTED"
  | "REFERRAL_UNLOCKED"
  | "RENTAL_CLEANING_BENEFIT_AVAILABLE";

export interface CustomerNotification {
  id: number;
  type: CustomerNotificationType;
  targetPath: string;
  createdAt: string;
  readAt: string | null;
}

export interface CustomerNotificationPage {
  content: CustomerNotification[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
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
  amount: number;
  currency: string;
  targetPath: string;
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
  targetPath: string;
}

export interface CustomerHomeRepeatOpportunity {
  service: import("./platformService").PlatformService;
  sourceEntityId: number;
  sourceCompletedAt: string;
  targetPath: string;
}

export interface CustomerHome {
  hasActivity: boolean;
  activeTransaction: CustomerActivityItem | null;
  activeTransactionCount: number;
  primaryAction: CustomerHomePrimaryAction | null;
  repeatOpportunity: CustomerHomeRepeatOpportunity | null;
}
