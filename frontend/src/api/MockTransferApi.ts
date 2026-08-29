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
  UpdateTransferAirportRequest,
  UpdateTransferVehicleRequest,
  UpsertTransferDriverRequest,
  UpsertTransferPriceRequest,
} from "../domain/transfer";
import { ApiError } from "./ApiError";
import type { TransferApi } from "./TransferApi";

const STORAGE_KEY = "cleany.mock.transfer-bookings.v1";

function dateFromToday(days: number): string {
  const value = new Date();
  value.setHours(12, 0, 0, 0);
  value.setDate(value.getDate() + days);
  return value.toISOString().slice(0, 10);
}

const configuration: TransferConfiguration = {
  earliestBookingDate: dateFromToday(1),
  latestBookingDate: dateFromToday(183),
  timeSlotMinutes: 30,
  airports: [
    { id: 1, code: "GZP", nameRu: "Аэропорт Газипаша", nameEn: "Alanya Gazipaşa Airport" },
    { id: 2, code: "AYT", nameRu: "Аэропорт Анталья", nameEn: "Antalya Airport" },
  ],
  vehicleTypes: [
    { id: 1, code: "SEDAN", nameRu: "Седан", nameEn: "Sedan", maxPassengers: 3, maxLuggage: 3 },
    { id: 2, code: "MINIVAN", nameRu: "Минивэн", nameEn: "Minivan", maxPassengers: 6, maxLuggage: 6 },
  ],
  prices: [
    { airportId: 1, vehicleTypeId: 1, direction: "TO_AIRPORT", amount: 1800, currency: "TRY" },
    { airportId: 1, vehicleTypeId: 1, direction: "FROM_AIRPORT", amount: 1800, currency: "TRY" },
    { airportId: 1, vehicleTypeId: 2, direction: "TO_AIRPORT", amount: 2300, currency: "TRY" },
    { airportId: 1, vehicleTypeId: 2, direction: "FROM_AIRPORT", amount: 2300, currency: "TRY" },
    { airportId: 2, vehicleTypeId: 1, direction: "TO_AIRPORT", amount: 2800, currency: "TRY" },
    { airportId: 2, vehicleTypeId: 1, direction: "FROM_AIRPORT", amount: 2800, currency: "TRY" },
    { airportId: 2, vehicleTypeId: 2, direction: "TO_AIRPORT", amount: 3200, currency: "TRY" },
    { airportId: 2, vehicleTypeId: 2, direction: "FROM_AIRPORT", amount: 3200, currency: "TRY" },
  ],
};

const now = () => new Date().toISOString();
let adminAirports: AdminTransferAirport[] = configuration.airports.map((airport, index) => ({
  ...airport, enabled: true, sortOrder: (index + 1) * 10, createdAt: now(), updatedAt: now(), version: 0,
}));
let adminVehicles: AdminTransferVehicleType[] = configuration.vehicleTypes.map((vehicle, index) => ({
  ...vehicle, enabled: true, sortOrder: (index + 1) * 10, createdAt: now(), updatedAt: now(), version: 0,
}));
let adminPrices: AdminTransferPrice[] = configuration.prices.map((price, index) => ({
  ...price,
  id: index + 1,
  airportCode: configuration.airports.find((item) => item.id === price.airportId)?.code ?? "",
  vehicleCode: configuration.vehicleTypes.find((item) => item.id === price.vehicleTypeId)?.code ?? "",
  enabled: true,
  createdAt: now(),
  updatedAt: now(),
  version: 0,
}));
let adminDrivers: AdminTransferDriver[] = [];

function readBookings(): TransferBooking[] {
  const raw = localStorage.getItem(STORAGE_KEY);
  return raw ? JSON.parse(raw) as TransferBooking[] : [];
}

function writeBookings(bookings: TransferBooking[]): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(bookings));
}

export class MockTransferApi implements TransferApi {
  getConfiguration(): Promise<TransferConfiguration> {
    return Promise.resolve(structuredClone(configuration));
  }

