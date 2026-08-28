import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import type { RentalAdminNotificationPreference as Preference } from "../../domain/rental";
import { Link } from "react-router-dom";

export function RentalAdminNotificationPreference() {
  const { t } = useTranslation();
  const api = useRentalApi();
  const [preference, setPreference] = useState<Preference | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true;
    setError(false);
    api.getAdminRentalNotificationPreference()
      .then((value) => {
        if (active) setPreference(value);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => { active = false; };
  }, [api]);

  const toggle = async () => {
    if (!preference || saving) return;
    try {
      setSaving(true);
      setError(false);
      setPreference(await api.updateAdminRentalNotificationPreference(
        !preference.telegramEnabled,
      ));
    } catch {
      setError(true);
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="admin-rental-notification-setting" aria-busy={!preference || saving}>
      <div>
        <strong>{t("adminRental.notifications.title")}</strong>
        <p>{preference?.telegramLinked
          ? t("adminRental.notifications.description")
          : t("adminRental.notifications.notLinked")}</p>
        {preference?.telegramLinked && preference.telegramUsername ? <small>@{preference.telegramUsername}</small> : null}
        {preference?.telegramLinked && !preference.writeAccessAllowed ? <small>{t("adminRental.notifications.writeAccessMissing")}</small> : null}
        {error ? <small role="alert">{t("adminRental.notifications.error")}</small> : null}
      </div>
      {!preference?.telegramLinked && preference ? (
        <Link className="button button--secondary" to="/account">{t("account.connectTelegram")}</Link>
      ) : <div className="admin-rental-notification-setting__control">
        <span>{preference
          ? t(`adminRental.notifications.${preference.telegramEnabled ? "on" : "off"}`)
          : "…"}</span>
        <button
          className={`switch${preference?.telegramEnabled ? " is-on" : ""}`}
          type="button"
          role="switch"
          aria-label={t("adminRental.notifications.title")}
          aria-checked={preference?.telegramEnabled ?? false}
          disabled={!preference || saving}
          onClick={() => void toggle()}
        >
          <span />
        </button>
      </div>}
    </section>
  );
}
