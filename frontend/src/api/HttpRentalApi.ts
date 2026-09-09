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
import { HttpApiClient } from "./HttpApiClient";
import type { RentalApi } from "./RentalApi";

export class HttpRentalApi implements RentalApi {
  constructor(private readonly client: HttpApiClient) {}

  getConfiguration(): Promise<RentalConfiguration> {
    return this.client.request("/api/v1/rental/configuration");
  }

  async getProperty(slug: string): Promise<RentalProperty> {
    const property = await this.client.request<RentalProperty>(
      `/api/v1/rental/properties/${encodeURIComponent(slug)}`,
    );
    return this.resolveMedia(property);
  }

  getAvailability(
    propertyId: number,
    fromDate: string,
    toDate: string,
  ): Promise<RentalAvailability> {
    const query = new URLSearchParams({ fromDate, toDate });
    return this.client.request(
      `/api/v1/rental/properties/${propertyId}/availability?${query.toString()}`,
    );
  }

  async search(
    request: RentalSearchRequest,
    signal?: AbortSignal,
    previousSearchId?: string,
  ): Promise<RentalSearchResponse> {
    const query = this.searchQuery(request);
    const response = await this.client.request<RentalSearchResponse>(
      `/api/v1/rental/search?${query.toString()}`,
      {
        signal,
        headers: previousSearchId ? { "X-Rental-Previous-Search-Id": previousSearchId } : undefined,
      },
    );
    return {
      ...response,
      properties: response.properties.map((property) => ({
        ...property,
        coverUrl: property.coverUrl ? this.client.resolveUrl(property.coverUrl) : null,
      })),
    };
  }

  quotePublic(propertyId: number, request: RentalTermCriteria): Promise<RentalQuote> {
    return this.client.request(
      `/api/v1/rental/properties/${propertyId}/quote?${this.searchQuery(request).toString()}`,
    );
  }

  recordPropertyOpened(searchExecutionId: string): Promise<void> {
    return this.client.request(`/api/v1/rental/searches/${searchExecutionId}/opened`, {
      method: "POST",
    });
  }

  recordFirstCardRendered(searchExecutionId: string, durationMs: number): Promise<void> {
    return this.client.request(`/api/v1/rental/searches/${searchExecutionId}/first-card`, {
      method: "POST",
      body: JSON.stringify({ durationMs: Math.max(0, Math.round(durationMs)) }),
    });
  }

  createBooking(request: CreateRentalBookingRequest): Promise<RentalBooking> {
    return this.client.request("/api/v1/rental/bookings", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  getBookings(): Promise<RentalBooking[]> {
    return this.client.request("/api/v1/rental/bookings");
  }

  getBooking(id: number): Promise<RentalBooking> {
    return this.client.request(`/api/v1/rental/bookings/${id}`);
  }

  getCleaningContext(id: number): Promise<RentalCleaningContext> {
    return this.client.request(`/api/v1/rental/bookings/${id}/cleaning-context`);
  }

  getTransferContext(id: number): Promise<RentalTransferContext> {
    return this.client.request(`/api/v1/rental/bookings/${id}/transfer-context`);
  }

  async recordTransferContextShown(id: number, context: RentalTransferContextType): Promise<void> {
    await this.client.request(
      `/api/v1/rental/bookings/${id}/transfer-context/${context}/shown`,
      { method: "POST" },
    );
  }

  getTransferPrefill(id: number, context: RentalTransferContextType): Promise<RentalTransferPrefill> {
    return this.client.request(
      `/api/v1/rental/bookings/${id}/transfer-context/${context}/prefill`,
      { method: "POST" },
    );
  }

  cancelBooking(id: number): Promise<RentalBooking> {
    return this.client.request(`/api/v1/rental/bookings/${id}/cancel`, { method: "POST" });
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

  private searchQuery(request: RentalSearchRequest): URLSearchParams {
    if (!("termType" in request) || !request.termType) return new URLSearchParams();
    const query = new URLSearchParams({
      termType: request.termType,
      checkInDate: request.checkInDate,
      guests: String(request.guests),
    });
    if (request.termType === "DATE_RANGE") query.set("checkOutDate", request.checkOutDate);
    else query.set("months", String(request.months));
    return query;
  }
}
