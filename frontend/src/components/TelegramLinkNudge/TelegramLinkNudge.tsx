import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../api/AuthApiProvider";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { usePlatform } from "../../platform/PlatformProvider";

const SESSION_KEY = "loco-place.telegram-link-nudge-shown";

export function TelegramLinkNudge() {
  const { t } = useTranslation();
  const location = useLocation();
  const authentication = useAuthentication();
  const api = useCustomerApi();
  const platform = usePlatform();
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    if (platform.kind !== "WEB" || !authentication.current.authenticated
      || location.pathname.startsWith("/account") || sessionStorage.getItem(SESSION_KEY)) return;
    let active = true;
    api.getAccountIdentities().then((response) => {
      if (!active) return;
      const linked = response.identities.some((identity) => identity.provider === "TELEGRAM" && identity.linked);
      sessionStorage.setItem(SESSION_KEY, "1");
      setVisible(!linked);
    }).catch(() => undefined);
    return () => { active = false; };
  }, [api, authentication.current.authenticated, location.pathname, platform.kind]);

  if (!visible) return null;
  return (
    <aside className="telegram-link-nudge">
      <div><strong>{t("account.nudgeTitle")}</strong><p>{t("account.nudgeText")}</p></div>
      <div className="telegram-link-nudge__actions">
        <Link className="button button--primary" to="/account" onClick={() => setVisible(false)}>{t("account.connectTelegram")}</Link>
        <button className="button button--ghost" type="button" onClick={() => setVisible(false)}>{t("account.notNow")}</button>
      </div>
    </aside>
  );
}
