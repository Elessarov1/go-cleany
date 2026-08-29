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

export interface TransferApi {
  getConfiguration(): Promise<TransferConfiguration>;
  createBooking(request: CreateTransferBookingRequest): Promise<TransferBooking>;
  getBookings(): Promise<TransferBooking[]>;
  getBooking(id: number): Promise<TransferBooking>;
  cancelBooking(id: number): Promise<TransferBooking>;
  getAdminAirports(): Promise<AdminTransferAirport[]>;
  createAdminAirport(request: CreateTransferAirportRequest): Promise<AdminTransferAirport>;
  updateAdminAirport(id: number, request: UpdateTransferAirportRequest): Promise<AdminTransferAirport>;
  getAdminVehicles(): Promise<AdminTransferVehicleType[]>;
  createAdminVehicle(request: CreateTransferVehicleRequest): Promise<AdminTransferVehicleType>;
  updateAdminVehicle(id: number, request: UpdateTransferVehicleRequest): Promise<AdminTransferVehicleType>;
  getAdminPrices(): Promise<AdminTransferPrice[]>;
  upsertAdminPrice(request: UpsertTransferPriceRequest): Promise<AdminTransferPrice>;
  getAdminDrivers(): Promise<AdminTransferDriver[]>;
  createAdminDriver(request: UpsertTransferDriverRequest): Promise<AdminTransferDriver>;
  updateAdminDriver(id: number, request: UpsertTransferDriverRequest): Promise<AdminTransferDriver>;
  createAdminDriverTelegramLink(id: number): Promise<TransferDriverLink>;
  getAdminBookings(filters?: AdminTransferBookingFilters): Promise<TransferBooking[]>;
  getAdminBooking(id: number): Promise<TransferBooking>;
  assignAdminBooking(id: number, driverId: number): Promise<TransferBooking>;
  rejectAdminBooking(id: number, reason?: string): Promise<TransferBooking>;
  cancelAdminBooking(id: number, reason?: string): Promise<TransferBooking>;
  completeAdminBooking(id: number): Promise<TransferBooking>;
}
