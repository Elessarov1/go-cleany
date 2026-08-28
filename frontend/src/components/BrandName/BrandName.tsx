import { getBrandName, type BrandService } from "../../brand/productBrand";

interface BrandNameProps {
  service?: BrandService;
}

export function BrandName({ service }: BrandNameProps) {
  const [prefix, suffix] = getBrandName(service).split("-", 2);
  return (
    <span className={`brand-name${service ? " brand-name--service" : ""}`}>
      <span className="brand-name__prefix">{prefix}</span>
      {suffix ? <span className="brand-name__suffix">{` ${suffix}`}</span> : null}
    </span>
  );
}
