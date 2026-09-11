import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { useAuthentication } from "../../api/AuthApiProvider";
import { ApiError } from "../../api/ApiError";
import type { AccountIdentities } from "../../domain/customer";
import { usePlatform } from "../../platform/PlatformProvider";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { BrandName } from "../../components/BrandName/BrandName";
import { Icon } from "../../components/Icon/Icon";
import { Link } from "react-router-dom";
import "./AccountPage.css";

const TELEGRAM_LINK_PENDING_UNTIL = "loco-place.telegram-link-pending-until";
const TELEGRAM_LINK_ATTEMPT_ID = "loco-place.telegram-link-attempt-id";
const TELEGRAM_LINK_AFTER_REAUTH = "loco-place.telegram-link-after-reauth";

function hasPendingTelegramLink(): boolean {
  const value = window.sessionStorage.getItem(TELEGRAM_LINK_PENDING_UNTIL);
  const expiresAt = value ? Date.parse(value) : Number.NaN;
  if (!Number.isFinite(expiresAt) || expiresAt <= Date.now()) {
    window.sessionStorage.removeItem(TELEGRAM_LINK_PENDING_UNTIL);
    window.sessionStorage.removeItem(TELEGRAM_LINK_ATTEMPT_ID);
    return false;
  }
  return true;
}

