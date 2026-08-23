import { Link, Navigate, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Icon } from "../../components/Icon/Icon";

export function ServiceCatalogPage() {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();
  const referralCode = searchParams.get("ref")?.trim();

  if (referralCode) {
    return <Navigate replace to={`/cleaning?ref=${encodeURIComponent(referralCode)}`} />;
  }

  return (
    <div className="page page--service-catalog">
      <header className="service-catalog__header">
        <span className="eyebrow">go services</span>
        <h1>{t("catalog.title")}</h1>
        <p>{t("catalog.subtitle")}</p>
      </header>

      <div className="service-catalog__grid">
        <Link className="service-card service-card--rent" to="/rent">
          <span className="service-card__icon"><Icon name="building" size={34} /></span>
          <span className="service-card__copy">
            <small>go-rent</small>
            <strong>{t("catalog.rent.title")}</strong>
            <span>{t("catalog.rent.text")}</span>
          </span>
          <Icon name="arrow-right" size={20} />
        </Link>

        <Link className="service-card service-card--cleaning" to="/cleaning">
          <span className="service-card__icon"><Icon name="sparkles" size={32} /></span>
          <span className="service-card__copy">
            <small>go-cleany</small>
            <strong>{t("catalog.cleaning.title")}</strong>
            <span>{t("catalog.cleaning.text")}</span>
          </span>
          <Icon name="arrow-right" size={20} />
        </Link>
      </div>

      <p className="service-catalog__note">{t("catalog.note")}</p>
    </div>
  );
}
