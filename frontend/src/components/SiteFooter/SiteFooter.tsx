import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { BrandName } from "../BrandName/BrandName";
import { Icon } from "../Icon/Icon";
import { SUPPORT_EMAIL } from "../../brand/publicContact";

export function SiteFooter() {
  const { t } = useTranslation();
  const year = new Date().getFullYear();
  const configuredUsername = import.meta.env.VITE_TELEGRAM_BOT_USERNAME?.trim().replace(/^@/, "");
  const telegramUsername = configuredUsername && /^[A-Za-z0-9_]{5,32}$/.test(configuredUsername)
    ? configuredUsername
    : "go_cleany_bot";

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
          <Link to="/transfer"><BrandName service="transfer" /></Link>
        </nav>

        <nav className="site-footer__group" aria-label={t("footer.legal")}> 
          <strong>{t("footer.legal")}</strong>
          <Link to="/privacy">{t("footer.privacy")}</Link>
          <Link to="/terms">{t("footer.terms")}</Link>
          <Link to="/account/delete">{t("footer.deleteAccount")}</Link>
        </nav>

        <nav className="site-footer__group" aria-label={t("footer.contacts")}>
          <strong>{t("footer.contacts")}</strong>
          <a
            className="site-footer__contact-link"
            href={`https://t.me/${telegramUsername}`}
            target="_blank"
            rel="noreferrer"
          >
            <img src="/assets/icons/telegram.svg" alt="" />
            {t("footer.telegram")}
          </a>
          <a className="site-footer__contact-link" href={`mailto:${SUPPORT_EMAIL}`}>
            <Icon name="mail" size={16} />
            {SUPPORT_EMAIL}
          </a>
        </nav>

        <div className="site-footer__bottom">
          <span>© {year} Loco Place</span>
          <span>Alanya, Türkiye</span>
        </div>
      </div>
    </footer>
  );
}
