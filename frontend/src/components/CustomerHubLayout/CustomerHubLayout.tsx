import { Outlet, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { BrandName } from "../BrandName/BrandName";
import { CustomerHubTabs } from "../CustomerHubTabs/CustomerHubTabs";
import "./CustomerHubLayout.css";

export function CustomerHubLayout() {
  const { t } = useTranslation();
  const { pathname } = useLocation();

  return (
    <div className="page customer-hub-page">
      <header className="page-header page-header--compact customer-hub-page__header">
        <span className="eyebrow"><BrandName /></span>
        <h1>{t("customerHub.title")}</h1>
        <p>{t("customerHub.subtitle")}</p>
      </header>

      <CustomerHubTabs />

      <div className="customer-hub-page__content" key={pathname}>
        <Outlet />
      </div>
    </div>
  );
}
