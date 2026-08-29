import { useTranslation } from "react-i18next";
import type { TransferBookingStatus as Status } from "../../domain/transfer";

export function TransferBookingStatus({ status }: { status: Status }) {
  const { t } = useTranslation();
  return <span className={`status-badge transfer-status transfer-status--${status.toLowerCase()}`}>{t(`transfer.status.${status}`)}</span>;
}
