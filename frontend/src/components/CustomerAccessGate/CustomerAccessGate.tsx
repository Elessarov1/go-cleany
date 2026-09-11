import { Outlet, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../api/AuthApiProvider";
import { usePlatform } from "../../platform/PlatformProvider";
import { telegramMainMiniAppUrl } from "../../brand/telegram";
import { Icon } from "../Icon/Icon";
import { ErrorState, LoadingState } from "../PageState/PageState";

export function AuthenticationRequiredState({ compact = false }: { compact?: boolean }) {
  const { t } = useTranslation();
  const location = useLocation();
  const authentication = useAuthentication();
  const platform = usePlatform();
  const returnTo = `${location.pathname}${location.search}`;

  if (authentication.status === "LOADING") {
    return (
      <div className={`page-state auth-required${compact ? " auth-required--compact" : ""}`} role="status">
        <span className="loader" aria-hidden="true" />
        <p>{t("common.loading")}</p>
      </div>
    );
  }
  if (authentication.status === "ERROR") {
    return <ErrorState message={t("auth.loadError")} onRetry={authentication.reload} />;
  }

  if (platform.kind === "TELEGRAM") {
    if (platform.getAuthData()) {
      return (
        <div className={`page-state auth-required${compact ? " auth-required--compact" : ""}`}>
          <span className="page-state__symbol"><Icon name="user" size={25} /></span>
          {compact ? <h2>{t("auth.telegramAuthenticationFailedTitle")}</h2> : <h1>{t("auth.telegramAuthenticationFailedTitle")}</h1>}
          <p>{t("auth.telegramAuthenticationFailedText")}</p>
          <button className="button button--primary" type="button" onClick={() => void authentication.reload()}>
            {t("common.retry")}
          </button>
        </div>
      );
    }
    return (
      <div className={`page-state auth-required${compact ? " auth-required--compact" : ""}`}>
        <span className="page-state__symbol"><Icon name="user" size={25} /></span>
        {compact ? <h2>{t("auth.telegramSessionTitle")}</h2> : <h1>{t("auth.telegramSessionTitle")}</h1>}
        <p>{t("auth.telegramSessionText")}</p>
        <div className="action-stack">
          <button
            className="button button--primary"
            type="button"
            onClick={() => platform.openTelegramLink(telegramMainMiniAppUrl())}
          >
            {t("auth.openTelegramMiniApp")}
          </button>
          <button className="button button--secondary" type="button" onClick={() => platform.close()}>
            {t("auth.closeMiniApp")}
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className={`page-state auth-required${compact ? " auth-required--compact" : ""}`}>
      <span className="page-state__symbol"><Icon name="user" size={25} /></span>
      {compact ? <h2>{t("auth.customerTitle")}</h2> : <h1>{t("auth.customerTitle")}</h1>}
      <p>{authentication.googleAvailable ? t("auth.customerText") : t("auth.googleUnavailable")}</p>
      {authentication.googleAvailable ? (
        <a className="button button--primary" href={authentication.googleLoginUrl(returnTo)}>
          {t("auth.continueWithGoogle")}
        </a>
      ) : null}
    </div>
  );
}

export function CustomerAccessGate() {
  const { t } = useTranslation();
  const authentication = useAuthentication();

  if (authentication.status === "LOADING") return <LoadingState />;
  if (authentication.status === "ERROR") {
    return <ErrorState message={t("auth.loadError")} onRetry={authentication.reload} />;
  }
  if (!authentication.current.authenticated) return <AuthenticationRequiredState />;
  return <Outlet />;
}
