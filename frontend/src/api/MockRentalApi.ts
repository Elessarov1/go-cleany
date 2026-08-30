import type {
  AdminCancelRentalBookingRequest,
  AdminRentalBooking,
  AdminRentalBookingFilters,
  CreateRentalBookingRequest,
  RentalAvailability,
  RentalAvailabilityRange,
  RentalBooking,
  RentalCleaningContext,
  RentalTransferContext,
  RentalTransferContextType,
  RentalTransferPrefill,
  RentalBookingProperty,
  RentalBookingQuote,
  RentalBookingQuoteRequest,
  RentalConfiguration,
  RentalAdminNotificationPreference,
  RentalOccupancy,
  RentalProperty,
  UpdateRentalPropertyRequest,
  UpsertRentalOccupancyRequest,
} from "../domain/rental";
import type { TransferBooking, TransferDirection } from "../domain/transfer";
import type { Platform } from "../platform/Platform";
import { addMonthsToInputValue } from "../utils/format";
import { ApiError } from "./ApiError";
import type { RentalApi } from "./RentalApi";

const STORAGE_KEY = "cleany.mock.rental-bookings.v2";
const TRANSFER_STORAGE_KEY = "cleany.mock.transfer-bookings.v1";
const DAY_MS = 24 * 60 * 60 * 1000;

export const mockRentalConfiguration: RentalConfiguration = {
  minStayDays: 7,
  longTermMinDays: 30,
  longTermDiscountRate: 0.1,
  maxStayDays: 365,
  bookingStartMonthsAhead: 6,
  maxActiveBookingsPerCustomer: 3,
};

function dateFromToday(offset: number): string {
  const date = new Date();
  date.setHours(12, 0, 0, 0);
  date.setDate(date.getDate() + offset);
  return date.toISOString().slice(0, 10);
}

function addDays(value: string, offset: number): string {
  const date = new Date(`${value}T12:00:00Z`);
  date.setUTCDate(date.getUTCDate() + offset);
  return date.toISOString().slice(0, 10);
}

function durationDays(start: string, end: string): number {
  return Math.round((Date.parse(`${end}T12:00:00Z`) - Date.parse(`${start}T12:00:00Z`)) / DAY_MS);
}

function roundMoney(value: number): number {
  return Math.round((value + Number.EPSILON) * 100) / 100;
}

function normalizeAddress(value: string): string {
  return value.trim().replace(/\s+/g, " ").toLocaleLowerCase();
}

const properties: RentalProperty[] = [
  {
    id: 201,
    slug: "kestel-sea-breeze",
    titleRu: "Sea Breeze Residence",
    titleEn: "Sea Breeze Residence",
    descriptionEn: "A bright apartment near the sea with a spacious living room and a balcony for relaxed short or extended stays.",
    area: "Кестель",
    address: "Isa Küçülmez Cd., Kestel",
    bedrooms: 2,
    beds: 3,
    bathrooms: 2,
    maxGuests: 5,
    areaSqm: 105,
    floor: 5,
    baseDailyPrice: 2100,
    currency: "TRY",
    status: "PUBLISHED",
    amenities: ["WIFI", "AIR_CONDITIONING", "BALCONY", "SEA_VIEW", "POOL", "ELEVATOR", "KITCHEN", "WASHING_MACHINE"],
    media: [
      { id: 301, mediaAssetId: 401, sortOrder: 0, cover: true, url: "/assets/rent/sea-breeze-living.svg" },
      { id: 302, mediaAssetId: 402, sortOrder: 1, cover: false, url: "/assets/rent/sea-breeze-bedroom.svg" },
      { id: 303, mediaAssetId: 403, sortOrder: 2, cover: false, url: "/assets/rent/sea-breeze-balcony.svg" },
    ],
    createdAt: "2026-08-01T10:00:00Z",
    updatedAt: "2026-08-20T10:00:00Z",
  },
  {
    id: 202,
    slug: "mahmurlar-calm-home",
    titleRu: "Calm Home Mahmutlar",
    titleEn: "Calm Home Mahmutlar",
    descriptionEn: "A calm apartment with a workspace and everything needed for stays from one week to several months.",
    area: "Махмутлар",
    address: "Barbaros Cd., Mahmutlar",
    bedrooms: 1,
    beds: 2,
    bathrooms: 1,
    maxGuests: 3,
    areaSqm: 68,
    floor: 3,
    baseDailyPrice: 1600,
    currency: "TRY",
    status: "PUBLISHED",
    amenities: ["WIFI", "AIR_CONDITIONING", "BALCONY", "ELEVATOR", "WORKSPACE", "TV", "KITCHEN", "WASHING_MACHINE"],
    media: [
      { id: 304, mediaAssetId: 404, sortOrder: 0, cover: true, url: "/assets/rent/calm-home-living.svg" },
      { id: 305, mediaAssetId: 405, sortOrder: 1, cover: false, url: "/assets/rent/calm-home-bedroom.svg" },
    ],
    createdAt: "2026-08-02T10:00:00Z",
    updatedAt: "2026-08-19T10:00:00Z",
  },
];

