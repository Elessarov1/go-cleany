import type {
  AdminTransferAirport,
  AdminTransferBookingFilters,
  AdminTransferDriver,
  AdminTransferPrice,
  AdminTransferVehicleType,
  CreateTransferAirportRequest,
  CreateTransferBookingRequest,
  CreateTransferVehicleRequest,
  TransferBooking,
  TransferConfiguration,
  TransferDriverLink,
  TransferRepeatPrefill,
  TransferQuote,
  TransferQuoteRequest,
  UpdateTransferAirportRequest,
  UpdateTransferVehicleRequest,
  UpsertTransferDriverRequest,
  UpsertTransferPriceRequest,
} from "../domain/transfer";
import { generatedWire, HttpApiClient } from "./HttpApiClient";
import type { TransferApi } from "./TransferApi";
import {
  Configuration,
  CreateTransferBookingRequestFromJSON,
  TransferApi as GeneratedTransferApi,
  TransferBookingToJSON,
  TransferConfigurationToJSON,
  TransferQuoteRequestFromJSON,
  TransferQuoteToJSON,
  TransferRepeatPrefillToJSON,
} from "@locoplace/api-client";

export class HttpTransferApi implements TransferApi {
  private readonly generated: GeneratedTransferApi;

  constructor(private readonly client: HttpApiClient) {
    this.generated = new GeneratedTransferApi(new Configuration({
      basePath: client.basePath,
      fetchApi: client.generatedFetch,
    }));
  }

  getConfiguration(): Promise<TransferConfiguration> {
    return this.client.generated(this.generated.getTransferConfiguration())
      .then((value) => generatedWire<TransferConfiguration>(TransferConfigurationToJSON(value)));
  }

  quote(request: TransferQuoteRequest): Promise<TransferQuote> {
    return this.client.generated(this.generated.quoteTransfer({
      transferQuoteRequest: TransferQuoteRequestFromJSON(request),
    })).then((value) => generatedWire<TransferQuote>(TransferQuoteToJSON(value)));
  }

  createBooking(request: CreateTransferBookingRequest): Promise<TransferBooking> {
    return this.client.generated(this.generated.createTransferBooking({
      idempotencyKey: crypto.randomUUID(),
      createTransferBookingRequest: CreateTransferBookingRequestFromJSON(request),
    })).then((value) => generatedWire<TransferBooking>(TransferBookingToJSON(value)));
  }

  async getBookings(): Promise<TransferBooking[]> {
    const bookings: TransferBooking[] = [];
    let cursor: string | undefined;
    do {
      const page = await this.client.generated(this.generated.getTransferBookings({ cursor, size: 50 }));
      bookings.push(...page.items.map((value) => generatedWire<TransferBooking>(TransferBookingToJSON(value))));
      cursor = page.hasMore ? page.nextCursor ?? undefined : undefined;
    } while (cursor);
    return bookings;
  }

  getBooking(id: number): Promise<TransferBooking> {
    return this.client.generated(this.generated.getTransferBooking({ bookingId: id }))
      .then((value) => generatedWire<TransferBooking>(TransferBookingToJSON(value)));
  }

  async recordRepeatShown(id: number): Promise<void> {
    await this.client.generated(this.generated.recordTransferRepeatShown({ bookingId: id }));
  }

  getRepeatPrefill(id: number): Promise<TransferRepeatPrefill> {
    return this.client.generated(this.generated.getTransferRepeatPrefill({ bookingId: id }))
      .then((value) => generatedWire<TransferRepeatPrefill>(TransferRepeatPrefillToJSON(value)));
  }

  cancelBooking(id: number): Promise<TransferBooking> {
    return this.client.generated(this.generated.cancelTransferBooking({ bookingId: id }))
      .then((value) => generatedWire<TransferBooking>(TransferBookingToJSON(value)));
  }

  getAdminAirports(): Promise<AdminTransferAirport[]> {
    return this.client.request("/api/v1/admin/transfer/airports");
  }

