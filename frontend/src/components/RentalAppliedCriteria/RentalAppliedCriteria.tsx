import { useTranslation } from "react-i18next";
import type { RentalSearchCriteria } from "../../domain/rental";
import { formatDate } from "../../utils/format";
import { Icon } from "../Icon/Icon";

export function RentalAppliedCriteria({ criteria, locale }: {
  criteria: RentalSearchCriteria;
  locale: string;
}) {
  const { t } = useTranslation();
  if (criteria.mode === "BROWSE_ALL") {
    return <div className="rental-applied-criteria"><Icon name="building" size={18} />{t("rental.search.allApartments")}</div>;
  }
  return (
    <div className="rental-applied-criteria">
      <Icon name="calendar-plus" size={18} />
      <span>
        <strong>{formatDate(criteria.checkInDate!, locale)} — {formatDate(criteria.checkOutDate!, locale)}</strong>
        <small>
          {criteria.mode === "MONTHLY"
            ? t("rental.booking.months", { count: criteria.rentalMonths })
            : t("rental.booking.stay", { count: criteria.durationDays })}
          {" · "}{t("rental.search.guestsSummary", { count: criteria.guests })}
        </small>
      </span>
    </div>
  );
}