const manualUnavailable: Record<number, RentalAvailabilityRange[]> = {
  201: [
    { startDate: dateFromToday(12), endDate: dateFromToday(18) },
    { startDate: dateFromToday(41), endDate: dateFromToday(47) },
  ],
  202: [{ startDate: dateFromToday(24), endDate: dateFromToday(31) }],
};

let manualOccupancies: RentalOccupancy[] = Object.entries(manualUnavailable).flatMap(
  ([propertyId, ranges], propertyIndex) => ranges.map((range, rangeIndex) => ({
    id: 700 + propertyIndex * 10 + rangeIndex,
    propertyId: Number(propertyId),
    startDate: range.startDate,
    endDate: range.endDate,
    type: "OWNER_BLOCK" as const,
    note: null,
    createdAt: new Date().toISOString(),
    createdByAdminId: 1,
  })),
);

function propertySummary(property: RentalProperty): RentalBookingProperty {
  if (!property.slug || !property.titleEn || !property.area) {
    throw new ApiError("Published rental property is incomplete", 500);
  }
  return {
    id: property.id,
    slug: property.slug,
    titleRu: property.titleRu,
    titleEn: property.titleEn,
    area: property.area,
  };
}

function seedBooking(): RentalBooking {
  const property = properties[1]!;
  const checkInDate = dateFromToday(58);
  const checkOutDate = dateFromToday(72);
  const duration = durationDays(checkInDate, checkOutDate);
  return {
    id: 501,
    property: propertySummary(property),
    termType: "DATE_RANGE",
    checkInDate,
    checkOutDate,
    rentalMonths: null,
    durationDays: duration,
    customerName: "Alex",
    phone: "+90 555 123 45 67",
    guests: 2,
    comment: "Позвонить за час до заселения",
    baseDailyPriceSnapshot: property.baseDailyPrice!,
    monthlyPriceSnapshot: null,
    longTermDiscountRateSnapshot: 0,
    discountAmount: 0,
    totalPrice: property.baseDailyPrice! * duration,
    currency: property.currency!,
    status: "CONFIRMED",
    createdAt: new Date().toISOString(),
  };
}

function readBookings(): RentalBooking[] {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored === null ? [seedBooking()] : JSON.parse(stored) as RentalBooking[];
  } catch {
    return [seedBooking()];
  }
}

function writeBookings(bookings: RentalBooking[]): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(bookings));
}

async function simulateNetwork<T>(value: T): Promise<T> {
  await new Promise((resolve) => window.setTimeout(resolve, 160));
  return value;
}

function overlaps(range: RentalAvailabilityRange, start: string, end: string): boolean {
  return range.startDate < end && range.endDate > start;
}

export class MockRentalApi implements RentalApi {
  private adminNotificationPreference: RentalAdminNotificationPreference = {
    telegramLinked: true,
    telegramEnabled: true,
    writeAccessAllowed: true,
    telegramUsername: "browser_preview",
  };

  constructor(private readonly platform: Platform) {}

  getConfiguration(): Promise<RentalConfiguration> {
    return simulateNetwork(mockRentalConfiguration);
  }

  getProperties(): Promise<RentalProperty[]> {
    return simulateNetwork(properties.filter((property) => property.status === "PUBLISHED"));
  }

  getProperty(slug: string): Promise<RentalProperty> {
    const property = properties.find((item) => item.slug === slug && item.status === "PUBLISHED");
    return property
      ? simulateNetwork(property)
      : Promise.reject(new ApiError("Rental property not found", 404, "rental_property_not_found"));
  }

