import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import type { AdminOrderDetails } from "../../domain/admin";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";

export function AdminOrderPage() {
  const { id } = useParams();
  const orderId = Number(id);
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const [details, setDetails] = useState<AdminOrderDetails | null>(null);
  const [error, setError] = useState(false);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setDetails(null);
    setError(false);
    api.getAdminOrder(orderId)
      .then((value) => {
        if (active) setDetails(value);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, orderId]);

  if (error || !Number.isFinite(orderId)) {
    return <ErrorState message={t("admin.orderLoadError")} />;
  }

  if (!details) {
    return <LoadingState />;
  }

  const order = details.order;
  const financial = details.financial;

  return (
    <div className="page page--admin-order">
      <Link className="back-link" to="/admin">
        <Icon name="arrow-left" size={17} /> {t("common.back")}
      </Link>
      <header className="page-header page-header--compact">
        <span className="eyebrow">{t("admin.orderEyebrow", { id: order.id })}</span>
        <h1>{t("admin.orderTitle")}</h1>
      </header>

      <section className="detail-status-card">
        <span className="detail-status-card__label">{t("details.timelineTitle")}</span>
        <OrderStatus status={order.status} withDescription />
      </section>

      <section className="details-card">
        <div className="details-card__price">
          <div>
            <span>{t("admin.customer")}</span>
            <h2>{order.customerName}</h2>
          </div>
          <strong>{formatPrice(order.price, order.currency, locale)}</strong>
        </div>
        <dl className="detail-list">
          <div><dt>{t("details.date")}</dt><dd>{formatDate(order.requestedDate, locale)}</dd></div>
          <div><dt>{t("details.area")}</dt><dd>{t(`areas.${order.area}`)}</dd></div>
          <div><dt>{t("details.address")}</dt><dd>{order.address}</dd></div>
          <div><dt>{t("details.phone")}</dt><dd>{order.phone}</dd></div>
          <div><dt>Telegram ID</dt><dd>{order.telegramUserId}</dd></div>
          <div><dt>{t("admin.cleaner")}</dt><dd>{order.cleanerTelegramUserId ?? t("admin.notAssigned")}</dd></div>
          <div><dt>{t("details.photoReport")}</dt><dd>{details.photoCount}</dd></div>
          <div><dt>{t("admin.financial.basePrice")}</dt><dd>{formatPrice(financial.basePrice, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.baseCommission")}</dt><dd>{formatPrice(financial.baseCommission, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.customerDiscount")}</dt><dd>{formatPrice(financial.customerDiscount, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.partnerPayout")}</dt><dd>{formatPrice(financial.partnerPayout, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.platformNet")}</dt><dd>{formatPrice(financial.platformNet, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.acquisitionSource")}</dt><dd>{financial.acquisitionSource}</dd></div>
          <div><dt>{t("details.customerComment")}</dt><dd>{order.customerComment || t("common.notProvided")}</dd></div>
          {order.cleanerComment ? (
            <div><dt>{t("details.cleanerComment")}</dt><dd>{order.cleanerComment}</dd></div>
          ) : null}
        </dl>
      </section>

      <section className="admin-history">
        <div className="admin-section-heading">
          <div>
            <h2>{t("admin.history.title")}</h2>
            <p>{t("admin.history.subtitle")}</p>
          </div>
          <Icon name="admin" size={24} />
        </div>
        <ol className="admin-timeline">
          {details.events.map((event) => (
            <li key={event.id}>
              <span className="admin-timeline__marker"><Icon name="check" size={15} /></span>
              <div>
                <strong>{t(`admin.events.${event.eventType}`)}</strong>
                <p>
                  {new Intl.DateTimeFormat(locale, {
                    dateStyle: "medium",
                    timeStyle: "short",
                  }).format(new Date(event.occurredAt))}
                  {" / "}{t(`admin.actors.${event.actorType}`)}
                  {event.actorTelegramUserId ? ` / ${event.actorTelegramUserId}` : ""}
                </p>
                {event.details ? <p className="admin-timeline__details">{event.details}</p> : null}
              </div>
            </li>
          ))}
        </ol>
      </section>
    </div>
  );
}
