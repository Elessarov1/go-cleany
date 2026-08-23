import type { RentalBookingProperty, RentalProperty } from "../domain/rental";

type RentalLanguage = "ru" | "en";

export function rentalLanguage(language?: string): RentalLanguage {
  return language === "ru" ? "ru" : "en";
}

export function rentalPropertyTitle(
  property: RentalProperty | RentalBookingProperty,
  language: RentalLanguage,
): string {
  return (language === "ru" ? property.titleRu : property.titleEn) ?? "";
}

export function rentalPropertyDescription(
  property: RentalProperty,
  language: RentalLanguage,
): string {
  return language === "ru"
    ? property.descriptionRu ?? ""
    : property.descriptionEn ?? "";
}

export function rentalCoverUrl(property: RentalProperty): string | undefined {
  return property.media.find((item) => item.cover)?.url ?? property.media[0]?.url;
}
