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
import { generatedWire, HttpApiClient } from "./HttpApiClient";
import type { RentalApi, RentalSearchPageOptions } from "./RentalApi";
import {
  Configuration,
  CreateRentalBookingRequestFromJSON,
  RentalApi as GeneratedRentalApi,
  RentalAvailabilityToJSON,
  RentalBookingToJSON,
  RentalCleaningContextToJSON,
  RentalConfigurationToJSON,
  RentalPropertyToJSON,
  RentalQuoteToJSON,
  RentalSearchToJSON,
  RentalTermType as GeneratedRentalTermType,
  RentalTransferContextToJSON,
  RentalTransferPrefillToJSON,
} from "@locoplace/api-client";

export class HttpRentalApi implements RentalApi {
  private readonly generated: GeneratedRentalApi;

  constructor(private readonly client: HttpApiClient) {
    this.generated = new GeneratedRentalApi(new Configuration({
      basePath: client.basePath,
      fetchApi: client.generatedFetch,
    }));
  }

  getConfiguration(): Promise<RentalConfiguration> {
    return this.client.generated(this.generated.getRentalConfiguration())
      .then((value) => generatedWire<RentalConfiguration>(RentalConfigurationToJSON(value)));
  }

  async getProperty(slug: string): Promise<RentalProperty> {
    const value = await this.client.generated(this.generated.getRentalProperty({ slug }));
    const property = generatedWire<RentalProperty>(RentalPropertyToJSON(value));
    return this.resolveMedia(property);
  }

  getAvailability(
    propertyId: number,
    fromDate: string,
    toDate: string,
  ): Promise<RentalAvailability> {
    return this.client.generated(this.generated.getRentalAvailability({
      propertyId,
      fromDate: apiDate(fromDate),
      toDate: apiDate(toDate),
    })).then((value) => generatedWire<RentalAvailability>(RentalAvailabilityToJSON(value)));
  }

  async search(
    request: RentalSearchRequest,
    options: RentalSearchPageOptions = {},
  ): Promise<RentalSearchResponse> {
    const criteria = "termType" in request && request.termType ? {
      termType: request.termType as GeneratedRentalTermType,
      checkInDate: apiDate(request.checkInDate),
      checkOutDate: request.termType === "DATE_RANGE" ? apiDate(request.checkOutDate) : undefined,
      months: request.termType === "MONTHLY" ? request.months : undefined,
      guests: request.guests,
      cursor: options.cursor,
      size: options.size,
      xRentalPreviousSearchId: options.previousSearchId,
    } : {
      cursor: options.cursor,
      size: options.size,
      xRentalPreviousSearchId: options.previousSearchId,
    };
    const value = await this.client.generated(this.generated.searchRentalProperties(criteria, {
      signal: options.signal,
    }));
    const response = generatedWire<RentalSearchResponse>(RentalSearchToJSON(value));
    return {
      ...response,
      properties: response.properties.map((property) => ({
        ...property,
        coverUrl: property.coverUrl ? this.client.resolveUrl(property.coverUrl) : null,
      })),
    };
  }

  quotePublic(propertyId: number, request: RentalTermCriteria): Promise<RentalQuote> {
    return this.client.generated(this.generated.quoteRentalProperty({
      propertyId,
      termType: request.termType as GeneratedRentalTermType,
      checkInDate: apiDate(request.checkInDate),
      checkOutDate: request.termType === "DATE_RANGE" ? apiDate(request.checkOutDate) : undefined,
      months: request.termType === "MONTHLY" ? request.months : undefined,
      guests: request.guests,
    })).then((value) => generatedWire<RentalQuote>(RentalQuoteToJSON(value)));
  }

  recordPropertyOpened(searchExecutionId: string): Promise<void> {
    return this.client.generated(this.generated.recordRentalSearchOpened({
      executionId: searchExecutionId,
    }));
  }

  recordFirstCardRendered(searchExecutionId: string, durationMs: number): Promise<void> {
    return this.client.generated(this.generated.recordRentalFirstCard({
      executionId: searchExecutionId,
      rentalFirstCardRequest: { durationMs: Math.max(0, Math.round(durationMs)) },
    }));
  }

