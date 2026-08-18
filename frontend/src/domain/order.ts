export type ServiceArea = "MAHMUTLAR" | "KARGICAK" | "KESTEL";

export type ApartmentType =
  | "STUDIO"
  | "ONE_PLUS_ONE"
  | "TWO_PLUS_ONE"
  | "THREE_PLUS_ONE"
  | "FOUR_PLUS_ONE";

export type CleaningType = "REGULAR" | "DEEP";

export type CleaningOrderStatus =
  | "NEW"
  | "ACCEPTED"
  | "AWAITING_REPORT"
  | "COMPLETED"
  | "REJECTED"
  | "CANCELLED";

export interface CreateCleaningOrderRequest {
  area: ServiceArea;
  address: string;
  apartmentType: ApartmentType;
  duplex: boolean;
  cleaningType: CleaningType;
  requestedDate: string;
  phone: string;
  comment?: string;
}

export interface CleaningOrder {
  id: number;
  telegramUserId: number;
  telegramUsername?: string;
  customerName: string;
  phone: string;
  area: ServiceArea;
  address: string;
  apartmentType: ApartmentType;
  duplex: boolean;
  cleaningType: CleaningType;
  price: number;
  currency: string;
  requestedDate: string;
  customerComment?: string;
  cleanerComment?: string;
  cleanerTelegramUserId?: number;
  status: CleaningOrderStatus;
  createdAt: string;
  acceptedAt?: string;
  completedAt?: string;
  photoCount?: number;
}

