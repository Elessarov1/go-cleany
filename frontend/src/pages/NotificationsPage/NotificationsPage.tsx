import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import type { CustomerNotification, CustomerNotificationPage } from "../../domain/customer";
import { actionTargetPath } from "../../domain/action";
import { Icon } from "../../components/Icon/Icon";

const PAGE_SIZE = 20;

export function NotificationsPage() {
  const { t, i18n } = useTranslation();
  const api = useCustomerApi();
  const navigate = useNavigate();
  const [page, setPage] = useState<CustomerNotificationPage | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const load = async (cursor: string | null = null, append = false) => {
    setLoading(true);
    setError(false);
    try {
      const next = await api.getNotifications(cursor, PAGE_SIZE);
      setPage((current) => append && current ? { ...next, items: [...current.items, ...next.items] } : next);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { void load(); }, [api]);

  const open = async (notification: CustomerNotification) => {
    if (!notification.readAt) {
      setPage((current) => current ? {
        ...current,
        items: current.items.map((item) => item.id === notification.id
          ? { ...item, readAt: new Date().toISOString() }
          : item),
      } : current);
      try {
        await api.markNotificationRead(notification.id);
        window.dispatchEvent(new Event("customer-notifications-updated"));
      } finally {
        void navigate(actionTargetPath(notification.action));
      }
      return;
    }
    void navigate(actionTargetPath(notification.action));
  };

  const markAllRead = async () => {
    await api.markAllNotificationsRead();
    window.dispatchEvent(new Event("customer-notifications-updated"));
    const now = new Date().toISOString();
    setPage((current) => current ? {
      ...current,
      items: current.items.map((item) => ({ ...item, readAt: item.readAt ?? now })),
    } : current);
  };

  const notifications = page?.items ?? [];
  const hasUnread = notifications.some((notification) => !notification.readAt);
  return (
    <div className="notifications-page">
      <div className="notifications-page__toolbar">
        <button
          className={`button button--secondary${!loading && !error && hasUnread ? "" : " is-hidden"}`}
          type="button"
          disabled={loading || error || !hasUnread}
          onClick={() => void markAllRead()}
        >
          {t("notifications.readAll")}
        </button>
      </div>

      {loading && !page ? (
        <div className="page-state"><p>{t("common.loading")}</p></div>
      ) : error ? (
        <div className="page-state"><h2>{t("common.errorTitle")}</h2><button className="button button--secondary" type="button" onClick={() => void load()}>{t("common.retry")}</button></div>
      ) : notifications.length === 0 ? (
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
                <strong>{t(`notifications.types.${notification.type}`, {
                  defaultValue: t("notifications.types.UNKNOWN"),
                })}</strong>
                <time dateTime={notification.createdAt}>{new Intl.DateTimeFormat(i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-US", { dateStyle: "medium", timeStyle: "short" }).format(new Date(notification.createdAt))}</time>
              </span>
              {!notification.readAt ? <span className="notification-card__dot" aria-label={t("notifications.unread")} /> : null}
              <Icon name="arrow-right" size={18} />
            </button>
          ))}
        </div>
      )}

      {!loading && !error && page?.hasMore ? (
        <nav className="notifications-pagination" aria-label={t("notifications.pagination")}>
          <button className="button button--secondary" type="button" onClick={() => void load(page.nextCursor, true)}>{t("notifications.next")}</button>
        </nav>
      ) : null}
    </div>
  );
}
