import { useEffect, useRef } from "react";
import { NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../api/AuthApiProvider";
import { usePlatform } from "../../platform/PlatformProvider";
import { Icon } from "../Icon/Icon";
import { LanguageSwitcher } from "../LanguageSwitcher/LanguageSwitcher";
import { PreviewPanel } from "../PreviewPanel/PreviewPanel";
import { BrandName } from "../BrandName/BrandName";
import type { BrandService } from "../../brand/productBrand";
import { TelegramLinkNudge } from "../TelegramLinkNudge/TelegramLinkNudge";
import { RouteMetadata } from "../RouteMetadata/RouteMetadata";
import { NotificationBell } from "../NotificationBell/NotificationBell";

function navClassName({ isActive }: { isActive: boolean }): string {
  return `bottom-nav__link${isActive ? " is-active" : ""}`;
}

function logoTarget(pathname: string): string {
  if (pathname === "/admin") return "/";
  if (pathname.startsWith("/admin/")) return "/admin";
  if (pathname === "/cleaning" || pathname === "/rent" || pathname === "/rent/properties") {
    return "/";
  }
  if (pathname.startsWith("/cleaning/")) return "/cleaning";
  if (pathname.startsWith("/rent/")) return "/rent";
  return "/";
}

export function AppShell() {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const platform = usePlatform();
  const authentication = useAuthentication();
  const telegramStartHandled = useRef(false);
  const hasAdminAccess = authentication.isAdmin;
  const standaloneWeb = platform.kind !== "TELEGRAM";
  const showAdminNavigation = !standaloneWeb && hasAdminAccess;

  useEffect(() => {
    if (telegramStartHandled.current || platform.kind !== "TELEGRAM") {
      return;
    }

    const startParameter = platform.getStartParameter();
    if (!startParameter) {
      telegramStartHandled.current = true;
      return;
    }

    telegramStartHandled.current = true;
    const accountLinkPath = `/account/link/telegram?token=${encodeURIComponent(startParameter)}`;
    if (`${location.pathname}${location.search}` !== accountLinkPath) {
      void navigate(accountLinkPath, { replace: true });
    }
  }, [location.pathname, location.search, navigate, platform]);

  const admin = location.pathname.startsWith("/admin");
  const rental = location.pathname.startsWith("/rent") || location.pathname.startsWith("/admin/rent");
  const catalog = location.pathname === "/" || location.pathname === "/admin";
  const customerServiceHome = location.pathname === "/cleaning"
    || location.pathname === "/rent"
    || location.pathname === "/rent/properties";
  const service = catalog ? "platform" : rental ? "rent" : "cleaning";
  const parentRoute = logoTarget(location.pathname);
  const parentLabel = admin && !catalog
    ? t("app.navigation.adminServices")
    : !admin && !catalog && !customerServiceHome
      ? t("app.navigation.serviceHome")
      : t("app.navigation.main");
  const brandService: BrandService | undefined = catalog ? undefined : rental ? "rental" : "cleaning";
  const showLocalNavigation = !catalog && (!admin || rental);
  const showWebAdminSidebar = standaloneWeb && admin && hasAdminAccess;

  return (
    <>
    <RouteMetadata />
    <div
      className="app-frame service-shell"
      data-service={service}
      data-layout={admin ? "admin" : "customer"}
      data-platform={standaloneWeb ? "web" : "telegram"}
    >
      <div className="app-container">
        <header className="topbar">
          <div className="topbar__inner">
            <NavLink
              className="brand"
              to={parentRoute}
              title={parentLabel}
              aria-label={parentLabel}
            >
              <span className="brand__mark" aria-hidden="true">
                <Icon name={rental ? "building" : "sparkles"} size={19} strokeWidth={2} />
              </span>
              <span className="brand__word"><BrandName service={brandService} /></span>
            </NavLink>
            {standaloneWeb && !admin ? (
              <nav className="web-primary-nav" aria-label={t("app.navigation.label")}>
                <NavLink to="/" end>{t("app.navigation.services")}</NavLink>
                <NavLink to="/cleaning"><BrandName service="cleaning" /></NavLink>
                <NavLink to="/rent"><BrandName service="rental" /></NavLink>
              </nav>
            ) : null}
            <div className="topbar__actions">
              {!admin && authentication.status === "READY" && authentication.current.authenticated ? <NotificationBell /> : null}
              {!admin && !catalog && !customerServiceHome ? (
                <NavLink
                  className="topbar__global-link"
                  to="/"
                  title={t("app.navigation.services")}
                  aria-label={t("app.navigation.services")}
                >
                  <Icon name="services" size={18} />
                  <span>{t("app.navigation.services")}</span>
                </NavLink>
              ) : null}
              {admin && !catalog ? (
                <NavLink
                  className="topbar__global-link"
                  to="/"
                  title={t("app.navigation.openApplication")}
                  aria-label={t("app.navigation.openApplication")}
                >
                  <Icon name="home" size={18} />
                  <span>{t("app.navigation.openApplication")}</span>
                </NavLink>
              ) : null}
              {standaloneWeb && authentication.status === "READY" ? (
                authentication.current.authenticated ? (
                  <>
                    <NavLink className="topbar__auth-action" to="/account">
                      <Icon name="user" size={17} />
                      <span>{t("account.title")}</span>
                    </NavLink>
                    <button className="topbar__auth-action" type="button" onClick={() => void authentication.logout()}>
                      <span>{t("auth.logout")}</span>
                    </button>
                  </>
                ) : authentication.googleAvailable ? (
                  <a
                    className="topbar__auth-action"
                    href={authentication.googleLoginUrl(`${location.pathname}${location.search}`)}
                  >
                    <Icon name="user" size={17} />
                    <span>{t("auth.login")}</span>
                  </a>
                ) : null
              ) : null}
              <LanguageSwitcher />
            </div>
          </div>
        </header>

        <div className={`shell-body${showWebAdminSidebar ? " shell-body--admin" : ""}`}>
          {showWebAdminSidebar ? (
            <aside className="admin-sidebar">
              <nav aria-label={t("app.navigation.admin")}>
                <NavLink to="/admin" end>
                  <Icon name="services" size={19} />
                  <span>{t("app.navigation.services")}</span>
                </NavLink>
                <NavLink to="/admin/cleaning">
                  <Icon name="calendar-plus" size={19} />
                  <span><BrandName service="cleaning" /></span>
                </NavLink>
                <NavLink to="/admin/rent/properties">
                  <Icon name="building" size={19} />
                  <span>{t("app.navigation.apartments")}</span>
                </NavLink>
                <NavLink to="/admin/rent/bookings">
                  <Icon name="clipboard" size={19} />
                  <span>{t("app.navigation.bookings")}</span>
                </NavLink>
              </nav>
            </aside>
          ) : null}
          <main className="app-content">
            <Outlet />
          </main>
        </div>

        {showLocalNavigation ? (
          <nav
            className={`bottom-nav${showAdminNavigation && !admin ? " bottom-nav--three-items" : ""}`}
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
            {showAdminNavigation && !admin ? (
              <NavLink className={navClassName} to="/admin" end>
                <span className="bottom-nav__icon"><Icon name="admin" size={21} /></span>
                <span>{t("app.navigation.admin")}</span>
              </NavLink>
            ) : null}
          </nav>
        ) : null}
      </div>
      <TelegramLinkNudge />
      <PreviewPanel />
    </div>
    </>
  );
}
