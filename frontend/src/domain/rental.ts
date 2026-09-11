export type RentalPropertyStatus = "DRAFT" | "PUBLISHED" | "ARCHIVED";

export type RentalAmenity =
  | "WIFI"
  | "AIR_CONDITIONING"
  | "WASHING_MACHINE"
  | "DISHWASHER"
  | "BALCONY"
  | "SEA_VIEW"
  | "POOL"
  | "PARKING"
  | "ELEVATOR"
  | "WORKSPACE"
  | "TV"
  | "KITCHEN";

export interface RentalConfiguration {
  minStayDays: number;
  longTermMinDays: number;
  longTermDiscountRate: number;
  maxStayDays: number;
  bookingStartMonthsAhead: number;
  maxActiveBookingsPerCustomer: number;
  today: string;
  latestCheckInDate: string;
}

export interface RentalPropertyMedia {
  id: number;
  mediaAssetId: number;
  sortOrder: number;
  cover: boolean;
  url: string;
  cardUrl?: string;
  thumbnailUrl?: string;
}

export interface RentalProperty {
  id: number;
  slug: string | null;
  titleRu: string | null;
  titleEn: string | null;
  descriptionEn: string | null;
  area: string | null;
  address: string | null;
  apartmentNumber: string | null;
  bedrooms: number | null;
  beds: number | null;
  bathrooms: number | null;
  maxGuests: number | null;
  areaSqm: number | null;
  floor: number | null;
  baseDailyPrice: number | null;
  currency: string | null;
  displayOrder: number;
  status: RentalPropertyStatus;
  amenities: RentalAmenity[];
  media: RentalPropertyMedia[];
  createdAt: string;
  updatedAt: string;
}

export interface RentalAvailabilityRange {
  startDate: string;
  endDate: string;
}

export interface RentalAvailability {
  propertyId: number;
  fromDate: string;
  toDate: string;
  unavailableRanges: RentalAvailabilityRange[];
}

export interface RentalBookingProperty {
  id: number;
  slug: string;
  titleRu: string | null;
  titleEn: string;
  area: string;
}

export type RentalTermType = "DATE_RANGE" | "MONTHLY";

export interface DateRangeRentalTermCriteria {
  termType: "DATE_RANGE";
  checkInDate: string;
  checkOutDate: string;
  months?: never;
  guests: number;
}

export interface MonthlyRentalTermCriteria {
  termType: "MONTHLY";
  checkInDate: string;
  checkOutDate?: never;
  months: number;
  guests: number;
}

export type RentalTermCriteria =
  | DateRangeRentalTermCriteria
  | MonthlyRentalTermCriteria;

export interface RentalQuote {
  property: RentalBookingProperty;
  criteria: RentalSearchCriteria;
  price: RentalSearchPrice;
}

export type CreateRentalBookingRequest = RentalTermCriteria & {
  propertyId: number;
  phone: string;
  comment?: string;
  expectedTotalPrice: number;
  expectedCurrency: string;
  searchExecutionId?: string;
};

export type RentalSearchMode = RentalTermType | "BROWSE_ALL";

export type RentalSearchRequest =
  | RentalTermCriteria
  | { termType?: never; view: "all" };

export interface RentalSearchCriteria {
  mode: RentalSearchMode;
  termType: RentalTermType | null;
  checkInDate: string | null;
  checkOutDate: string | null;
  rentalMonths: number | null;
  durationDays: number | null;
  guests: number | null;
}

export interface RentalSearchPrice {
  baseDailyPrice: number;
  baseMonthlyPrice: number | null;
  monthlyPrice: number | null;
  baseAmount: number;
  discountRate: number;
  discountAmount: number;
  longTermDiscountApplied: boolean;
  totalPrice: number;
  currency: string;
  rentalMonths: number | null;
  durationDays: number;
}

export interface RentalSearchProperty {
  id: number;
  slug: string;
  titleRu: string | null;
  titleEn: string;
  descriptionEn: string | null;
  area: string;
  bedrooms: number;
  maxGuests: number;
  areaSqm: number;
  baseDailyPrice: number;
  currency: string;
  coverUrl: string | null;
  price: RentalSearchPrice | null;
}

export interface RentalSearchResponse {
  searchExecutionId: string;
  criteria: RentalSearchCriteria;
  calculatedAt: string;
  properties: RentalSearchProperty[];
  nextCursor: string | null;
  hasMore: boolean;
}