  createBooking(request: CreateRentalBookingRequest): Promise<RentalBooking> {
    return this.client.generated(this.generated.createRentalBooking({
      idempotencyKey: crypto.randomUUID(),
      createRentalBookingRequest: CreateRentalBookingRequestFromJSON(request),
    })).then((value) => generatedWire<RentalBooking>(RentalBookingToJSON(value)));
  }

  async getBookings(): Promise<RentalBooking[]> {
    const bookings: RentalBooking[] = [];
    let cursor: string | undefined;
    do {
      const page = await this.client.generated(this.generated.getRentalBookings({ cursor, size: 50 }));
      bookings.push(...page.items.map((value) => generatedWire<RentalBooking>(RentalBookingToJSON(value))));
      cursor = page.hasMore ? page.nextCursor ?? undefined : undefined;
    } while (cursor);
    return bookings;
  }

  getBooking(id: number): Promise<RentalBooking> {
    return this.client.generated(this.generated.getRentalBooking({ bookingId: id }))
      .then((value) => generatedWire<RentalBooking>(RentalBookingToJSON(value)));
  }

  getCleaningContext(id: number): Promise<RentalCleaningContext> {
    return this.client.generated(this.generated.getRentalCleaningContext({ bookingId: id }))
      .then((value) => generatedWire<RentalCleaningContext>(RentalCleaningContextToJSON(value)));
  }

  getTransferContext(id: number): Promise<RentalTransferContext> {
    return this.client.generated(this.generated.getRentalTransferContext({ bookingId: id }))
      .then((value) => generatedWire<RentalTransferContext>(RentalTransferContextToJSON(value)));
  }

  async recordTransferContextShown(id: number, context: RentalTransferContextType): Promise<void> {
    await this.client.generated(this.generated.recordRentalTransferContextShown({
      bookingId: id,
      context: context as Parameters<GeneratedRentalApi["recordRentalTransferContextShown"]>[0]["context"],
    }));
  }

  getTransferPrefill(id: number, context: RentalTransferContextType): Promise<RentalTransferPrefill> {
    return this.client.generated(this.generated.getRentalTransferPrefill({
      bookingId: id,
      context: context as Parameters<GeneratedRentalApi["getRentalTransferPrefill"]>[0]["context"],
    })).then((value) => generatedWire<RentalTransferPrefill>(RentalTransferPrefillToJSON(value)));
  }

  cancelBooking(id: number): Promise<RentalBooking> {
    return this.client.generated(this.generated.cancelRentalBooking({ bookingId: id }))
      .then((value) => generatedWire<RentalBooking>(RentalBookingToJSON(value)));
  }

  async getAdminProperties(): Promise<RentalProperty[]> {
    return (await this.client.request<RentalProperty[]>("/api/v1/admin/rental/properties"))
      .map((property) => this.resolveMedia(property));
  }

  async reorderAdminProperties(propertyIds: number[]): Promise<RentalProperty[]> {
    const properties = await this.client.request<RentalProperty[]>(
      "/api/v1/admin/rental/properties/order",
      {
        method: "PUT",
        body: JSON.stringify({ propertyIds }),
      },
    );
    return properties.map((property) => this.resolveMedia(property));
  }

