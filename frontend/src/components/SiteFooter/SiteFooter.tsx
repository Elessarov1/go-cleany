import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { BrandName } from "../BrandName/BrandName";

export function SiteFooter() {
  const { t } = useTranslation();
  const year = new Date().getFullYear();

  return (
    <footer className="site-footer">
      <div className="site-footer__inner">
        <div className="site-footer__brand">
          <strong><BrandName /></strong>
          <p>{t("footer.tagline")}</p>
        </div>

        <nav className="site-footer__group" aria-label={t("footer.services")}> 
          <strong>{t("footer.services")}</strong>
          <Link to="/cleaning"><BrandName service="cleaning" /></Link>
          <Link to="/rent"><BrandName service="rental" /></Link>
        </nav>

        <nav className="site-footer__group" aria-label={t("footer.legal")}> 
          <strong>{t("footer.legal")}</strong>
          <Link to="/privacy">{t("footer.privacy")}</Link>
          <Link to="/terms">{t("footer.terms")}</Link>
        </nav>

        <div className="site-footer__bottom">
          <span>© {year} Loco Place</span>
          <span>Alanya, Türkiye</span>
        </div>
      </div>
    </footer>
  );
}
