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
}

export interface RentalPropertyMedia {
  id: number;
  mediaAssetId: number;
  sortOrder: number;
  cover: boolean;
  url: string;
}

export interface RentalProperty {
  id: number;
  slug: string | null;
  titleRu: string | null;
  titleEn: string | null;
  descriptionEn: string | null;
  area: string | null;
  address: string | null;
  bedrooms: number | null;
  beds: number | null;
  bathrooms: number | null;
  maxGuests: number | null;
  areaSqm: number | null;
  floor: number | null;
  baseDailyPrice: number | null;
  currency: string | null;
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

export interface DateRangeRentalBookingQuoteRequest {
  propertyId: number;
  termType: "DATE_RANGE";
  checkInDate: string;
  checkOutDate: string;
  months?: never;
}

export interface MonthlyRentalBookingQuoteRequest {
  propertyId: number;
  termType: "MONTHLY";
  checkInDate: string;
  checkOutDate?: never;
  months: number;
}

export type RentalBookingQuoteRequest =
  | DateRangeRentalBookingQuoteRequest
  | MonthlyRentalBookingQuoteRequest;

export interface RentalBookingQuote {
  property: RentalBookingProperty;
  termType: RentalTermType;
  checkInDate: string;
  checkOutDate: string;
  rentalMonths: number | null;
  durationDays: number;
  baseDailyPrice: number;
  monthlyPrice: number | null;
  baseAmount: number;
  longTermDiscountApplied: boolean;
  discountRate: number;
  discountAmount: number;
  totalPrice: number;
  currency: string;
}

export type CreateRentalBookingRequest = RentalBookingQuoteRequest & {
  guests: number;
  phone: string;
  comment?: string;
};

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

export interface RentalTransferContextOption {
  context: RentalTransferContextType;
  availability: RentalTransferContextAvailability;
  direction: "TO_AIRPORT" | "FROM_AIRPORT";
  suggestedDate: string;
  address: string;
  availableFromDate: string | null;
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
}

export interface UpdateRentalPropertyRequest {
  titleRu: string | null;
  titleEn: string | null;
  descriptionEn: string | null;
  area: string | null;
  address: string | null;
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
