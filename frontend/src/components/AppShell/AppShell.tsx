import { useEffect, useState } from "react";
import { NavLink, Outlet } from "react-router-dom";
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

  return (
    <div className="app-frame">
      <div className="app-container">
        <header className="topbar">
          <NavLink className="brand" to="/" aria-label={t("app.homeLabel")}>
            <span className="brand__mark" aria-hidden="true">
              <Icon name="sparkles" size={19} strokeWidth={2} />
            </span>
            <span className="brand__word"><b>go</b>-cleany</span>
          </NavLink>
          <LanguageSwitcher />
        </header>

        <main className="app-content">
          <Outlet />
        </main>

        <nav
          className={`bottom-nav${hasAdminAccess ? " bottom-nav--admin" : ""}`}
          aria-label={t("app.navigation.label")}
        >
          <NavLink className={navClassName} to="/" end>
            <span className="bottom-nav__icon"><Icon name="calendar-plus" size={21} /></span>
            <span>{t("app.navigation.book")}</span>
          </NavLink>
          <NavLink className={navClassName} to="/orders">
            <span className="bottom-nav__icon"><Icon name="clipboard" size={21} /></span>
            <span>{t("app.navigation.orders")}</span>
          </NavLink>
          {hasAdminAccess ? (
            <NavLink className={navClassName} to="/admin">
              <span className="bottom-nav__icon"><Icon name="admin" size={21} /></span>
              <span>{t("app.navigation.admin")}</span>
            </NavLink>
          ) : null}
        </nav>
      </div>
      <PreviewPanel />
    </div>
  );
}
