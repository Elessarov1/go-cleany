import { useEffect, useState } from "react";
import { NavLink, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { Icon } from "../Icon/Icon";

export function NotificationBell() {
  const { t } = useTranslation();
  const api = useCustomerApi();
  const location = useLocation();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    let active = true;
    const refresh = () => {
      void api.getNotificationUnreadCount()
        .then((count) => { if (active) setUnreadCount(count); })
        .catch(() => { if (active) setUnreadCount(0); });
    };
    refresh();
    window.addEventListener("customer-notifications-updated", refresh);
    return () => {
      active = false;
      window.removeEventListener("customer-notifications-updated", refresh);
    };
  }, [api, location.pathname]);

  return (
    <NavLink className="notification-bell" to="/notifications" aria-label={t("notifications.title")}>
      <Icon name="bell" size={19} />
      {unreadCount > 0 ? (
        <span className="notification-bell__count">{unreadCount > 99 ? "99+" : unreadCount}</span>
      ) : null}
    </NavLink>
  );
}
