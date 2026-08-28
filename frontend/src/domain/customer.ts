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
