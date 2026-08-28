import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import type { AccountIdentities } from "../../domain/customer";
import { usePlatform } from "../../platform/PlatformProvider";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { BrandName } from "../../components/BrandName/BrandName";
import "./AccountPage.css";

const TELEGRAM_LINK_PENDING_UNTIL = "loco-place.telegram-link-pending-until";

function hasPendingTelegramLink(): boolean {
  const value = window.sessionStorage.getItem(TELEGRAM_LINK_PENDING_UNTIL);
  const expiresAt = value ? Date.parse(value) : Number.NaN;
  if (!Number.isFinite(expiresAt) || expiresAt <= Date.now()) {
    window.sessionStorage.removeItem(TELEGRAM_LINK_PENDING_UNTIL);
    return false;
  }
  return true;
}

export function AccountPage() {
  const { t } = useTranslation();
  const api = useCustomerApi();
  const platform = usePlatform();
  const [state, setState] = useState<AccountIdentities | null>(null);
  const [error, setError] = useState(false);
  const [linking, setLinking] = useState(false);
  const [grantingAccess, setGrantingAccess] = useState(false);
  const [permissionRequested, setPermissionRequested] = useState(false);
  const [pending, setPending] = useState(hasPendingTelegramLink);

  const load = useCallback(async () => {
    try {
      setError(false);
      setState(await api.getAccountIdentities());
    } catch {
      setError(true);
    }
  }, [api]);

  useEffect(() => { void load(); }, [load]);
  useEffect(() => {
    if (!pending) return;
    const interval = window.setInterval(() => {
      if (!hasPendingTelegramLink()) {
        setPending(false);
        return;
      }
      void load();
    }, 3_000);
    return () => window.clearInterval(interval);
  }, [load, pending]);

  const telegram = state?.identities.find((identity) => identity.provider === "TELEGRAM");
  useEffect(() => {
    if (telegram?.linked) {
      window.sessionStorage.removeItem(TELEGRAM_LINK_PENDING_UNTIL);
      setPending(false);
    }
  }, [telegram?.linked]);

  const connect = async () => {
    try {
      setLinking(true);
      setError(false);
      const request = await api.initiateTelegramLink();
      window.sessionStorage.setItem(TELEGRAM_LINK_PENDING_UNTIL, request.expiresAt);
      platform.openExternalLink(request.deepLink);
      setPending(true);
    } catch {
      setError(true);
    } finally {
      setLinking(false);
    }
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
    </div>
  );
}