  createBooking(request: CreateTransferBookingRequest): Promise<TransferBooking> {
    const airport = configuration.airports.find((item) => item.id === request.airportId);
    const vehicle = configuration.vehicleTypes.find((item) => item.id === request.vehicleTypeId);
    const price = configuration.prices.find((item) => item.airportId === request.airportId
      && item.vehicleTypeId === request.vehicleTypeId && item.direction === request.direction);
    if (!airport || !vehicle || !price) {
      return Promise.reject(new ApiError("Transfer configuration is unavailable", 409));
    }
    const booking: TransferBooking = {
      id: Date.now(),
      direction: request.direction,
      airportCode: airport.code,
      airportNameRu: airport.nameRu,
      airportNameEn: airport.nameEn,
      vehicleCode: vehicle.code,
      vehicleNameRu: vehicle.nameRu,
      vehicleNameEn: vehicle.nameEn,
      pickupDate: request.pickupDate,
      pickupTime: request.pickupTime,
      address: request.address,
      passengerCount: request.passengerCount,
      luggageCount: request.luggageCount,
      flightNumber: request.flightNumber ?? null,
      scheduledArrivalTime: request.scheduledArrivalTime ?? null,
      customerName: "Alex",
      phone: request.phone,
      comment: request.comment ?? null,
      priceAmount: price.amount,
      priceCurrency: price.currency,
      status: "REQUESTED",
      driverId: null,
      driverName: null,
      createdAt: new Date().toISOString(),
      confirmedAt: null,
      completedAt: null,
      cancelledAt: null,
      rejectedAt: null,
      statusReason: null,
    };
    writeBookings([booking, ...readBookings()]);
    return Promise.resolve(booking);
  }

  getBookings(): Promise<TransferBooking[]> {
    return Promise.resolve(readBookings());
  }

  getBooking(id: number): Promise<TransferBooking> {
    const booking = readBookings().find((item) => item.id === id);
    return booking
      ? Promise.resolve(booking)
      : Promise.reject(new ApiError("Transfer booking not found", 404));
  }

  async cancelBooking(id: number): Promise<TransferBooking> {
    const booking = await this.getBooking(id);
    if (booking.status !== "REQUESTED" && booking.status !== "CONFIRMED") {
      throw new ApiError("Transfer booking cannot be cancelled", 409);
    }
    const cancelled = { ...booking, status: "CANCELLED" as const, cancelledAt: new Date().toISOString() };
    writeBookings(readBookings().map((item) => item.id === id ? cancelled : item));
    return cancelled;
  }

  getAdminAirports(): Promise<AdminTransferAirport[]> {
    return Promise.resolve(structuredClone(adminAirports));
  }

  createAdminAirport(request: CreateTransferAirportRequest): Promise<AdminTransferAirport> {
    const airport = { ...request, id: Date.now(), createdAt: now(), updatedAt: now(), version: 0 };
    adminAirports = [...adminAirports, airport];
    return Promise.resolve(airport);
  }

  updateAdminAirport(id: number, request: UpdateTransferAirportRequest): Promise<AdminTransferAirport> {
    const current = adminAirports.find((item) => item.id === id);
    if (!current) return Promise.reject(new ApiError("Transfer airport not found", 404));
    const updated = { ...current, ...request, updatedAt: now(), version: current.version + 1 };
    adminAirports = adminAirports.map((item) => item.id === id ? updated : item);
    return Promise.resolve(updated);
  }

  getAdminVehicles(): Promise<AdminTransferVehicleType[]> {
    return Promise.resolve(structuredClone(adminVehicles));
  }

  createAdminVehicle(request: CreateTransferVehicleRequest): Promise<AdminTransferVehicleType> {
    const vehicle = { ...request, id: Date.now(), createdAt: now(), updatedAt: now(), version: 0 };
    adminVehicles = [...adminVehicles, vehicle];
    return Promise.resolve(vehicle);
  }

  updateAdminVehicle(id: number, request: UpdateTransferVehicleRequest): Promise<AdminTransferVehicleType> {
    const current = adminVehicles.find((item) => item.id === id);
    if (!current) return Promise.reject(new ApiError("Transfer vehicle not found", 404));
    const updated = { ...current, ...request, updatedAt: now(), version: current.version + 1 };
    adminVehicles = adminVehicles.map((item) => item.id === id ? updated : item);
    return Promise.resolve(updated);
  }

  getAdminPrices(): Promise<AdminTransferPrice[]> {
    return Promise.resolve(structuredClone(adminPrices));
  }

  upsertAdminPrice(request: UpsertTransferPriceRequest): Promise<AdminTransferPrice> {
    const current = adminPrices.find((item) => item.airportId === request.airportId
      && item.vehicleTypeId === request.vehicleTypeId && item.direction === request.direction);
    const updated: AdminTransferPrice = {
      ...request,
      id: current?.id ?? Date.now(),
      airportCode: adminAirports.find((item) => item.id === request.airportId)?.code ?? "",
      vehicleCode: adminVehicles.find((item) => item.id === request.vehicleTypeId)?.code ?? "",
      createdAt: current?.createdAt ?? now(),
      updatedAt: now(),
      version: (current?.version ?? -1) + 1,
    };
    adminPrices = current
      ? adminPrices.map((item) => item.id === current.id ? updated : item)
      : [...adminPrices, updated];
    return Promise.resolve(updated);
  }

