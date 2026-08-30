import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { Icon, type IconName } from "../../components/Icon/Icon";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import { RentalBookingStatus } from "../../components/RentalBookingStatus/RentalBookingStatus";
import { TransferBookingStatus } from "../../components/TransferBookingStatus/TransferBookingStatus";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { CustomerActivity, CustomerActivityItem } from "../../domain/customer";
import type { CleaningOrderStatus } from "../../domain/order";
import type { RentalBookingStatus as RentalStatus } from "../../domain/rental";
import type { TransferBookingStatus as TransferStatus } from "../../domain/transfer";
import { formatDate } from "../../utils/format";
import "./CustomerActivityPage.css";

function serviceIcon(service: CustomerActivityItem["service"]): IconName {
  if (service === "RENTAL") return "building";
  if (service === "TRANSFER") return "car";
  return "sparkles";
}

function ActivityStatus({ item }: { item: CustomerActivityItem }) {
  if (item.service === "RENTAL") {
    return <RentalBookingStatus status={item.status as RentalStatus} />;
  }
  if (item.service === "TRANSFER") {
    return <TransferBookingStatus status={item.status as TransferStatus} />;
  }
  return <OrderStatus status={item.status as CleaningOrderStatus} />;
}

function ActivityCard({ item, historical }: { item: CustomerActivityItem; historical: boolean }) {
  const { t, i18n } = useTranslation();
  const russian = i18n.resolvedLanguage === "ru";
  const locale = russian ? "ru-RU" : "en-US";
  const title = russian ? item.titleRu : item.titleEn;
  const subtitle = russian ? item.subtitleRu : item.subtitleEn;
  const date = item.scheduledEndDate
    ? t("activity.dateRange", { from: formatDate(item.scheduledDate, locale), to: formatDate(item.scheduledEndDate, locale) })
    : formatDate(item.scheduledDate, locale);
  const price = new Intl.NumberFormat(locale, { maximumFractionDigits: 2 }).format(item.amount);

  return (
    <Link className="activity-card" to={item.targetPath}>
      <span className={`activity-card__service activity-card__service--${item.service.toLowerCase()}`}>
        <Icon name={serviceIcon(item.service)} size={22} />
      </span>
      <span className="activity-card__body">
        <span className="activity-card__eyebrow">{t(`activity.services.${item.service}`)}</span>
        <strong>{title}</strong>
        <span className="activity-card__subtitle">{subtitle}</span>
        <span className="activity-card__date">
          {date}{item.scheduledTime ? ` · ${item.scheduledTime.slice(0, 5)}` : ""}
        </span>
        {historical ? (
          <span className="activity-card__occurred">
            {t("activity.updated", { date: new Intl.DateTimeFormat(locale, { dateStyle: "medium" }).format(new Date(item.occurredAt)) })}
          </span>
        ) : null}
      </span>
      <span className="activity-card__aside">
        <ActivityStatus item={item} />
        <strong>{price} {item.currency}</strong>
        <span className="activity-card__open">{t("activity.open")} <Icon name="arrow-right" size={17} /></span>
      </span>
    </Link>
  );
}

export function CustomerActivityPage() {
  const { t } = useTranslation();
  const api = useCustomerApi();
  const [activity, setActivity] = useState<CustomerActivity | null>(null);
  const [error, setError] = useState(false);

  const load = async () => {
    try {
      setError(false);
      setActivity(await api.getActivity());
    } catch {
      setError(true);
    }
  };

  useEffect(() => { void load(); }, [api]);

  const empty = activity
    ? activity.activeAndUpcoming.length === 0 && activity.history.length === 0
    : false;
  return (
    <div className="activity-page">
      {!activity && !error ? (
        <LoadingState />
      ) : !activity ? (
        <ErrorState message={t("activity.loadError")} onRetry={() => void load()} />
      ) : empty ? (
        <section className="empty-state activity-empty">
          <div className="empty-state__art"><Icon name="clipboard" size={42} /></div>
          <h2>{t("activity.emptyTitle")}</h2>
          <p>{t("activity.emptyText")}</p>
          <Link className="button button--primary" to="/">{t("activity.emptyAction")}</Link>
        </section>
      ) : (
        <div className="activity-sections">
          <section className="activity-section">
            <div className="activity-section__heading">
              <h2>{t("activity.activeTitle")}</h2>
              <span>{activity.activeAndUpcoming.length}</span>
            </div>
            {activity.activeAndUpcoming.length === 0 ? (
              <p className="activity-section__empty">{t("activity.activeEmpty")}</p>
            ) : (
              <div className="activity-list">
                {activity.activeAndUpcoming.map((item) => <ActivityCard key={`${item.service}-${item.entityId}`} item={item} historical={false} />)}
              </div>
            )}
          </section>

          <section className="activity-section">
            <div className="activity-section__heading">
              <h2>{t("activity.historyTitle")}</h2>
              <span>{activity.history.length}</span>
            </div>
            {activity.history.length === 0 ? (
              <p className="activity-section__empty">{t("activity.historyEmpty")}</p>
            ) : (
              <div className="activity-list">
                {activity.history.map((item) => <ActivityCard key={`${item.service}-${item.entityId}`} item={item} historical />)}
              </div>
            )}
          </section>
        </div>
      )}
    </div>
  );
}
