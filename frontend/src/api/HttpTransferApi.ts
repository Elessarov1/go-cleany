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
import { HttpApiClient } from "./HttpApiClient";
import type { TransferApi } from "./TransferApi";

export class HttpTransferApi implements TransferApi {
  constructor(private readonly client: HttpApiClient) {}

  getConfiguration(): Promise<TransferConfiguration> {
    return this.client.request("/api/v1/transfer/configuration");
  }

  quote(request: TransferQuoteRequest): Promise<TransferQuote> {
    return this.client.request("/api/v1/transfer/quote", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  createBooking(request: CreateTransferBookingRequest): Promise<TransferBooking> {
    return this.client.request("/api/v1/transfer/bookings", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  getBookings(): Promise<TransferBooking[]> {
    return this.client.request("/api/v1/transfer/bookings");
  }

  getBooking(id: number): Promise<TransferBooking> {
    return this.client.request(`/api/v1/transfer/bookings/${id}`);
  }

  async recordRepeatShown(id: number): Promise<void> {
    await this.client.request(`/api/v1/transfer/bookings/${id}/repeat-shown`, { method: "POST" });
  }

  getRepeatPrefill(id: number): Promise<TransferRepeatPrefill> {
    return this.client.request(`/api/v1/transfer/bookings/${id}/repeat-prefill`, { method: "POST" });
  }

  cancelBooking(id: number): Promise<TransferBooking> {
    return this.client.request(`/api/v1/transfer/bookings/${id}/cancel`, { method: "POST" });
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
