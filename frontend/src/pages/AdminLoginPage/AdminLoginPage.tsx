import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../api/AuthApiProvider";
import { Icon } from "../../components/Icon/Icon";

export function AdminLoginPage() {
  const { t } = useTranslation();
  const authentication = useAuthentication();

  return (
    <div className="page-state admin-login">
      <span className="page-state__symbol"><Icon name="admin" size={25} /></span>
      <h1>{t("auth.adminTitle")}</h1>
      <p>{authentication.googleAvailable ? t("auth.adminText") : t("auth.googleUnavailable")}</p>
      {authentication.googleAvailable ? (
        <a className="button button--primary" href={authentication.googleAdminLoginUrl}>
          {t("auth.continueWithGoogle")}
        </a>
      ) : null}
    </div>
  );
}