  getAvailability(
    propertyId: number,
    fromDate: string,
    toDate: string,
  ): Promise<RentalAvailability> {
    const bookingRanges = readBookings()
      .filter((booking) => booking.property.id === propertyId && booking.status === "CONFIRMED")
      .map((booking) => ({ startDate: booking.checkInDate, endDate: booking.checkOutDate }));
    const manualRanges = manualOccupancies
      .filter((occupancy) => occupancy.propertyId === propertyId)
      .map(({ startDate, endDate }) => ({ startDate, endDate }));
    const unavailableRanges = [...manualRanges, ...bookingRanges]
      .filter((range) => overlaps(range, fromDate, toDate))
      .sort((first, second) => first.startDate.localeCompare(second.startDate));
    return simulateNetwork({ propertyId, fromDate, toDate, unavailableRanges });
  }

  async quoteBooking(request: RentalBookingQuoteRequest): Promise<RentalBookingQuote> {
    const property = properties.find((item) => item.id === request.propertyId && item.status === "PUBLISHED");
    if (!property || property.baseDailyPrice === null || !property.currency) {
      throw new ApiError("Rental property is unavailable", 404, "rental_property_not_available");
    }
    const checkOutDate = request.termType === "MONTHLY"
      ? addMonthsToInputValue(request.checkInDate, request.months)
      : request.checkOutDate;
    const duration = durationDays(request.checkInDate, checkOutDate);
    if (request.termType === "DATE_RANGE") {
      if (duration < mockRentalConfiguration.minStayDays) {
        throw new ApiError("Minimum stay not met", 400, "rental_min_stay_not_met");
      }
      if (duration >= mockRentalConfiguration.longTermMinDays) {
        throw new ApiError("Maximum date range exceeded", 400, "rental_max_stay_exceeded");
      }
    } else if (duration > mockRentalConfiguration.maxStayDays) {
      throw new ApiError("Maximum stay exceeded", 400, "rental_max_stay_exceeded");
    }
    const availability = await this.getAvailability(
      request.propertyId,
      request.checkInDate,
      checkOutDate,
    );
    if (availability.unavailableRanges.some((range) => overlaps(range, request.checkInDate, checkOutDate))) {
      throw new ApiError("Dates are unavailable", 409, "dates_not_available");
    }
    const monthlyPrice = request.termType === "MONTHLY"
      ? roundMoney(property.baseDailyPrice * 30 * (1 - mockRentalConfiguration.longTermDiscountRate))
      : null;
    const baseAmount = request.termType === "MONTHLY"
      ? roundMoney(property.baseDailyPrice * 30 * request.months)
      : roundMoney(property.baseDailyPrice * duration);
    const discountRate = request.termType === "MONTHLY"
      ? mockRentalConfiguration.longTermDiscountRate
      : 0;
    const totalPrice = request.termType === "MONTHLY"
      ? roundMoney(monthlyPrice! * request.months)
      : baseAmount;
    const discountAmount = roundMoney(baseAmount - totalPrice);
    return simulateNetwork({
      property: propertySummary(property),
      termType: request.termType,
      checkInDate: request.checkInDate,
      checkOutDate,
      rentalMonths: request.termType === "MONTHLY" ? request.months : null,
      durationDays: duration,
      baseDailyPrice: property.baseDailyPrice,
      monthlyPrice,
      baseAmount,
      longTermDiscountApplied: discountRate > 0,
      discountRate,
      discountAmount,
      totalPrice,
      currency: property.currency,
    });
  }

  async createBooking(request: CreateRentalBookingRequest): Promise<RentalBooking> {
    if (!/^\+\s*\d/.test(request.phone.trim())) {
      throw new ApiError("Invalid phone", 400, "invalid_phone_number", { phone: "invalid" });
    }
    const activeBookings = readBookings().filter(
      (booking) => booking.status === "CONFIRMED" && booking.checkOutDate > dateFromToday(0),
    );
    if (activeBookings.length >= mockRentalConfiguration.maxActiveBookingsPerCustomer) {
      throw new ApiError("Active booking limit exceeded", 409, "rental_active_booking_limit_exceeded");
    }
    const quote = await this.quoteBooking(request);
    const user = this.platform.getUser();
    const booking: RentalBooking = {
      id: Date.now(),
      property: quote.property,
      termType: quote.termType,
      checkInDate: quote.checkInDate,
      checkOutDate: quote.checkOutDate,
      rentalMonths: quote.rentalMonths,
      durationDays: quote.durationDays,
      customerName: user
        ? [user.firstName, user.lastName].filter(Boolean).join(" ")
        : "Browser preview",
      phone: request.phone.trim(),
      guests: request.guests,
      comment: request.comment?.trim() || null,
      baseDailyPriceSnapshot: quote.baseDailyPrice,
      monthlyPriceSnapshot: quote.monthlyPrice,
      longTermDiscountRateSnapshot: quote.discountRate,
      discountAmount: quote.discountAmount,
      totalPrice: quote.totalPrice,
      currency: quote.currency,
      status: "CONFIRMED",
      createdAt: new Date().toISOString(),
    };
    writeBookings([booking, ...readBookings()]);
    return simulateNetwork(booking);
  }

