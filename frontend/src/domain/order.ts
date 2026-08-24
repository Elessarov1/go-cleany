export type ServiceArea = "MAHMUTLAR" | "KARGICAK" | "KESTEL";

export type ApartmentType =
  | "STUDIO"
  | "ONE_PLUS_ONE"
  | "TWO_PLUS_ONE"
  | "THREE_PLUS_ONE"
  | "FOUR_PLUS_ONE";

export type CleaningType = "REGULAR" | "DEEP";

export type AcquisitionSource = "ORGANIC" | "CUSTOMER_REFERRAL" | "PARTNER";

export type CustomerDiscountType =
  | "NONE"
  | "FRIEND_REFERRAL"
  | "REFERRER_REWARD"
  | "PARTNER_REFERRAL"
  | "RENTAL_CHECKOUT_PROMO";

export type CleaningOrderStatus =
  | "NEW"
  | "ACCEPTED"
  | "AWAITING_REPORT"
  | "ONSITE_ISSUE_REPORTED"
  | "COMPLETED"
  | "REJECTED"
  | "CANCELLED";

export interface CreateCleaningOrderRequest {
  area: ServiceArea;
  address: string;
  apartmentType: ApartmentType;
  duplex: boolean;
  cleaningType: CleaningType;
  requestedDate: string;
  phone: string;
  comment?: string;
  referralCode?: string;
  rentalCleaningPromoCode?: string;
}

export interface CleaningOrderQuoteRequest {
  apartmentType: ApartmentType;
  duplex: boolean;
  cleaningType: CleaningType;
  referralCode?: string;
  requestedDate?: string;
  rentalCleaningPromoCode?: string;
}

export interface CleaningOrderQuote {
  basePrice: number;
  customerDiscount: number;
  finalCustomerPrice: number;
  customerDiscountType: CustomerDiscountType;
  currency: string;
}

export interface CleaningOrder {
  id: number;
  communicationIdentityId: number;
  customerName: string;
  phone: string;
  area: ServiceArea;
  address: string;
  apartmentType: ApartmentType;
  duplex: boolean;
  cleaningType: CleaningType;
  price: number;
  basePrice: number;
  customerDiscount: number;
  finalCustomerPrice: number;
  customerDiscountType: CustomerDiscountType;
  currency: string;
  requestedDate: string;
  customerComment?: string;
  cleanerComment?: string;
  cleanerTelegramUserId?: number;
  status: CleaningOrderStatus;
  createdAt: string;
  acceptedAt?: string;
  completedAt?: string;
  photoCount?: number;
}

export interface ReferralSummary {
  referralCode?: string;
  availableRewards: number;
  referralProgramUnlocked: boolean;
}
