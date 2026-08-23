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
  descriptionRu: string | null;
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
  titleRu: string;
  titleEn: string;
  area: string;
}

export interface RentalBookingQuoteRequest {
  propertyId: number;
  checkInDate: string;
  checkOutDate: string;
}

export interface RentalBookingQuote {
  property: RentalBookingProperty;
  checkInDate: string;
  checkOutDate: string;
  durationDays: number;
  baseDailyPrice: number;
  baseAmount: number;
  longTermDiscountApplied: boolean;
  discountRate: number;
  discountAmount: number;
  totalPrice: number;
  currency: string;
}

export interface CreateRentalBookingRequest extends RentalBookingQuoteRequest {
  guests: number;
  phone: string;
  comment?: string;
}

export type RentalBookingStatus =
  | "CONFIRMED"
  | "CANCELLED_BY_CUSTOMER"
  | "CANCELLED_BY_ADMIN"
  | "COMPLETED";

export interface RentalBooking {
  id: number;
  property: RentalBookingProperty;
  checkInDate: string;
  checkOutDate: string;
  durationDays: number;
  customerName: string;
  phone: string;
  guests: number;
  comment?: string | null;
  baseDailyPriceSnapshot: number;
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

export interface UpdateRentalPropertyRequest {
  slug: string | null;
  titleRu: string | null;
  titleEn: string | null;
  descriptionRu: string | null;
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
