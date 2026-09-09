import { useTranslation } from "react-i18next";
import type { RentalSearchPrice } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";

export function RentalPricePresentation({
  price,
  locale,
  compact = false,
}: {
  price: RentalSearchPrice;
  locale: string;
  compact?: boolean;
}) {
  const { t } = useTranslation();
  const monthly = price.rentalMonths !== null && price.monthlyPrice !== null;
  return (
    <div className={`rental-price-presentation${compact ? " rental-price-presentation--compact" : ""}`}>
      <div className="rental-price-presentation__primary">
        {monthly ? (
          <>
            <strong>{formatPrice(price.monthlyPrice!, price.currency, locale)}</strong>
            <span>{t("rental.common.perMonth")}</span>
          </>
        ) : (
          <>
            <strong>{formatPrice(price.totalPrice, price.currency, locale)}</strong>
            <span>{t("rental.booking.total")}</span>
          </>
        )}
      </div>
      {monthly ? (
        <div className="rental-price-presentation__monthly-meta">
          {price.longTermDiscountApplied && price.baseMonthlyPrice !== null ? (
            <>
              <del>{formatPrice(price.baseMonthlyPrice, price.currency, locale)}</del>
              <b>−{Math.round(price.discountRate * 100)}%</b>
            </>
          ) : null}
          <span>{t("rental.booking.total")}: {formatPrice(price.totalPrice, price.currency, locale)}</span>
        </div>
      ) : (
        <div className="rental-price-presentation__daily-meta">
          {formatPrice(price.baseDailyPrice, price.currency, locale)} {t("rental.common.perDay")}
          <span> · {t("rental.booking.stay", { count: price.durationDays })}</span>
        </div>
      )}
    </div>
  );
}
