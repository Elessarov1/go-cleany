import { useCallback, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ApiError } from "../../api/ApiError";
import { useAuthentication } from "../../api/AuthApiProvider";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { ConfirmationDialog } from "../../components/ConfirmationDialog/ConfirmationDialog";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { usePlatform } from "../../platform/PlatformProvider";
import "./AccountDeletionPage.css";

const WEB_REAUTHENTICATION_AT = "loco-place.account-deletion-google-reauthenticated-at";
const WEB_REAUTHENTICATION_MAX_AGE_MS = 4 * 60 * 1_000;

type DeletionError = "ADMIN" | "BLOCKED" | "EXPIRED" | "NETWORK" | null;

function hasFreshWebReauthentication(): boolean {
  const value = Number(window.sessionStorage.getItem(WEB_REAUTHENTICATION_AT));
  return Number.isFinite(value) && value > 0 && Date.now() - value < WEB_REAUTHENTICATION_MAX_AGE_MS;
}

function isExpiredProof(error: ApiError): boolean {
  return error.code === "auth_challenge_expired"
    || error.code === "account_challenge_invalid"
    || error.code === "reauthentication_identity_mismatch"
    || error.code === "telegram_init_data_expired"
    || error.code === "invalid_telegram_init_data";
}

export function AccountDeletionPage() {
  const { t } = useTranslation();
  const api = useCustomerApi();
  const authentication = useAuthentication();
  const platform = usePlatform();
  const navigate = useNavigate();
  const [confirmed, setConfirmed] = useState(false);
  const [pending, setPending] = useState(false);
  const requestInFlight = useRef(false);
  const [error, setError] = useState<DeletionError>(null);
  const [webReauthenticated, setWebReauthenticated] = useState(hasFreshWebReauthentication);
  const isTelegram = platform.kind === "TELEGRAM";

  const startGoogleReauthentication = useCallback(() => {
    window.sessionStorage.setItem(WEB_REAUTHENTICATION_AT, String(Date.now()));
    window.location.assign(authentication.googleLoginUrl("/account/delete?reauthenticated=1"));
  }, [authentication]);

  const deleteAccount = useCallback(async () => {
    if (requestInFlight.current) return;
    requestInFlight.current = true;
    setPending(true);
    setError(null);
    try {
      const challenge = await api.createAccountDeletionRequest();
      const telegramInitData = isTelegram ? platform.getAuthData() : null;
      if (isTelegram && !telegramInitData) {
        setError("EXPIRED");
        return;
      }
      await api.confirmAccountDeletion(challenge.id, telegramInitData);
      window.sessionStorage.removeItem(WEB_REAUTHENTICATION_AT);
      authentication.invalidate();
      navigate("/account/deleted", { replace: true });
    } catch (failure: unknown) {
      if (failure instanceof ApiError
        && failure.code === "account_deletion_blocked_by_active_operation") {
        setError("BLOCKED");
      } else if (failure instanceof ApiError
        && failure.code === "admin_account_self_deletion_forbidden") {
        setError("ADMIN");
      } else if (failure instanceof ApiError && isExpiredProof(failure)) {
        window.sessionStorage.removeItem(WEB_REAUTHENTICATION_AT);
        setWebReauthenticated(false);
        setError("EXPIRED");
      } else {
        setError("NETWORK");
      }
    } finally {
      requestInFlight.current = false;
      setPending(false);
      setConfirmed(false);
    }
  }, [api, authentication, isTelegram, navigate, platform]);

  if (authentication.status === "LOADING") return <LoadingState />;
  if (authentication.status === "ERROR") {
    return <ErrorState message={t("accountDeletion.authError")} onRetry={authentication.reload} />;
  }

  const authenticated = authentication.current.authenticated;
  const admin = authentication.isAdmin;
  const readyForConfirmation = authenticated
    && !admin
    && (isTelegram || webReauthenticated);

  return (
    <div className="page account-deletion-page">
      <header className="page-header page-header--compact">
        <span className="eyebrow"><BrandName /></span>
        <h1>{t("accountDeletion.title")}</h1>
        <p>{t("accountDeletion.subtitle")}</p>
      </header>

      <section className="account-deletion-card" aria-labelledby="account-deletion-effects">
        <h2 id="account-deletion-effects">{t("accountDeletion.effectsTitle")}</h2>
        <ul>
          <li>{t("accountDeletion.effects.cancel")}</li>
          <li>{t("accountDeletion.effects.data")}</li>
          <li>{t("accountDeletion.effects.referral")}</li>
          <li>{t("accountDeletion.effects.marker")}</li>
        </ul>
      </section>

      {admin ? (
        <section className="account-deletion-notice" role="alert">
          <h2>{t("accountDeletion.adminTitle")}</h2>
          <p>{t("accountDeletion.adminText")}</p>
        </section>
      ) : !authenticated ? (
        <section className="account-deletion-action">
          <p>{isTelegram
            ? t("accountDeletion.telegramReopen")
            : t("accountDeletion.signInText")}</p>
          {!isTelegram && authentication.googleAvailable ? (
            <button className="button button--primary button--full" type="button" onClick={startGoogleReauthentication}>
              {t("accountDeletion.signInGoogle")}
            </button>
          ) : !isTelegram ? <p role="alert">{t("auth.googleUnavailable")}</p> : null}
        </section>
      ) : !isTelegram && !webReauthenticated ? (
        <section className="account-deletion-action">
          <p>{t("accountDeletion.reauthenticateText")}</p>
          <button className="button button--primary button--full" type="button" onClick={startGoogleReauthentication}>
            {t("accountDeletion.reauthenticateGoogle")}
          </button>
        </section>
      ) : (
        <section className="account-deletion-action">
          <p>{t("accountDeletion.finalText")}</p>
          <button
            className="button button--danger button--full"
            type="button"
            disabled={pending}
            onClick={() => setConfirmed(true)}
          >
            {t("accountDeletion.deleteAction")}
          </button>
        </section>
      )}

      {error === "BLOCKED" ? (
        <div className="account-deletion-error" role="alert">
          <p>{t("accountDeletion.errors.blocked")}</p>
          <Link className="button button--secondary" to="/account/activity">
            {t("accountDeletion.openActivity")}
          </Link>
        </div>
      ) : null}
      {error === "ADMIN" ? (
        <div className="account-deletion-error" role="alert">
          <p>{t("accountDeletion.adminText")}</p>
        </div>
      ) : null}
      {error === "EXPIRED" ? (
        <div className="account-deletion-error" role="alert">
          <p>{isTelegram
            ? t("accountDeletion.errors.telegramExpired")
            : t("accountDeletion.errors.webExpired")}</p>
          {isTelegram ? (
            <button className="button button--secondary" type="button" onClick={() => platform.close()}>
              {t("accountDeletion.closeMiniApp")}
            </button>
          ) : (
            <button className="button button--secondary" type="button" onClick={startGoogleReauthentication}>
              {t("accountDeletion.reauthenticateGoogle")}
            </button>
          )}
        </div>
      ) : null}
      {error === "NETWORK" ? (
        <div className="account-deletion-error" role="alert">
          <p>{t("accountDeletion.errors.network")}</p>
          {readyForConfirmation ? (
            <button className="button button--secondary" type="button" disabled={pending} onClick={() => void deleteAccount()}>
              {t("common.retry")}
            </button>
          ) : null}
        </div>
      ) : null}

      {confirmed ? (
        <ConfirmationDialog
          title={t("accountDeletion.confirmTitle")}
          description={t("accountDeletion.confirmText")}
          confirmLabel={pending ? t("accountDeletion.deleting") : t("accountDeletion.confirmAction")}
          pending={pending}
          destructive
          onCancel={() => setConfirmed(false)}
          onConfirm={() => void deleteAccount()}
        />
      ) : null}
    </div>
  );
}

export function AccountDeletedPage() {
  const { t } = useTranslation();
  const platform = usePlatform();
  return (
    <div className="page-state account-deleted-page">
      <span className="page-state__symbol" aria-hidden="true">✓</span>
      <h1>{t("accountDeletion.successTitle")}</h1>
      <p>{t("accountDeletion.successText")}</p>
      {platform.kind === "TELEGRAM" ? (
        <button className="button button--primary" type="button" onClick={() => platform.close()}>
          {t("accountDeletion.closeMiniApp")}
        </button>
      ) : (
        <Link className="button button--primary" to="/">{t("accountDeletion.homeAction")}</Link>
      )}
    </div>
  );
}