  getBookings(): Promise<RentalBooking[]> {
    return simulateNetwork(readBookings());
  }

  getBooking(id: number): Promise<RentalBooking> {
    const booking = readBookings().find((item) => item.id === id);
    return booking
      ? simulateNetwork(booking)
      : Promise.reject(new ApiError("Rental booking not found", 404, "rental_booking_not_found"));
  }

  async getCleaningContext(id: number): Promise<RentalCleaningContext> {
    const booking = await this.getBooking(id);
    const property = properties.find((item) => item.id === booking.property.id);
    if (!property?.address) {
      throw new ApiError("Rental property address is unavailable", 404);
    }
    const previewBenefit = new URLSearchParams(window.location.search)
      .get("scenario")?.toUpperCase() === "RENT_BENEFIT";
    const benefitAvailable = previewBenefit || (
      booking.status === "CONFIRMED"
      && booking.checkOutDate >= dateFromToday(0)
      && booking.checkOutDate <= dateFromToday(3)
    );
    return simulateNetwork({
      rentalBookingId: booking.id,
      address: property.address,
      phone: booking.phone,
      checkOutDate: booking.checkOutDate,
      earliestBenefitCleaningDate: addDays(booking.checkOutDate, -3),
      benefitStatus: benefitAvailable ? "AVAILABLE" : null,
      promoCode: benefitAvailable ? "RCPREVIEW1" : null,
      cleaningFlowAvailable: true,
    });
  }

  async getTransferContext(id: number): Promise<RentalTransferContext> {
    const booking = await this.getBooking(id);
    const property = properties.find((item) => item.id === booking.property.id);
    if (!property?.address) throw new ApiError("Rental property address is unavailable", 404);
    if (booking.status !== "CONFIRMED") {
      return simulateNetwork({ rentalBookingId: id, transferFlowAvailable: true, options: [] });
    }
    const earliest = dateFromToday(1);
    const latest = dateFromToday(183);
    const candidates: Array<[RentalTransferContextType, TransferDirection, string]> = [
      ["ARRIVAL", "FROM_AIRPORT", booking.checkInDate],
      ["CHECKOUT", "TO_AIRPORT", booking.checkOutDate],
    ];
    const options = candidates
      .filter(([, direction, date]) => date >= earliest
        && !this.hasMatchingTransfer(id, direction, date, property.address!))
      .map(([context, direction, suggestedDate]) => ({
        context,
        direction,
        suggestedDate,
        address: property.address!,
        availability: suggestedDate <= latest ? "BOOKABLE" as const : "AVAILABLE_LATER" as const,
        availableFromDate: suggestedDate <= latest ? null : addMonthsToInputValue(suggestedDate, -6),
      }));
    return simulateNetwork({ rentalBookingId: id, transferFlowAvailable: true, options });
  }

  async recordTransferContextShown(id: number, context: RentalTransferContextType): Promise<void> {
    await this.requireBookableTransferContext(id, context);
  }

  async getTransferPrefill(
    id: number,
    context: RentalTransferContextType,
  ): Promise<RentalTransferPrefill> {
    const option = await this.requireBookableTransferContext(id, context);
    return {
      rentalBookingId: id,
      context,
      direction: option.direction,
      suggestedDate: option.suggestedDate,
      address: option.address,
    };
  }

  async cancelBooking(id: number): Promise<RentalBooking> {
    const bookings = readBookings();
    const booking = bookings.find((item) => item.id === id);
    if (!booking) throw new ApiError("Rental booking not found", 404, "rental_booking_not_found");
    if (booking.status !== "CONFIRMED" || booking.checkInDate <= dateFromToday(0)) {
      throw new ApiError("Rental booking cannot be cancelled", 409, "rental_booking_cannot_be_cancelled");
    }
    const cancelled: RentalBooking = {
      ...booking,
      status: "CANCELLED_BY_CUSTOMER",
      cancelledAt: new Date().toISOString(),
    };
    writeBookings(bookings.map((item) => item.id === id ? cancelled : item));
    return simulateNetwork(cancelled);
  }

