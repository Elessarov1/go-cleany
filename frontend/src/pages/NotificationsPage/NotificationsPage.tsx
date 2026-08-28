import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import type { CustomerNotification, CustomerNotificationPage } from "../../domain/customer";
import { Icon } from "../../components/Icon/Icon";

const PAGE_SIZE = 20;

export function NotificationsPage() {
  const { t, i18n } = useTranslation();
  const api = useCustomerApi();
  const navigate = useNavigate();
  const [pageNumber, setPageNumber] = useState(0);
  const [page, setPage] = useState<CustomerNotificationPage | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const load = async (requestedPage: number) => {
    setLoading(true);
    setError(false);
    try {
      setPage(await api.getNotifications(requestedPage, PAGE_SIZE));
      setPageNumber(requestedPage);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { void load(0); }, [api]);

  const open = async (notification: CustomerNotification) => {
    if (!notification.readAt) {
      setPage((current) => current ? {
        ...current,
        content: current.content.map((item) => item.id === notification.id
          ? { ...item, readAt: new Date().toISOString() }
          : item),
      } : current);
      try {
        await api.markNotificationRead(notification.id);
        window.dispatchEvent(new Event("customer-notifications-updated"));
      } finally {
        void navigate(notification.targetPath);
      }
      return;
    }
    void navigate(notification.targetPath);
  };

  const markAllRead = async () => {
    await api.markAllNotificationsRead();
    window.dispatchEvent(new Event("customer-notifications-updated"));
    const now = new Date().toISOString();
    setPage((current) => current ? {
      ...current,
      content: current.content.map((item) => ({ ...item, readAt: item.readAt ?? now })),
    } : current);
  };

  if (loading && !page) return <div className="page-state"><p>{t("common.loading")}</p></div>;
  if (error) return <div className="page-state"><h1>{t("common.errorTitle")}</h1><button className="button button--secondary" type="button" onClick={() => void load(pageNumber)}>{t("common.retry")}</button></div>;

  const notifications = page?.content ?? [];
  const hasUnread = notifications.some((notification) => !notification.readAt);
  return (
    <div className="page notifications-page">
      <header className="notifications-page__header">
        <div>
          <span className="eyebrow">Loco Place</span>
          <h1>{t("notifications.title")}</h1>
          <p>{t("notifications.subtitle")}</p>
        </div>
        {hasUnread ? <button className="button button--secondary" type="button" onClick={() => void markAllRead()}>{t("notifications.readAll")}</button> : null}
      </header>

      {notifications.length === 0 ? (
        <section className="empty-state">
          <div className="empty-state__art"><Icon name="bell" size={42} /></div>
          <h2>{t("notifications.emptyTitle")}</h2>
          <p>{t("notifications.emptyText")}</p>
        </section>
      ) : (
        <div className="notification-list">
          {notifications.map((notification) => (
            <button
              className={`notification-card${notification.readAt ? "" : " is-unread"}`}
              key={notification.id}
              type="button"
              onClick={() => void open(notification)}
            >
              <span className="notification-card__icon"><Icon name="bell" size={20} /></span>
              <span className="notification-card__body">
                <strong>{t(`notifications.types.${notification.type}`)}</strong>
                <time dateTime={notification.createdAt}>{new Intl.DateTimeFormat(i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-US", { dateStyle: "medium", timeStyle: "short" }).format(new Date(notification.createdAt))}</time>
              </span>
              {!notification.readAt ? <span className="notification-card__dot" aria-label={t("notifications.unread")} /> : null}
              <Icon name="arrow-right" size={18} />
            </button>
          ))}
        </div>
      )}

      {(page?.totalPages ?? 0) > 1 ? (
        <nav className="notifications-pagination" aria-label={t("notifications.pagination")}>
          <button className="button button--secondary" type="button" disabled={pageNumber === 0} onClick={() => void load(pageNumber - 1)}>{t("common.back")}</button>
          <span>{pageNumber + 1} / {page?.totalPages}</span>
          <button className="button button--secondary" type="button" disabled={pageNumber + 1 >= (page?.totalPages ?? 0)} onClick={() => void load(pageNumber + 1)}>{t("notifications.next")}</button>
        </nav>
      ) : null}
    </div>
  );
}
