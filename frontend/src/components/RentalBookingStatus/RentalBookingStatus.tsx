import { useTranslation } from "react-i18next";
import type { RentalBookingStatus as RentalBookingStatusValue } from "../../domain/rental";

interface RentalBookingStatusProps {
  status: RentalBookingStatusValue;
}

export function RentalBookingStatus({ status }: RentalBookingStatusProps) {
  const { t } = useTranslation();
  return <span className={`rental-status rental-status--${status.toLowerCase()}`}>{t(`rental.status.${status}`)}</span>;
}
