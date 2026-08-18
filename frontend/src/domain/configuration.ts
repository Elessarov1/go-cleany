import type { ApartmentType, CleaningType, ServiceArea } from "./order";

export interface ApartmentPrice {
  type: ApartmentType;
  regularPrice: number;
  deepPrice: number;
}

export interface CleaningConfiguration {
  areas: ServiceArea[];
  apartmentTypes: ApartmentPrice[];
  duplexSurcharges: Record<CleaningType, number>;
  bookingDaysAhead: number;
  currency: string;
}

