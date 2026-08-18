import type { CleaningConfiguration } from "./configuration";
import type { ApartmentType, CleaningType } from "./order";

export function calculateDisplayedPrice(
  configuration: CleaningConfiguration,
  apartmentType: ApartmentType,
  cleaningType: CleaningType,
  duplex: boolean,
): number {
  const apartment = configuration.apartmentTypes.find(
    ({ type }) => type === apartmentType,
  );

  if (!apartment) {
    return 0;
  }

  const basePrice =
    cleaningType === "REGULAR"
      ? apartment.regularPrice
      : apartment.deepPrice;

  return basePrice + (duplex ? configuration.duplexSurcharges[cleaningType] : 0);
}

export function formatPrice(
  value: number,
  currency: string,
  locale: string,
): string {
  return new Intl.NumberFormat(locale, {
    style: "currency",
    currency,
    maximumFractionDigits: 0,
  }).format(value);
}

