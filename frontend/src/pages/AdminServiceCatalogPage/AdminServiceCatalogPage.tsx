import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Icon } from "../../components/Icon/Icon";

export function AdminServiceCatalogPage() {
  const { t } = useTranslation();
  return (
    <div className="page page--admin-service-catalog">
      <header className="page-header admin-platform-header">
        <span className="eyebrow">go services / admin</span>
        <h1>{t("adminPlatform.title")}</h1>
        <p>{t("adminPlatform.subtitle")}</p>
      </header>
      <div className="admin-service-grid">
        <Link className="admin-service-card admin-service-card--rent" to="/admin/rent">
          <span><Icon name="building" size={31} /></span>
          <div><small>go-rent</small><strong>{t("adminPlatform.rent")}</strong><p>{t("adminPlatform.rentText")}</p></div>
          <Icon name="arrow-right" size={20} />
        </Link>
        <Link className="admin-service-card admin-service-card--cleaning" to="/admin/cleaning">
          <span><Icon name="sparkles" size={29} /></span>
          <div><small>go-cleany</small><strong>{t("adminPlatform.cleaning")}</strong><p>{t("adminPlatform.cleaningText")}</p></div>
          <Icon name="arrow-right" size={20} />
        </Link>
      </div>
    </div>
  );
}
