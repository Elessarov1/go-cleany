import type {
  AdminCancelRentalBookingRequest,
  AdminRentalBooking,
  AdminRentalBookingFilters,
  CreateRentalBookingRequest,
  RentalAvailability,
  RentalBooking,
  RentalCleaningContext,
  RentalTransferContext,
  RentalTransferContextType,
  RentalTransferPrefill,
  RentalQuote,
  RentalConfiguration,
  RentalAdminNotificationPreference,
  RentalProperty,
  RentalSearchRequest,
  RentalSearchResponse,
  RentalTermCriteria,
  RentalOccupancy,
  UpdateRentalPropertyRequest,
  UpsertRentalOccupancyRequest,
} from "../domain/rental";

export interface RentalSearchPageOptions {
  signal?: AbortSignal;
  previousSearchId?: string;
  cursor?: string;
  size?: number;
}

export interface RentalApi {
  getConfiguration(): Promise<RentalConfiguration>;
  getProperty(slug: string): Promise<RentalProperty>;
  getAvailability(propertyId: number, fromDate: string, toDate: string): Promise<RentalAvailability>;
  search(
    request: RentalSearchRequest,
    options?: RentalSearchPageOptions,
  ): Promise<RentalSearchResponse>;
  quotePublic(propertyId: number, request: RentalTermCriteria): Promise<RentalQuote>;
  recordPropertyOpened(searchExecutionId: string): Promise<void>;
  recordFirstCardRendered(searchExecutionId: string, durationMs: number): Promise<void>;
  createBooking(request: CreateRentalBookingRequest): Promise<RentalBooking>;
  getBookings(): Promise<RentalBooking[]>;
  getBooking(id: number): Promise<RentalBooking>;
  getCleaningContext(id: number): Promise<RentalCleaningContext>;
  getTransferContext(id: number): Promise<RentalTransferContext>;
  recordTransferContextShown(id: number, context: RentalTransferContextType): Promise<void>;
  getTransferPrefill(id: number, context: RentalTransferContextType): Promise<RentalTransferPrefill>;
  cancelBooking(id: number): Promise<RentalBooking>;
  getAdminProperties(): Promise<RentalProperty[]>;
  reorderAdminProperties(propertyIds: number[]): Promise<RentalProperty[]>;
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
  getAdminPropertyMedia(id: number, mediaId: number, variant?: "thumbnail"): Promise<Blob>;
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
