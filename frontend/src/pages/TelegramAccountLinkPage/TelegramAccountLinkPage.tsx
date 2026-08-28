import { useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { usePlatform } from "../../platform/PlatformProvider";
import { BrandName } from "../../components/BrandName/BrandName";

type LinkState = "READY" | "LINKING" | "SUCCESS_ALLOWED" | "SUCCESS_NO_ACCESS" | "ERROR";

export function TelegramAccountLinkPage() {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();
  const api = useCustomerApi();
  const platform = usePlatform();
  const token = useMemo(() => searchParams.get("token")?.trim() ?? "", [searchParams]);
  const [state, setState] = useState<LinkState>(token ? "READY" : "ERROR");

  const confirm = async () => {
    try {
      setState("LINKING");
      const allowed = await platform.ensureNotificationAccess();
      await api.confirmTelegramLink(token);
      setState(allowed ? "SUCCESS_ALLOWED" : "SUCCESS_NO_ACCESS");
    } catch {
      setState("ERROR");
    }
  };

  const success = state === "SUCCESS_ALLOWED" || state === "SUCCESS_NO_ACCESS";
  return (
    <div className="page account-link-confirm">
      <section className="account-link-confirm__card">
        <span className="eyebrow"><BrandName /></span>
        <h1>{success ? t("account.linkSuccessTitle") : t("account.confirmTitle")}</h1>
        <p>{state === "SUCCESS_ALLOWED"
          ? t("account.linkSuccessAllowed")
          : state === "SUCCESS_NO_ACCESS"
            ? t("account.linkSuccessNoAccess")
            : state === "ERROR"
              ? t("account.linkInvalid")
              : t("account.confirmText")}</p>
        {state === "READY" || state === "LINKING" ? (
          <div className="account-link-confirm__actions">
            <button className="button button--primary" type="button" disabled={state === "LINKING"} onClick={() => void confirm()}>
              {state === "LINKING" ? t("account.connecting") : t("account.confirm")}
            </button>
            <button className="button button--secondary" type="button" onClick={() => platform.close()}>
              {t("common.cancel")}
            </button>
          </div>
        ) : success ? (
          <button className="button button--primary" type="button" onClick={() => platform.close()}>{t("common.close")}</button>
        ) : (
          <Link className="button button--secondary" to="/">{t("notFound.action")}</Link>
        )}
      </section>
    </div>
  );
}
