import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Icon } from "../../components/Icon/Icon";

export function NotFoundPage() {
  const { t } = useTranslation();
  return (
    <div className="page-state">
      <span className="page-state__symbol"><Icon name="info" size={25} /></span>
      <h1>{t("notFound.title")}</h1>
      <Link className="button button--primary" to="/">{t("notFound.action")}</Link>
    </div>
  );
}