  getAdminProperties(): Promise<RentalProperty[]> {
    return simulateNetwork([...properties]);
  }

  createAdminProperty(): Promise<RentalProperty> {
    const timestamp = new Date().toISOString();
    const property: RentalProperty = {
      id: Date.now(), slug: null, titleRu: null, titleEn: null,
      descriptionEn: null, area: null, address: null,
      bedrooms: null, beds: null, bathrooms: null, maxGuests: null,
      areaSqm: null, floor: null, baseDailyPrice: null, currency: "TRY",
      status: "DRAFT", amenities: [], media: [], createdAt: timestamp, updatedAt: timestamp,
    };
    properties.unshift(property);
    return simulateNetwork(property);
  }

  getAdminProperty(id: number): Promise<RentalProperty> {
    const property = properties.find((item) => item.id === id);
    return property
      ? simulateNetwork(property)
      : Promise.reject(new ApiError("Rental property not found", 404, "rental_property_not_found"));
  }

  async updateAdminProperty(id: number, request: UpdateRentalPropertyRequest): Promise<RentalProperty> {
    const property = await this.getAdminProperty(id);
    const updated = {
      ...property,
      ...request,
      slug: property.slug ?? this.uniqueSlug(request.titleEn),
      currency: request.currency ?? property.currency ?? "TRY",
      updatedAt: new Date().toISOString(),
    };
    this.replaceProperty(updated);
    return simulateNetwork(updated);
  }

  async publishAdminProperty(id: number): Promise<RentalProperty> {
    const property = await this.getAdminProperty(id);
    const required = [
      property.slug, property.titleEn, property.descriptionEn, property.area,
      property.address, property.bedrooms, property.maxGuests, property.areaSqm,
      property.baseDailyPrice, property.currency,
    ];
    if (required.some((value) => value === null || value === "") || property.media.length === 0) {
      throw new ApiError("Rental property is incomplete", 409, "rental_property_incomplete");
    }
    const updated = { ...property, status: "PUBLISHED" as const, updatedAt: new Date().toISOString() };
    this.replaceProperty(updated);
    return simulateNetwork(updated);
  }

  async archiveAdminProperty(id: number): Promise<RentalProperty> {
    const property = await this.getAdminProperty(id);
    const updated = { ...property, status: "ARCHIVED" as const, updatedAt: new Date().toISOString() };
    this.replaceProperty(updated);
    return simulateNetwork(updated);
  }

  async addAdminPropertyMedia(id: number, file: File, cover: boolean): Promise<RentalProperty> {
    const property = await this.getAdminProperty(id);
    const mediaId = Date.now();
    const shouldCover = cover || property.media.length === 0;
    const media = property.media.map((item) => shouldCover ? { ...item, cover: false } : item);
    media.push({
      id: mediaId,
      mediaAssetId: mediaId + 1,
      sortOrder: media.length,
      cover: shouldCover,
      url: URL.createObjectURL(file),
    });
    const updated = { ...property, media, updatedAt: new Date().toISOString() };
    this.replaceProperty(updated);
    return simulateNetwork(updated);
  }

  async removeAdminPropertyMedia(id: number, mediaId: number): Promise<RentalProperty> {
    const property = await this.getAdminProperty(id);
    const removed = property.media.find((item) => item.id === mediaId);
    let media = property.media.filter((item) => item.id !== mediaId)
      .map((item, index) => ({ ...item, sortOrder: index }));
    if (removed?.cover && media[0]) media = media.map((item, index) => ({ ...item, cover: index === 0 }));
    const updated = { ...property, media, updatedAt: new Date().toISOString() };
    this.replaceProperty(updated);
    return simulateNetwork(updated);
  }

  async setAdminPropertyMediaCover(id: number, mediaId: number): Promise<RentalProperty> {
    const property = await this.getAdminProperty(id);
    const updated = {
      ...property,
      media: property.media.map((item) => ({ ...item, cover: item.id === mediaId })),
      updatedAt: new Date().toISOString(),
    };
    this.replaceProperty(updated);
    return simulateNetwork(updated);
  }