  createAdminAirport(request: CreateTransferAirportRequest): Promise<AdminTransferAirport> {
    return this.client.request("/api/v1/admin/transfer/airports", { method: "POST", body: JSON.stringify(request) });
  }

  updateAdminAirport(id: number, request: UpdateTransferAirportRequest): Promise<AdminTransferAirport> {
    return this.client.request(`/api/v1/admin/transfer/airports/${id}`, { method: "PUT", body: JSON.stringify(request) });
  }

  getAdminVehicles(): Promise<AdminTransferVehicleType[]> {
    return this.client.request("/api/v1/admin/transfer/vehicles");
  }

  createAdminVehicle(request: CreateTransferVehicleRequest): Promise<AdminTransferVehicleType> {
    return this.client.request("/api/v1/admin/transfer/vehicles", { method: "POST", body: JSON.stringify(request) });
  }

  updateAdminVehicle(id: number, request: UpdateTransferVehicleRequest): Promise<AdminTransferVehicleType> {
    return this.client.request(`/api/v1/admin/transfer/vehicles/${id}`, { method: "PUT", body: JSON.stringify(request) });
  }

  getAdminPrices(): Promise<AdminTransferPrice[]> {
    return this.client.request("/api/v1/admin/transfer/prices");
  }

  upsertAdminPrice(request: UpsertTransferPriceRequest): Promise<AdminTransferPrice> {
    return this.client.request("/api/v1/admin/transfer/prices", { method: "PUT", body: JSON.stringify(request) });
  }

  getAdminDrivers(): Promise<AdminTransferDriver[]> {
    return this.client.request("/api/v1/admin/transfer/drivers");
  }

  createAdminDriver(request: UpsertTransferDriverRequest): Promise<AdminTransferDriver> {
    return this.client.request("/api/v1/admin/transfer/drivers", { method: "POST", body: JSON.stringify(request) });
  }

  updateAdminDriver(id: number, request: UpsertTransferDriverRequest): Promise<AdminTransferDriver> {
    return this.client.request(`/api/v1/admin/transfer/drivers/${id}`, { method: "PUT", body: JSON.stringify(request) });
  }

  createAdminDriverTelegramLink(id: number): Promise<TransferDriverLink> {
    return this.client.request(`/api/v1/admin/transfer/drivers/${id}/telegram-link`, { method: "POST" });
  }

  getAdminBookings(filters: AdminTransferBookingFilters = {}): Promise<TransferBooking[]> {
    const query = new URLSearchParams();
    if (filters.status) query.set("status", filters.status);
    if (filters.date) query.set("date", filters.date);
    if (filters.airportId) query.set("airportId", String(filters.airportId));
    const suffix = query.size ? `?${query.toString()}` : "";
    return this.client.request(`/api/v1/admin/transfer/bookings${suffix}`);
  }

  getAdminBooking(id: number): Promise<TransferBooking> {
    return this.client.request(`/api/v1/admin/transfer/bookings/${id}`);
  }

  assignAdminBooking(id: number, driverId: number): Promise<TransferBooking> {
    return this.client.request(`/api/v1/admin/transfer/bookings/${id}/assign`, {
      method: "POST", body: JSON.stringify({ driverId }),
    });
  }

  rejectAdminBooking(id: number, reason?: string): Promise<TransferBooking> {
    return this.statusAction(id, "reject", reason);
  }

  cancelAdminBooking(id: number, reason?: string): Promise<TransferBooking> {
    return this.statusAction(id, "cancel", reason);
  }

  completeAdminBooking(id: number): Promise<TransferBooking> {
    return this.client.request(`/api/v1/admin/transfer/bookings/${id}/complete`, { method: "POST" });
  }

  private statusAction(id: number, action: "reject" | "cancel", reason?: string): Promise<TransferBooking> {
    return this.client.request(`/api/v1/admin/transfer/bookings/${id}/${action}`, {
      method: "POST", body: JSON.stringify({ reason: reason?.trim() || null }),
    });
  }
}