  async createAdminProperty(): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request("/api/v1/admin/rental/properties", { method: "POST" }));
  }

  async getAdminProperty(id: number): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}`));
  }

  async updateAdminProperty(id: number, request: UpdateRentalPropertyRequest): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}`, {
      method: "PUT",
      body: JSON.stringify(request),
    }));
  }

  async publishAdminProperty(id: number): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}/publish`, { method: "POST" }));
  }

  async unpublishAdminProperty(id: number): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}/unpublish`, { method: "POST" }));
  }

  deleteAdminProperty(id: number): Promise<void> {
    return this.client.request(`/api/v1/admin/rental/properties/${id}`, { method: "DELETE" });
  }

  async archiveAdminProperty(id: number): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}/archive`, { method: "POST" }));
  }

  async addAdminPropertyMedia(id: number, file: File, cover: boolean): Promise<RentalProperty> {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("cover", String(cover));
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}/media`, {
      method: "POST",
      body: formData,
    }));
  }

  async removeAdminPropertyMedia(id: number, mediaId: number): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}/media/${mediaId}`, { method: "DELETE" }));
  }

  async setAdminPropertyMediaCover(id: number, mediaId: number): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}/media/${mediaId}/cover`, { method: "POST" }));
  }

  async reorderAdminPropertyMedia(id: number, mediaIds: number[]): Promise<RentalProperty> {
    return this.resolveMedia(await this.client.request(`/api/v1/admin/rental/properties/${id}/media/order`, {
      method: "PUT",
      body: JSON.stringify({ mediaIds }),
    }));
  }

  getAdminPropertyMedia(id: number, mediaId: number, variant?: "thumbnail"): Promise<Blob> {
    const suffix = variant ? `/${variant}` : "";
    return this.client.requestBlob(`/api/v1/admin/rental/properties/${id}/media/${mediaId}${suffix}`);
  }

  getAdminOccupancies(id: number, fromDate: string, toDate: string): Promise<RentalOccupancy[]> {
    const query = new URLSearchParams({ fromDate, toDate });
    return this.client.request(`/api/v1/admin/rental/properties/${id}/occupancies?${query.toString()}`);
  }

  createAdminOccupancy(id: number, request: UpsertRentalOccupancyRequest): Promise<RentalOccupancy> {
    return this.client.request(`/api/v1/admin/rental/properties/${id}/occupancies`, {
      method: "POST", body: JSON.stringify(request),
    });
  }

  updateAdminOccupancy(id: number, occupancyId: number, request: UpsertRentalOccupancyRequest): Promise<RentalOccupancy> {
    return this.client.request(`/api/v1/admin/rental/properties/${id}/occupancies/${occupancyId}`, {
      method: "PUT", body: JSON.stringify(request),
    });
  }

  deleteAdminOccupancy(id: number, occupancyId: number): Promise<void> {
    return this.client.request(`/api/v1/admin/rental/properties/${id}/occupancies/${occupancyId}`, { method: "DELETE" });
  }

  getAdminBookings(filters: AdminRentalBookingFilters = {}): Promise<AdminRentalBooking[]> {
    const query = new URLSearchParams();
    if (filters.status) query.set("status", filters.status);
    if (filters.propertyId) query.set("propertyId", String(filters.propertyId));
    if (filters.time) query.set("time", filters.time);
    const suffix = query.size ? `?${query.toString()}` : "";
    return this.client.request(`/api/v1/admin/rental/bookings${suffix}`);
  }

  getAdminBooking(id: number): Promise<AdminRentalBooking> {
    return this.client.request(`/api/v1/admin/rental/bookings/${id}`);
  }

  cancelAdminBooking(id: number, request: AdminCancelRentalBookingRequest): Promise<AdminRentalBooking> {
    return this.client.request(`/api/v1/admin/rental/bookings/${id}/cancel`, {
      method: "POST", body: JSON.stringify(request),
    });
  }

  completeAdminBooking(id: number): Promise<AdminRentalBooking> {
    return this.client.request(`/api/v1/admin/rental/bookings/${id}/complete`, { method: "POST" });
  }

  getAdminRentalNotificationPreference(): Promise<RentalAdminNotificationPreference> {
    return this.client.request("/api/v1/admin/rental/notification-preferences");
  }

  updateAdminRentalNotificationPreference(
    telegramEnabled: boolean,
  ): Promise<RentalAdminNotificationPreference> {
    return this.client.request("/api/v1/admin/rental/notification-preferences", {
      method: "PUT",
      body: JSON.stringify({ telegramEnabled }),
    });
  }

  private resolveMedia(property: RentalProperty): RentalProperty {
    return {
      ...property,
      media: property.media.map((media) => ({
        ...media,
        url: this.client.resolveUrl(media.url),
        cardUrl: media.cardUrl ? this.client.resolveUrl(media.cardUrl) : undefined,
        thumbnailUrl: media.thumbnailUrl ? this.client.resolveUrl(media.thumbnailUrl) : undefined,
      })),
    };
  }

}

function apiDate(value: string): Date {
  return new Date(`${value}T00:00:00.000Z`);
}