  getAdminDrivers(): Promise<AdminTransferDriver[]> {
    return Promise.resolve(structuredClone(adminDrivers));
  }

  createAdminDriver(request: UpsertTransferDriverRequest): Promise<AdminTransferDriver> {
    const driver = this.driverFromRequest(Date.now(), request);
    adminDrivers = [...adminDrivers, driver];
    return Promise.resolve(driver);
  }

  updateAdminDriver(id: number, request: UpsertTransferDriverRequest): Promise<AdminTransferDriver> {
    const current = adminDrivers.find((item) => item.id === id);
    if (!current) return Promise.reject(new ApiError("Transfer driver not found", 404));
    const updated = { ...this.driverFromRequest(id, request), createdAt: current.createdAt, version: current.version + 1 };
    adminDrivers = adminDrivers.map((item) => item.id === id ? updated : item);
    return Promise.resolve(updated);
  }

  createAdminDriverTelegramLink(id: number): Promise<TransferDriverLink> {
    const driver = adminDrivers.find((item) => item.id === id);
    if (!driver?.configuredTelegramUserId) {
      return Promise.reject(new ApiError("Configure Telegram ID first", 409));
    }
    return Promise.resolve({
      url: `https://t.me/test_bot?start=driver_${"x".repeat(43)}`,
      expiresAt: new Date(Date.now() + 86_400_000).toISOString(),
    });
  }

  getAdminBookings(filters: AdminTransferBookingFilters = {}): Promise<TransferBooking[]> {
    return Promise.resolve(readBookings().filter((booking) =>
      (!filters.status || booking.status === filters.status)
      && (!filters.date || booking.pickupDate === filters.date)
      && (!filters.airportId || adminAirports.find((airport) => airport.id === filters.airportId)?.code === booking.airportCode)
    ));
  }

  getAdminBooking(id: number): Promise<TransferBooking> {
    return this.getBooking(id);
  }

  async assignAdminBooking(id: number, driverId: number): Promise<TransferBooking> {
    const booking = await this.getBooking(id);
    const driver = adminDrivers.find((item) => item.id === driverId && item.enabled);
    if (booking.status !== "REQUESTED" || !driver) throw new ApiError("Transfer assignment conflict", 409);
    return this.replaceBooking({
      ...booking, status: "CONFIRMED", driverId, driverName: driver.name, confirmedAt: now(),
    });
  }

  async rejectAdminBooking(id: number, reason?: string): Promise<TransferBooking> {
    const booking = await this.getBooking(id);
    if (booking.status !== "REQUESTED") throw new ApiError("Transfer booking state conflict", 409);
    return this.replaceBooking({ ...booking, status: "REJECTED", rejectedAt: now(), statusReason: reason ?? null });
  }

  async cancelAdminBooking(id: number, reason?: string): Promise<TransferBooking> {
    const booking = await this.getBooking(id);
    if (booking.status !== "REQUESTED" && booking.status !== "CONFIRMED") throw new ApiError("Transfer booking state conflict", 409);
    return this.replaceBooking({ ...booking, status: "CANCELLED", cancelledAt: now(), statusReason: reason ?? null });
  }

  async completeAdminBooking(id: number): Promise<TransferBooking> {
    const booking = await this.getBooking(id);
    if (booking.status !== "CONFIRMED") throw new ApiError("Transfer booking state conflict", 409);
    return this.replaceBooking({ ...booking, status: "COMPLETED", completedAt: now() });
  }

  private replaceBooking(booking: TransferBooking): TransferBooking {
    writeBookings(readBookings().map((item) => item.id === booking.id ? booking : item));
    return booking;
  }

  private driverFromRequest(id: number, request: UpsertTransferDriverRequest): AdminTransferDriver {
    return {
      id,
      name: request.name,
      phone: request.phone,
      enabled: request.enabled,
      configuredTelegramUserId: request.telegramUserId,
      verifiedTelegramUserId: null,
      telegramChatId: null,
      telegramNotificationsEnabled: false,
      telegramBotAuthorizedAt: null,
      telegramStatus: request.telegramUserId ? "AWAITING_AUTHORIZATION" : "NOT_CONFIGURED",
      createdAt: now(),
      updatedAt: now(),
      version: 0,
    };
  }
}
