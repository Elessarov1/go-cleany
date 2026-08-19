import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import type { CleaningOrder } from "../../domain/order";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";

export function OrderDetailsPage() {
  const { id } = useParams();
  const orderId = Number(id);
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const location = useLocation();
  const [order, setOrder] = useState<CleaningOrder | null>(null);
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [isCancelling, setIsCancelling] = useState(false);
  const [cancelError, setCancelError] = useState(false);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setOrder(null);
    setError(false);
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
  }, [api, orderId, reloadKey, location.search]);

  const cancelOrder = async () => {
    if (!window.confirm(t("details.cancelConfirm"))) return;
    try {
      setIsCancelling(true);
      setCancelError(false);
      setOrder(await api.cancelOrder(orderId));
    } catch {
      setCancelError(true);
    } finally {
      setIsCancelling(false);
    }
  };

  if (error || !Number.isFinite(orderId)) {
    return (
      <ErrorState
        message={t("details.loadError")}
        onRetry={() => setReloadKey((value) => value + 1)}
      />
    );
  }

  if (!order) {
    return <LoadingState />;
  }

  return (
    <div className="page page--details">
      <Link className="back-link" to="/orders"><Icon name="arrow-left" size={17} /> {t("common.back")}</Link>
      <header className="page-header page-header--compact">
        <span className="eyebrow">{t("details.eyebrow", { id: order.id })}</span>
        <h1>{t("details.title")}</h1>
      </header>

      <section className="detail-status-card">
        <span className="detail-status-card__label">{t("details.timelineTitle")}</span>
        <OrderStatus status={order.status} withDescription />
      </section>

      <section className="details-card">
        <div className="details-card__price">
          <div>
            <span>{t("details.cleaning")}</span>
            <h2>{t(`cleaning.${order.cleaningType}.title`)}</h2>
          </div>
          <div className="details-card__price-value">
            {order.customerDiscount > 0 ? (
              <span>{formatPrice(order.basePrice, order.currency, locale)}</span>
            ) : null}
            <strong>{formatPrice(order.finalCustomerPrice, order.currency, locale)}</strong>
          </div>
        </div>
        <dl className="detail-list">
          <div><dt>{t("details.date")}</dt><dd>{formatDate(order.requestedDate, locale)}</dd></div>
          <div><dt>{t("details.area")}</dt><dd>{t(`areas.${order.area}`)}</dd></div>
          <div><dt>{t("details.address")}</dt><dd>{order.address}</dd></div>
          <div>
            <dt>{t("details.apartment")}</dt>
            <dd>
              {t(`apartments.${order.apartmentType}`)}
              {order.duplex ? ` · ${t("create.apartment.duplexTitle")}` : ""}
            </dd>
          </div>
          <div><dt>{t("details.phone")}</dt><dd>{order.phone}</dd></div>
          {order.customerDiscount > 0 ? (
            <div>
              <dt>{t("details.discount")}</dt>
              <dd>−{formatPrice(order.customerDiscount, order.currency, locale)}</dd>
            </div>
          ) : null}
          <div><dt>{t("details.customerComment")}</dt><dd>{order.customerComment || t("common.notProvided")}</dd></div>
          {order.cleanerComment ? (
            <div><dt>{t("details.cleanerComment")}</dt><dd>{order.cleanerComment}</dd></div>
          ) : null}
          {order.photoCount ? (
            <div><dt>{t("details.photoReport")}</dt><dd>{t("details.photos", { count: order.photoCount })}</dd></div>
          ) : null}
        </dl>
      </section>

      <div className="payment-note payment-note--standalone">
        <span><Icon name="wallet" size={17} /></span>
        <div><strong>{t("details.price")}</strong><p>{t("details.payment")}</p></div>
      </div>

      {cancelError ? <p className="form-alert" role="alert">{t("details.cancelError")}</p> : null}
      {order.status === "NEW" ? (
        <button
          className="button button--danger button--full"
          type="button"
          disabled={isCancelling}
          onClick={() => void cancelOrder()}
        >
          {isCancelling ? t("details.cancelling") : t("details.cancel")}
        </button>
      ) : null}
    </div>
  );
}
