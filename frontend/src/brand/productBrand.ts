export const productBrand = {
  platform: "loco-place",
  services: {
    cleaning: "loco-cleaning",
    rental: "loco-rent",
  },
} as const;

export type BrandService = keyof typeof productBrand.services;

export function getBrandName(service?: BrandService): string {
  return service ? productBrand.services[service] : productBrand.platform;
}
