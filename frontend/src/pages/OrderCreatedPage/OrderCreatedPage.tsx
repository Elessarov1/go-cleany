import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { CleaningOrder } from "../../domain/order";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";

export function OrderCreatedPage() {
  const { id } = useParams();
  const orderId = Number(id);
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const [order, setOrder] = useState<CleaningOrder | null>(null);
  const [error, setError] = useState(false);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    api.getOrder(orderId)
      .then((value) => {
        if (active) setOrder(value);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, orderId]);

  if (error || !Number.isFinite(orderId)) {
    return <ErrorState message={t("details.loadError")} />;
  }

  if (!order) {
    return <LoadingState />;
  }

  return (
    <div className="page page--created">
      <section className="success-card">
        <div className="success-card__icon" aria-hidden="true">
          <Icon name="check" size={38} strokeWidth={2.2} />
        </div>
        <span className="eyebrow">{t("created.eyebrow")}</span>
        <h1>{t("created.title")}</h1>
        <p>{t("created.text")}</p>

        <div className="success-summary">
          <div>
            <span>{t("created.date")}</span>
            <strong>{formatDate(order.requestedDate, locale)}</strong>
          </div>
          <div>
            <span>{t("created.price")}</span>
            <strong>{formatPrice(order.price, order.currency, locale)}</strong>
            {order.customerDiscount > 0 ? (
              <small>
                {t("created.discount", {
                  amount: formatPrice(order.customerDiscount, order.currency, locale),
                })}
              </small>
            ) : null}
          </div>
        </div>
      </section>

      <div className="action-stack">
        <Link className="button button--primary button--full" to={`/cleaning/orders/${order.id}`}>
          {t("created.details")}
        </Link>
        <Link className="button button--secondary button--full" to="/cleaning">
          {t("created.another")}
        </Link>
      </div>
    </div>
  );
}