export function AccountPage() {
  const { t } = useTranslation();
  const api = useCustomerApi();
  const authentication = useAuthentication();
  const platform = usePlatform();
  const [state, setState] = useState<AccountIdentities | null>(null);
  const [error, setError] = useState(false);
  const [linking, setLinking] = useState(false);
  const [grantingAccess, setGrantingAccess] = useState(false);
  const [permissionRequested, setPermissionRequested] = useState(false);
  const [pending, setPending] = useState(hasPendingTelegramLink);
  const [attemptId, setAttemptId] = useState(
    () => window.sessionStorage.getItem(TELEGRAM_LINK_ATTEMPT_ID) ?? "",
  );

  const load = useCallback(async () => {
    try {
      setError(false);
      setState(await api.getAccountIdentities());
    } catch {
      setError(true);
    }
  }, [api]);

  useEffect(() => { void load(); }, [load]);
  const beginTelegramLink = useCallback(async () => {
    try {
      setLinking(true);
      setError(false);
      const request = await api.initiateTelegramLink();
      window.sessionStorage.setItem(TELEGRAM_LINK_PENDING_UNTIL, request.expiresAt);
      window.sessionStorage.setItem(TELEGRAM_LINK_ATTEMPT_ID, request.id);
      setAttemptId(request.id);
      setPending(true);
      platform.openExternalLink(request.deepLink);
    } catch {
      setError(true);
    } finally {
      setLinking(false);
    }
  }, [api, platform]);

  useEffect(() => {
    if (platform.kind !== "WEB"
      || !new URLSearchParams(window.location.search).has("linkTelegram")
      || window.sessionStorage.getItem(TELEGRAM_LINK_AFTER_REAUTH) !== "1") return;
    window.sessionStorage.removeItem(TELEGRAM_LINK_AFTER_REAUTH);
    window.history.replaceState({}, "", "/account");
    void beginTelegramLink();
  }, [beginTelegramLink, platform.kind]);

  useEffect(() => {
    if (!pending || !attemptId) return;
    const interval = window.setInterval(() => {
      if (!hasPendingTelegramLink()) {
        setPending(false);
        return;
      }
      void api.confirmTelegramLink(attemptId)
        .then((identities) => {
          setState(identities);
          window.sessionStorage.removeItem(TELEGRAM_LINK_PENDING_UNTIL);
          window.sessionStorage.removeItem(TELEGRAM_LINK_ATTEMPT_ID);
          setPending(false);
        })
        .catch((failure: unknown) => {
          if (failure instanceof ApiError
            && (failure.code === "identity_link_target_not_verified"
              || failure.code === "identity_link_waiting_for_telegram")) return;
          setError(true);
        });
    }, 3_000);
    return () => window.clearInterval(interval);
  }, [api, attemptId, pending]);

  const telegram = state?.identities.find((identity) => identity.provider === "TELEGRAM");
  useEffect(() => {
    if (telegram?.linked) {
      window.sessionStorage.removeItem(TELEGRAM_LINK_PENDING_UNTIL);
      window.sessionStorage.removeItem(TELEGRAM_LINK_ATTEMPT_ID);
      setPending(false);
    }
  }, [telegram?.linked]);

  const connect = async () => {
    if (platform.kind === "WEB") {
      window.sessionStorage.setItem(TELEGRAM_LINK_AFTER_REAUTH, "1");
      window.location.assign(authentication.googleLoginUrl("/account?linkTelegram=1"));
      return;
    }
    await beginTelegramLink();
  };

  const allowNotifications = async () => {
    try {
      setGrantingAccess(true);
      setError(false);
      const allowed = await platform.ensureNotificationAccess();
      setPermissionRequested(allowed);
      if (allowed) await load();
    } catch {
      setError(true);
    } finally {
      setGrantingAccess(false);
    }
  };

  if (error && !state) return <ErrorState message={t("account.loadError")} onRetry={() => void load()} />;
  if (!state) return <LoadingState />;

  const google = state.identities.find((identity) => identity.provider === "GOOGLE");
  return (
    <div className="page account-page">
      <header className="page-header page-header--compact">
        <span className="eyebrow"><BrandName /></span>
        <h1>{t("account.title")}</h1>
        <p>{t("account.subtitle")}</p>
      </header>

      <Link className="account-activity-link" to="/account/activity">
        <span className="account-activity-link__icon"><Icon name="clipboard" size={24} /></span>
        <span className="account-activity-link__content">
          <strong>{t("account.activityTitle")}</strong>
          <span>{t("account.activityText")}</span>
        </span>
        <Icon name="arrow-right" size={20} />
      </Link>

      <div className="account-providers">
        <section className="account-provider-card">
          <div className="account-provider-card__mark account-provider-card__mark--provider-logo">
            <img className="account-provider-card__logo" src="/assets/icons/google.svg" alt="" aria-hidden="true" />
          </div>
          <div><h2>Google</h2><p>{google?.linked ? t("account.connected") : t("account.notConnected")}</p></div>
          <span className={`account-provider-card__status${google?.linked ? " is-connected" : ""}`}>
            {google?.linked ? "✓" : "—"}
          </span>
        </section>

        <section className="account-provider-card account-provider-card--telegram">
          <div className="account-provider-card__mark account-provider-card__mark--provider-logo">
            <img className="account-provider-card__logo" src="/assets/icons/telegram.svg" alt="" aria-hidden="true" />
          </div>
          <div className="account-provider-card__content">
            <h2>Telegram</h2>
            {telegram?.linked ? (
              <>
                <p>{telegram.username ? `@${telegram.username}` : t("account.connected")}</p>
                <small>{telegram.writeAccessAllowed
                  ? t("account.notificationsAllowed")
                  : t("account.notificationsNotAllowed")}</small>
                {!telegram.writeAccessAllowed && platform.kind === "TELEGRAM" ? (
                  <button
                    className="button button--secondary account-provider-card__permission"
                    type="button"
                    disabled={grantingAccess}
                    onClick={() => void allowNotifications()}
                  >
                    {grantingAccess ? t("account.connecting") : t("account.allowNotifications")}
                  </button>
                ) : null}
                {permissionRequested && !telegram.writeAccessAllowed ? (
                  <small>{t("account.permissionUpdatePending")}</small>
                ) : null}
              </>
            ) : (
              <>
                <p>{t("account.notConnected")}</p>
                <small>{t("account.telegramBenefit")}</small>
              </>
            )}
          </div>
          {telegram?.linked ? (
            <span className="account-provider-card__status is-connected">✓</span>
          ) : platform.kind === "WEB" || platform.kind === "PREVIEW" ? (
            <button className="button button--primary" type="button" disabled={linking} onClick={() => void connect()}>
              {linking ? t("account.connecting") : t("account.connectTelegram")}
            </button>
          ) : null}
        </section>
      </div>

      {pending ? (
        <section className="account-link-pending" role="status">
          <p>{t("account.pending")}</p>
          <button className="button button--secondary" type="button" onClick={() => void load()}>
            {t("account.checkConnection")}
          </button>
        </section>
      ) : null}
      {error ? <p className="form-alert" role="alert">{t("account.linkError")}</p> : null}

      <section className="account-danger-zone">
        <div>
          <h2>{t("account.dangerTitle")}</h2>
          <p>{t("account.dangerText")}</p>
        </div>
        <Link className="button button--danger" to="/account/delete">
          {t("account.deleteAccount")}
        </Link>
      </section>
    </div>
  );
}