export type RentalBookingStatus =
  | "CONFIRMED"
  | "CANCELLED_BY_CUSTOMER"
  | "CANCELLED_BY_ADMIN"
  | "COMPLETED";

export interface RentalBooking {
  id: number;
  property: RentalBookingProperty;
  termType: RentalTermType;
  checkInDate: string;
  checkOutDate: string;
  rentalMonths: number | null;
  durationDays: number;
  customerName: string;
  phone: string;
  guests: number;
  comment?: string | null;
  baseDailyPriceSnapshot: number;
  baseMonthlyPriceSnapshot: number | null;
  monthlyPriceSnapshot: number | null;
  longTermDiscountRateSnapshot: number;
  discountAmount: number;
  totalPrice: number;
  currency: string;
  status: RentalBookingStatus;
  createdAt: string;
  cancelledAt?: string | null;
  cancellationReason?: string | null;
  completedAt?: string | null;
}

export type RentalCleaningBenefitStatus =
  | "AVAILABLE"
  | "RESERVED"
  | "REDEEMED"
  | "REVOKED";

export interface RentalCleaningContext {
  rentalBookingId: number;
  address: string;
  phone: string;
  checkOutDate: string;
  earliestBenefitCleaningDate: string;
  benefitStatus: RentalCleaningBenefitStatus | null;
  promoCode: string | null;
  cleaningFlowAvailable: boolean;
}

export type RentalTransferContextType = "ARRIVAL" | "CHECKOUT";
export type RentalTransferContextAvailability = "BOOKABLE" | "AVAILABLE_LATER";

export interface RentalTransferBenefit {
  type: "RENTAL_FIRST_TRANSFER";
  discountRate: number;
}

export interface RentalTransferContextOption {
  context: RentalTransferContextType;
  availability: RentalTransferContextAvailability;
  direction: "TO_AIRPORT" | "FROM_AIRPORT";
  suggestedDate: string;
  address: string;
  availableFromDate: string | null;
  benefit: RentalTransferBenefit | null;
}

export interface RentalTransferContext {
  rentalBookingId: number;
  transferFlowAvailable: boolean;
  options: RentalTransferContextOption[];
}

export interface RentalTransferPrefill {
  rentalBookingId: number;
  context: RentalTransferContextType;
  direction: "TO_AIRPORT" | "FROM_AIRPORT";
  suggestedDate: string;
  address: string;
  benefit: RentalTransferBenefit | null;
}

export interface UpdateRentalPropertyRequest {
  titleRu: string | null;
  titleEn: string | null;
  descriptionEn: string | null;
  area: string | null;
  address: string | null;
  apartmentNumber: string | null;
  bedrooms: number | null;
  beds: number | null;
  bathrooms: number | null;
  maxGuests: number | null;
  areaSqm: number | null;
  floor: number | null;
  baseDailyPrice: number | null;
  currency: string | null;
  amenities: RentalAmenity[];
}

export type RentalOccupancyType =
  | "BOOKING"
  | "OWNER_BLOCK"
  | "EXTERNAL_BOOKING"
  | "MAINTENANCE";

export interface RentalOccupancy {
  id: number;
  propertyId: number;
  startDate: string;
  endDate: string;
  type: RentalOccupancyType;
  bookingId?: number | null;
  note?: string | null;
  createdAt: string;
  createdByAdminId?: number | null;
}

export interface UpsertRentalOccupancyRequest {
  startDate: string;
  endDate: string;
  type: Exclude<RentalOccupancyType, "BOOKING">;
  note?: string | null;
}

export interface AdminRentalBooking {
  customerId: number;
  communicationIdentityId: number;
  booking: RentalBooking;
}

export type RentalBookingTimeFilter = "ALL" | "FUTURE" | "PAST";

export interface AdminRentalBookingFilters {
  status?: RentalBookingStatus;
  propertyId?: number;
  time?: RentalBookingTimeFilter;
}

export interface AdminCancelRentalBookingRequest {
  reason?: string | null;
  keepDatesUnavailable: boolean;
}

export interface RentalAdminNotificationPreference {
  telegramLinked: boolean;
  telegramEnabled: boolean;
  writeAccessAllowed: boolean;
  telegramUsername: string | null;
}
