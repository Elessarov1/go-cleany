import { useTranslation } from "react-i18next";
import type { CleaningOrderStatus } from "../../domain/order";

interface OrderStatusProps {
  status: CleaningOrderStatus;
  withDescription?: boolean;
}

export function OrderStatus({ status, withDescription = false }: OrderStatusProps) {
  const { t } = useTranslation();

  return (
    <div className={`status status--${status.toLowerCase()}`}>
      <span className="status__dot" aria-hidden="true" />
      <div>
        <strong>{t(`status.${status}`)}</strong>
        {withDescription ? <p>{t(`status.help.${status}`)}</p> : null}
      </div>
    </div>
  );
}

