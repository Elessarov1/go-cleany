import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import type { CleaningOrder } from "../../domain/order";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";

export function OrdersPage() {
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const location = useLocation();
  const [orders, setOrders] = useState<CleaningOrder[] | null>(null);
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setError(false);
    setOrders(null);
    api.getOrders()
      .then((value) => {
        if (active) setOrders(value);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey, location.search]);

  if (error) {
    return (
      <ErrorState
        message={t("orders.loadError")}
        onRetry={() => setReloadKey((value) => value + 1)}
      />
    );
  }

  if (!orders) {
    return <LoadingState />;
  }

  return (
    <div className="page">
      <header className="page-header">
        <span className="eyebrow">{t("orders.eyebrow")}</span>
        <h1>{t("orders.title")}</h1>
        <p>{t("orders.subtitle")}</p>
      </header>

      {orders.length === 0 ? (
        <section className="empty-state">
          <div className="empty-state__art" aria-hidden="true">
            <Icon name="clipboard" size={44} strokeWidth={1.5} />
          </div>
          <h2>{t("orders.emptyTitle")}</h2>
          <p>{t("orders.emptyText")}</p>
          <Link className="button button--primary" to="/">
            {t("orders.emptyAction")}
          </Link>
        </section>
      ) : (
        <div className="order-list">
          {orders.map((order) => (
            <Link className="order-card" key={order.id} to={`/orders/${order.id}`}>
              <div className="order-card__top">
                <time dateTime={order.requestedDate}>
                  {formatDate(order.requestedDate, locale)}
                </time>
                <OrderStatus status={order.status} />
              </div>
              <div className="order-card__body">
                <div className="order-card__icon"><Icon name="broom" size={24} /></div>
                <div>
                  <h2>
                    {t(`apartments.${order.apartmentType}`)} · {t(`cleaning.${order.cleaningType}.title`)}
                  </h2>
                  <p>{t(`areas.${order.area}`)} · #{order.id}</p>
                </div>
                <strong>{formatPrice(order.price, order.currency, locale)}</strong>
              </div>
              <span className="order-card__link">{t("orders.open")} <Icon name="arrow-right" size={16} /></span>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