  async reorderAdminPropertyMedia(id: number, mediaIds: number[]): Promise<RentalProperty> {
    const property = await this.getAdminProperty(id);
    const media = mediaIds.map((mediaId, index) => {
      const item = property.media.find((candidate) => candidate.id === mediaId);
      if (!item) throw new ApiError("Rental property media not found", 404);
      return { ...item, sortOrder: index };
    });
    const updated = { ...property, media, updatedAt: new Date().toISOString() };
    this.replaceProperty(updated);
    return simulateNetwork(updated);
  }

  async getAdminPropertyMedia(id: number, mediaId: number): Promise<Blob> {
    const property = await this.getAdminProperty(id);
    const media = property.media.find((item) => item.id === mediaId);
    if (!media) throw new ApiError("Rental property media not found", 404);
    const response = await fetch(media.url);
    return response.blob();
  }

  getAdminOccupancies(id: number, fromDate: string, toDate: string): Promise<RentalOccupancy[]> {
    const bookingOccupancies: RentalOccupancy[] = readBookings()
      .filter((booking) => booking.property.id === id && booking.status === "CONFIRMED")
      .map((booking) => ({
        id: booking.id + 10_000,
        propertyId: id,
        startDate: booking.checkInDate,
        endDate: booking.checkOutDate,
        type: "BOOKING",
        bookingId: booking.id,
        note: null,
        createdAt: booking.createdAt,
        createdByAdminId: null,
      }));
    return simulateNetwork([...manualOccupancies, ...bookingOccupancies]
      .filter((item) => item.propertyId === id && overlaps(item, fromDate, toDate))
      .sort((first, second) => first.startDate.localeCompare(second.startDate)));
  }

  createAdminOccupancy(id: number, request: UpsertRentalOccupancyRequest): Promise<RentalOccupancy> {
    const occupancy: RentalOccupancy = {
      ...request, id: Date.now(), propertyId: id, bookingId: null,
      createdAt: new Date().toISOString(), createdByAdminId: 1,
    };
    manualOccupancies = [...manualOccupancies, occupancy];
    return simulateNetwork(occupancy);
  }

  updateAdminOccupancy(id: number, occupancyId: number, request: UpsertRentalOccupancyRequest): Promise<RentalOccupancy> {
    const current = manualOccupancies.find((item) => item.id === occupancyId && item.propertyId === id);
    if (!current) return Promise.reject(new ApiError("Rental occupancy not found", 404));
    const updated = { ...current, ...request };
    manualOccupancies = manualOccupancies.map((item) => item.id === occupancyId ? updated : item);
    return simulateNetwork(updated);
  }

  async deleteAdminOccupancy(id: number, occupancyId: number): Promise<void> {
    const current = manualOccupancies.find((item) => item.id === occupancyId && item.propertyId === id);
    if (!current) throw new ApiError("Rental occupancy not found", 404);
    manualOccupancies = manualOccupancies.filter((item) => item.id !== occupancyId);
    await simulateNetwork(undefined);
  }

  getAdminBookings(filters: AdminRentalBookingFilters = {}): Promise<AdminRentalBooking[]> {
    const today = dateFromToday(0);
    const bookings = readBookings().filter((booking) => {
      if (filters.status && booking.status !== filters.status) return false;
      if (filters.propertyId && booking.property.id !== filters.propertyId) return false;
      if (filters.time === "FUTURE" && booking.checkOutDate <= today) return false;
      if (filters.time === "PAST" && booking.checkOutDate > today) return false;
      return true;
    });
    return simulateNetwork(bookings.map((booking) => this.adminBooking(booking)));
  }

  async getAdminBooking(id: number): Promise<AdminRentalBooking> {
    return this.adminBooking(await this.getBooking(id));
  }

  async cancelAdminBooking(id: number, request: AdminCancelRentalBookingRequest): Promise<AdminRentalBooking> {
    const bookings = readBookings();
    const booking = bookings.find((item) => item.id === id);
    if (!booking || booking.status !== "CONFIRMED") throw new ApiError("Rental booking cannot be cancelled", 409);
    const cancelled: RentalBooking = {
      ...booking, status: "CANCELLED_BY_ADMIN", cancelledAt: new Date().toISOString(),
      cancellationReason: request.reason?.trim() || null,
    };
    writeBookings(bookings.map((item) => item.id === id ? cancelled : item));
    if (request.keepDatesUnavailable) {
      await this.createAdminOccupancy(booking.property.id, {
        startDate: booking.checkInDate, endDate: booking.checkOutDate,
        type: "OWNER_BLOCK", note: request.reason?.trim() || null,
      });
    }
    return simulateNetwork(this.adminBooking(cancelled));
  }

