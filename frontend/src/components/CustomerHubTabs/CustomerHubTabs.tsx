import { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import "./CustomerHubTabs.css";

const NOTIFICATIONS_UPDATED_EVENT = "customer-notifications-updated";

function tabClassName({ isActive }: { isActive: boolean }): string {
  return `customer-hub-tabs__link${isActive ? " is-active" : ""}`;
}

export function CustomerHubTabs() {
  const { t } = useTranslation();
  const api = useCustomerApi();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    let active = true;
    const refresh = () => {
      void api.getNotificationUnreadCount()
        .then((count) => { if (active) setUnreadCount(count); })
        .catch(() => { if (active) setUnreadCount(0); });
    };
    refresh();
    window.addEventListener(NOTIFICATIONS_UPDATED_EVENT, refresh);
    return () => {
      active = false;
      window.removeEventListener(NOTIFICATIONS_UPDATED_EVENT, refresh);
    };
  }, [api]);

  const visibleCount = unreadCount > 99 ? "99+" : unreadCount.toString();
  return (
    <nav className="customer-hub-tabs" aria-label={t("customerHub.label")}>
      <NavLink className={tabClassName} to="/account/activity" end>
        <span>{t("customerHub.activity")}</span>
      </NavLink>
      <NavLink className={tabClassName} to="/notifications" end>
        <span>{t("customerHub.notifications")}</span>
        <span
          className={`customer-hub-tabs__badge${unreadCount > 0 ? " is-visible" : ""}`}
          aria-label={unreadCount > 0 ? t("customerHub.unread", { count: unreadCount }) : undefined}
          aria-hidden={unreadCount === 0}
        >
          {visibleCount}
        </span>
      </NavLink>
    </nav>
  );
}
