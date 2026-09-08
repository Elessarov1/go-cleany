import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { BrandName } from "../../components/BrandName/BrandName";
import { Icon } from "../../components/Icon/Icon";
import { SUPPORT_EMAIL } from "../../brand/publicContact";
import "./SupportPage.css";

export function SupportPage() {
  const { t } = useTranslation();

  return (
    <div className="page support-page">
      <header className="page-header page-header--compact support-page__header">
        <span className="eyebrow"><BrandName /></span>
        <h1>{t("supportPage.title")}</h1>
        <p>{t("supportPage.subtitle")}</p>
      </header>

      <div className="support-page__options">
        <section className="support-option">
          <span className="support-option__icon"><Icon name="clipboard" size={24} /></span>
          <div className="support-option__content">
            <h2>{t("supportPage.transactionTitle")}</h2>
            <p>{t("supportPage.transactionText")}</p>
            <Link className="button button--primary" to="/account/activity">
              {t("supportPage.activityAction")}
            </Link>
          </div>
        </section>

        <section className="support-option">
          <span className="support-option__icon"><Icon name="mail" size={24} /></span>
          <div className="support-option__content">
            <h2>{t("supportPage.generalTitle")}</h2>
            <p>{t("supportPage.generalText")}</p>
            <a className="support-option__email" href={`mailto:${SUPPORT_EMAIL}`}>
              {SUPPORT_EMAIL}
            </a>
          </div>
        </section>
      </div>
    </div>
  );
}
