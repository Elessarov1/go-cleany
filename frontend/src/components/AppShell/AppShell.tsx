import { useEffect, useState } from "react";
import { NavLink, Outlet, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../Icon/Icon";
import { LanguageSwitcher } from "../LanguageSwitcher/LanguageSwitcher";
import { PreviewPanel } from "../PreviewPanel/PreviewPanel";

function navClassName({ isActive }: { isActive: boolean }): string {
  return `bottom-nav__link${isActive ? " is-active" : ""}`;
}

export function AppShell() {
  const { t } = useTranslation();
  const location = useLocation();
  const api = useCleaningApi();
  const [hasAdminAccess, setHasAdminAccess] = useState(false);

  useEffect(() => {
    let active = true;
    api.hasAdminAccess()
      .then((allowed) => {
        if (active) setHasAdminAccess(allowed);
      })
      .catch(() => {
        if (active) setHasAdminAccess(false);
      });
    return () => {
      active = false;
    };
  }, [api]);

  const admin = location.pathname.startsWith("/admin");
  const rental = location.pathname.startsWith("/rent") || location.pathname.startsWith("/admin/rent");
  const catalog = location.pathname === "/" || location.pathname === "/admin";
  const service = catalog ? "platform" : rental ? "rent" : "cleaning";
  const serviceHome = rental ? (admin ? "/admin/rent" : "/rent") : catalog ? (admin ? "/admin" : "/") : (admin ? "/admin/cleaning" : "/cleaning");
  const brand = rental ? "rent" : catalog ? "services" : "cleany";

  return (
    <div className="app-frame service-shell" data-service={service} data-layout={admin ? "admin" : "customer"}>
      <div className="app-container">
        <header className="topbar">
          <NavLink className="brand" to={serviceHome} aria-label={t("app.homeLabel")}>
            <span className="brand__mark" aria-hidden="true">
              <Icon name={rental ? "building" : "sparkles"} size={19} strokeWidth={2} />
            </span>
            <span className="brand__word"><b>go</b>{brand === "services" ? "" : `-${brand}`}</span>
          </NavLink>
          <LanguageSwitcher />
        </header>

        <main className="app-content">
          <Outlet />
        </main>

        {!catalog ? (
          <nav
            className={`bottom-nav${hasAdminAccess ? " bottom-nav--admin" : ""}`}
            aria-label={t("app.navigation.label")}
          >
            <NavLink className={navClassName} to={admin && rental ? "/admin/rent/properties" : rental ? "/rent" : "/cleaning"} end>
              <span className="bottom-nav__icon"><Icon name={rental ? "building" : "calendar-plus"} size={21} /></span>
              <span>{t(rental ? "app.navigation.apartments" : "app.navigation.book")}</span>
            </NavLink>
            <NavLink className={navClassName} to={admin && rental ? "/admin/rent/bookings" : rental ? "/rent/bookings" : "/cleaning/orders"}>
              <span className="bottom-nav__icon"><Icon name="clipboard" size={21} /></span>
              <span>{t(rental ? "app.navigation.bookings" : "app.navigation.orders")}</span>
            </NavLink>
            {hasAdminAccess ? (
              <NavLink className={navClassName} to="/admin" end>
                <span className="bottom-nav__icon"><Icon name="admin" size={21} /></span>
                <span>{t("app.navigation.admin")}</span>
              </NavLink>
            ) : null}
          </nav>
        ) : null}
      </div>
      <PreviewPanel />
    </div>
  );
}
