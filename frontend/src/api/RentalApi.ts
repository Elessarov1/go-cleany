import type {
  AdminCancelRentalBookingRequest,
  AdminRentalBooking,
  AdminRentalBookingFilters,
  CreateRentalBookingRequest,
  RentalAvailability,
  RentalBooking,
  RentalCleaningContext,
  RentalBookingQuote,
  RentalBookingQuoteRequest,
  RentalConfiguration,
  RentalAdminNotificationPreference,
  RentalProperty,
  RentalOccupancy,
  UpdateRentalPropertyRequest,
  UpsertRentalOccupancyRequest,
} from "../domain/rental";

export interface RentalApi {
  getConfiguration(): Promise<RentalConfiguration>;
  getProperties(): Promise<RentalProperty[]>;
  getProperty(slug: string): Promise<RentalProperty>;
  getAvailability(propertyId: number, fromDate: string, toDate: string): Promise<RentalAvailability>;
  quoteBooking(request: RentalBookingQuoteRequest): Promise<RentalBookingQuote>;
  createBooking(request: CreateRentalBookingRequest): Promise<RentalBooking>;
  getBookings(): Promise<RentalBooking[]>;
  getBooking(id: number): Promise<RentalBooking>;
  getCleaningContext(id: number): Promise<RentalCleaningContext>;
  cancelBooking(id: number): Promise<RentalBooking>;
  getAdminProperties(): Promise<RentalProperty[]>;
  createAdminProperty(): Promise<RentalProperty>;
  getAdminProperty(id: number): Promise<RentalProperty>;
  updateAdminProperty(id: number, request: UpdateRentalPropertyRequest): Promise<RentalProperty>;
  publishAdminProperty(id: number): Promise<RentalProperty>;
  unpublishAdminProperty(id: number): Promise<RentalProperty>;
  deleteAdminProperty(id: number): Promise<void>;
  archiveAdminProperty(id: number): Promise<RentalProperty>;
  addAdminPropertyMedia(id: number, file: File, cover: boolean): Promise<RentalProperty>;
  removeAdminPropertyMedia(id: number, mediaId: number): Promise<RentalProperty>;
  setAdminPropertyMediaCover(id: number, mediaId: number): Promise<RentalProperty>;
  reorderAdminPropertyMedia(id: number, mediaIds: number[]): Promise<RentalProperty>;
  getAdminPropertyMedia(id: number, mediaId: number): Promise<Blob>;
  getAdminOccupancies(id: number, fromDate: string, toDate: string): Promise<RentalOccupancy[]>;
  createAdminOccupancy(id: number, request: UpsertRentalOccupancyRequest): Promise<RentalOccupancy>;
  updateAdminOccupancy(id: number, occupancyId: number, request: UpsertRentalOccupancyRequest): Promise<RentalOccupancy>;
  deleteAdminOccupancy(id: number, occupancyId: number): Promise<void>;
  getAdminBookings(filters?: AdminRentalBookingFilters): Promise<AdminRentalBooking[]>;
  getAdminBooking(id: number): Promise<AdminRentalBooking>;
  cancelAdminBooking(id: number, request: AdminCancelRentalBookingRequest): Promise<AdminRentalBooking>;
  completeAdminBooking(id: number): Promise<AdminRentalBooking>;
  getAdminRentalNotificationPreference(): Promise<RentalAdminNotificationPreference>;
  updateAdminRentalNotificationPreference(telegramEnabled: boolean): Promise<RentalAdminNotificationPreference>;
}
