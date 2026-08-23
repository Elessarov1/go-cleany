import { productBrand, type BrandService } from "../../brand/productBrand";

interface BrandNameProps {
  service?: BrandService;
}

export function BrandName({ service }: BrandNameProps) {
  return (
    <span className="brand-name">
      <span className="brand-name__prefix">{productBrand.prefix}</span>
      {service ? <span className="brand-name__suffix">-{productBrand.services[service].suffix}</span> : null}
    </span>
  );
}
