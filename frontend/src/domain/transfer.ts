export type TransferDirection = "TO_AIRPORT" | "FROM_AIRPORT";

export type TransferBookingStatus =
  | "REQUESTED"
  | "CONFIRMED"
  | "COMPLETED"
  | "CANCELLED"
  | "REJECTED";

export interface TransferAirport {
  id: number;
  code: string;
  nameRu: string;
  nameEn: string;
}

export interface TransferVehicleType {
  id: number;
  code: string;
  nameRu: string;
  nameEn: string;
  maxPassengers: number;
  maxLuggage: number;
}

export interface TransferPrice {
  airportId: number;
  vehicleTypeId: number;
  direction: TransferDirection;
  amount: number;
  currency: string;
}

export interface TransferConfiguration {
  earliestBookingDate: string;
  latestBookingDate: string;
  timeSlotMinutes: number;
  airports: TransferAirport[];
  vehicleTypes: TransferVehicleType[];
  prices: TransferPrice[];
}

export interface CreateTransferBookingRequest {
  direction: TransferDirection;
  airportId: number;
  vehicleTypeId: number;
  pickupDate: string;
  pickupTime: string;
  address: string;
  passengerCount: number;
  luggageCount: number;
  flightNumber?: string | null;
  scheduledArrivalTime?: string | null;
  phone: string;
  comment?: string | null;
}

export interface TransferBooking {
  id: number;
  direction: TransferDirection;
  airportCode: string;
  airportNameRu: string;
  airportNameEn: string;
  vehicleCode: string;
  vehicleNameRu: string;
  vehicleNameEn: string;
  pickupDate: string;
  pickupTime: string;
  address: string;
  passengerCount: number;
  luggageCount: number;
  flightNumber: string | null;
  scheduledArrivalTime: string | null;
  customerName: string;
  phone: string;
  comment: string | null;
  priceAmount: number;
  priceCurrency: string;
  status: TransferBookingStatus;
  driverId: number | null;
  driverName: string | null;
  createdAt: string;
  confirmedAt: string | null;
  completedAt: string | null;
  cancelledAt: string | null;
  rejectedAt: string | null;
  statusReason: string | null;
}

export interface AdminTransferAirport extends TransferAirport {
  enabled: boolean;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface AdminTransferVehicleType extends TransferVehicleType {
  enabled: boolean;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface AdminTransferPrice extends TransferPrice {
  id: number;
  airportCode: string;
  vehicleCode: string;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export type DriverTelegramStatus = "NOT_CONFIGURED" | "AWAITING_AUTHORIZATION" | "CONNECTED";

export interface AdminTransferDriver {
  id: number;
  name: string;
  phone: string;
  enabled: boolean;
  configuredTelegramUserId: number | null;
  verifiedTelegramUserId: number | null;
  telegramChatId: number | null;
  telegramNotificationsEnabled: boolean;
  telegramBotAuthorizedAt: string | null;
  telegramStatus: DriverTelegramStatus;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface TransferDriverLink {
  url: string;
  expiresAt: string;
}

export interface CreateTransferAirportRequest {
  code: string;
  nameRu: string;
  nameEn: string;
  enabled: boolean;
  sortOrder: number;
}

export type UpdateTransferAirportRequest = Omit<CreateTransferAirportRequest, "code">;

export interface CreateTransferVehicleRequest {
  code: string;
  nameRu: string;
  nameEn: string;
  maxPassengers: number;
  maxLuggage: number;
  enabled: boolean;
  sortOrder: number;
}

export type UpdateTransferVehicleRequest = Omit<CreateTransferVehicleRequest, "code">;

export interface UpsertTransferPriceRequest {
  airportId: number;
  vehicleTypeId: number;
  direction: TransferDirection;
  amount: number;
  currency: string;
  enabled: boolean;
}

export interface UpsertTransferDriverRequest {
  name: string;
  phone: string;
  enabled: boolean;
  telegramUserId: number | null;
}

export interface AdminTransferBookingFilters {
  status?: TransferBookingStatus;
  date?: string;
  airportId?: number;
}