  async completeAdminBooking(id: number): Promise<AdminRentalBooking> {
    const bookings = readBookings();
    const booking = bookings.find((item) => item.id === id);
    if (!booking || booking.status !== "CONFIRMED") throw new ApiError("Rental booking cannot be completed", 409);
    const completed: RentalBooking = { ...booking, status: "COMPLETED", completedAt: new Date().toISOString() };
    writeBookings(bookings.map((item) => item.id === id ? completed : item));
    return simulateNetwork(this.adminBooking(completed));
  }

  getAdminRentalNotificationPreference(): Promise<RentalAdminNotificationPreference> {
    return simulateNetwork({ ...this.adminNotificationPreference });
  }

  async updateAdminRentalNotificationPreference(
    telegramEnabled: boolean,
  ): Promise<RentalAdminNotificationPreference> {
    this.adminNotificationPreference = { ...this.adminNotificationPreference, telegramEnabled };
    return simulateNetwork({ ...this.adminNotificationPreference });
  }

  private replaceProperty(property: RentalProperty): void {
    const index = properties.findIndex((item) => item.id === property.id);
    if (index >= 0) properties[index] = property;
  }

  private async requireBookableTransferContext(id: number, context: RentalTransferContextType) {
    const response = await this.getTransferContext(id);
    const option = response.options.find((item) => item.context === context
      && item.availability === "BOOKABLE");
    if (!option) throw new ApiError("Rental transfer context is not eligible", 409);
    return option;
  }

  private hasMatchingTransfer(
    rentalBookingId: number,
    direction: TransferDirection,
    date: string,
    address: string,
  ): boolean {
    const raw = localStorage.getItem(TRANSFER_STORAGE_KEY);
    const transfers = raw ? JSON.parse(raw) as Array<TransferBooking & {
      sourceRentalBookingId?: number;
      rentalContext?: RentalTransferContextType;
    }> : [];
    return transfers.some((transfer) => (
      ["REQUESTED", "CONFIRMED", "COMPLETED"].includes(transfer.status)
      && (
        transfer.sourceRentalBookingId === rentalBookingId
        || transfer.direction === direction
          && transfer.pickupDate === date
          && normalizeAddress(transfer.address) === normalizeAddress(address)
      )
    ));
  }

  async unpublishAdminProperty(id: number): Promise<RentalProperty> {
    const property = await this.getAdminProperty(id);
    if (property.status !== "PUBLISHED") {
      throw new ApiError("Rental property is not published", 409, "rental_property_cannot_be_unpublished");
    }
    const updated = { ...property, status: "DRAFT" as const, updatedAt: new Date().toISOString() };
    this.replaceProperty(updated);
    return simulateNetwork(updated);
  }

  async deleteAdminProperty(id: number): Promise<void> {
    const property = await this.getAdminProperty(id);
    if (property.status === "PUBLISHED" || readBookings().some((booking) => booking.property.id === id)) {
      throw new ApiError("Rental property cannot be deleted", 409, "rental_property_cannot_be_deleted");
    }
    property.media.forEach((media) => {
      if (media.url.startsWith("blob:")) URL.revokeObjectURL(media.url);
    });
    const index = properties.findIndex((item) => item.id === id);
    if (index >= 0) properties.splice(index, 1);
    manualOccupancies = manualOccupancies.filter((item) => item.propertyId !== id);
    await simulateNetwork(undefined);
  }

  private uniqueSlug(title: string | null): string | null {
    if (!title) return null;
    const base = title.normalize("NFKD")
      .replace(/[\u0300-\u036f]/g, "")
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/^-+|-+$/g, "")
      .slice(0, 120)
      .replace(/-+$/g, "");
    if (!base) return null;
    let candidate = base;
    let suffix = 2;
    while (properties.some((item) => item.slug === candidate)) {
      const suffixText = `-${suffix++}`;
      candidate = `${base.slice(0, 120 - suffixText.length).replace(/-+$/g, "")}${suffixText}`;
    }
    return candidate;
  }

  private adminBooking(booking: RentalBooking): AdminRentalBooking {
    return { customerId: booking.id + 1_000, communicationIdentityId: booking.id + 2_000, booking };
  }
}
