import type { RentalBookingProperty, RentalProperty } from "../domain/rental";

type RentalLanguage = "ru" | "en";

export function rentalLanguage(language?: string): RentalLanguage {
  return language === "ru" ? "ru" : "en";
}

export function rentalPropertyTitle(
  property: RentalProperty | RentalBookingProperty,
  language: RentalLanguage,
): string {
  return language === "ru"
    ? property.titleRu ?? property.titleEn ?? ""
    : property.titleEn ?? property.titleRu ?? "";
}

export function rentalPropertyDescription(
  property: RentalProperty,
  _language: RentalLanguage,
): string {
  return property.descriptionEn ?? "";
}

export function rentalCoverUrl(property: RentalProperty): string | undefined {
  const cover = property.media.find((item) => item.cover) ?? property.media[0];
  return cover?.cardUrl ?? cover?.url;
}
