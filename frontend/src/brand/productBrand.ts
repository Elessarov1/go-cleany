export const productBrand = {
  prefix: "go",
  services: {
    cleaning: { suffix: "cleany" },
    rental: { suffix: "renty" },
  },
} as const;

export type BrandService = keyof typeof productBrand.services;

export function getBrandName(service?: BrandService): string {
  return service
    ? `${productBrand.prefix}-${productBrand.services[service].suffix}`
    : productBrand.prefix;
}
