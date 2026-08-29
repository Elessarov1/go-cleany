import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useAnalyticsApi } from "../../api/AnalyticsApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { AnalyticsOverview, AnalyticsService } from "../../domain/analytics";
import { AcquisitionCampaignDialog } from "./AcquisitionCampaignDialog";

type PeriodPreset = "TODAY" | "7_DAYS" | "30_DAYS" | "THIS_MONTH" | "CUSTOM";

export function AdminAnalyticsPage() {
  const { t, i18n } = useTranslation();
  const api = useAnalyticsApi();
  const today = istanbulToday();
  const [preset, setPreset] = useState<PeriodPreset>("30_DAYS");
  const [customFrom, setCustomFrom] = useState(shiftDate(today, -29));
  const [customTo, setCustomTo] = useState(today);
  const [service, setService] = useState<AnalyticsService>("ALL");
  const [overview, setOverview] = useState<AnalyticsOverview | null>(null);
  const [failed, setFailed] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [campaignDialogOpen, setCampaignDialogOpen] = useState(false);
  const range = useMemo(
    () => periodRange(preset, today, customFrom, customTo),
    [preset, today, customFrom, customTo],
  );

  useEffect(() => {
    if (range.from > range.to) return;
    let active = true;
    setFailed(false);
    setOverview(null);
    api.getOverview({ ...range, service })
      .then((result) => {
        if (active) setOverview(result);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api, range.from, range.to, service, reloadKey]);

  if (failed) {
    return <ErrorState message={t("analytics.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  }

  const checks = new Map(overview?.averageChecks.map((item) => [item.service, item]));
  const number = new Intl.NumberFormat(i18n.language);
  const percent = new Intl.NumberFormat(i18n.language, { style: "percent", maximumFractionDigits: 1 });

  return (
    <div className="page page--admin-analytics">
      <header className="page-header admin-analytics__header">
        <div>
          <span className="eyebrow"><BrandName /> / {t("analytics.eyebrow")}</span>
          <h1>{t("analytics.title")}</h1>
          <p>{t("analytics.subtitle")}</p>
        </div>
        <button className="button button--primary admin-analytics__create-campaign" type="button" onClick={() => setCampaignDialogOpen(true)}>
          {t("analytics.campaigns.create")}
        </button>
      </header>

      {campaignDialogOpen ? (
        <AcquisitionCampaignDialog
          onCreate={(request) => api.createCampaign(request)}
          onClose={() => setCampaignDialogOpen(false)}
        />
      ) : null}

      <section className="admin-analytics__filters" aria-label={t("analytics.filters.label")}>
        <div className="admin-analytics__presets">
          {(["TODAY", "7_DAYS", "30_DAYS", "THIS_MONTH", "CUSTOM"] as PeriodPreset[]).map((item) => (
            <button
              className={preset === item ? "is-active" : ""}
              type="button"
              key={item}
              onClick={() => setPreset(item)}
            >
              {t(`analytics.filters.${item}`)}
            </button>
          ))}
        </div>
        <label className="admin-analytics__service">
          <span>{t("analytics.filters.service")}</span>
          <select value={service} onChange={(event) => setService(event.target.value as AnalyticsService)}>
            {(["ALL", "CLEANING", "RENTAL"] as AnalyticsService[]).map((item) => (
              <option key={item} value={item}>{t(`analytics.services.${item}`)}</option>
            ))}
          </select>
        </label>
        {preset === "CUSTOM" ? (
          <div className="admin-analytics__custom-range">
            <label>
              <span>{t("analytics.filters.from")}</span>
              <input type="date" value={customFrom} max={customTo} onChange={(event) => setCustomFrom(event.target.value)} />
            </label>
            <label>
              <span>{t("analytics.filters.to")}</span>
              <input type="date" value={customTo} min={customFrom} max={today} onChange={(event) => setCustomTo(event.target.value)} />
            </label>
          </div>
        ) : null}
      </section>

      {!overview ? <LoadingState /> : (
        <>
          <section className="admin-analytics__cards" aria-label={t("analytics.summaryLabel")}>
            <MetricCard label={t("analytics.metrics.newCustomers")} value={number.format(overview.customers.newCustomers)} />
            <MetricCard
              label={t("analytics.metrics.repeatCustomers")}
              value={number.format(overview.customers.repeatCustomers)}
              detail={percent.format(overview.customers.repeatRate)}
            />
            <MetricCard
              label={t("analytics.metrics.cleaningAverage")}
              value={formatMoney(checks.get("CLEANING")?.amount, checks.get("CLEANING")?.currency, i18n.language, t("analytics.noData"))}
            />
            <MetricCard
              label={t("analytics.metrics.rentalAverage")}
              value={formatMoney(checks.get("RENTAL")?.amount, checks.get("RENTAL")?.currency, i18n.language, t("analytics.noData"))}
            />
          </section>

          <section className="admin-analytics__panel">
            <div className="admin-analytics__section-heading">
              <div>
                <h2>{t("analytics.acquisition.title")}</h2>
                <p>{t("analytics.acquisition.subtitle")}</p>
              </div>
              <span>{t("analytics.activeCustomers", { count: overview.customers.activeCustomers })}</span>
            </div>
            {overview.acquisition.length === 0 ? (
              <p className="admin-analytics__empty">{t("analytics.acquisition.empty")}</p>
            ) : (
              <div className="admin-analytics__table-wrap">
                <table className="admin-analytics__table">
                  <thead><tr>
                    <th>{t("analytics.acquisition.channel")}</th>
                    <th>{t("analytics.acquisition.campaign")}</th>
                    <th>{t("analytics.acquisition.medium")}</th>
                    <th>{t("analytics.acquisition.entries")}</th>
                    <th>{t("analytics.acquisition.newCustomers")}</th>
                    <th>{t("analytics.acquisition.completed")}</th>
                  </tr></thead>
                  <tbody>
                    {overview.acquisition.map((metric) => (
                      <tr key={`${metric.channel}-${metric.campaignId ?? "organic"}`}>
                        <td><span className={`analytics-channel analytics-channel--${metric.channel.toLowerCase()}`}>{t(`analytics.channels.${metric.channel}`)}</span></td>
                        <td>{metric.campaignName ?? t("analytics.acquisition.noCampaign")}</td>
                        <td>{metric.medium ? t(`analytics.mediums.${metric.medium}`) : "—"}</td>
                        <td>{number.format(metric.entries)}</td>
                        <td>{number.format(metric.newCustomers)}</td>
                        <td>{number.format(metric.completedTransactions)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
}

function MetricCard({ label, value, detail }: { label: string; value: string; detail?: string }) {
  return <article className="admin-analytics__card"><span>{label}</span><strong>{value}</strong>{detail ? <small>{detail}</small> : null}</article>;
}

function formatMoney(amount: number | undefined, currency: string | undefined, locale: string, empty: string): string {
  if (amount === undefined || !currency) return empty;
  return new Intl.NumberFormat(locale, { style: "currency", currency, maximumFractionDigits: 0 }).format(amount);
}

function periodRange(preset: PeriodPreset, today: string, customFrom: string, customTo: string) {
  switch (preset) {
    case "TODAY": return { from: today, to: today };
    case "7_DAYS": return { from: shiftDate(today, -6), to: today };
    case "30_DAYS": return { from: shiftDate(today, -29), to: today };
    case "THIS_MONTH": return { from: `${today.slice(0, 7)}-01`, to: today };
    case "CUSTOM": return { from: customFrom, to: customTo };
  }
}

function istanbulToday(): string {
  const parts = new Intl.DateTimeFormat("en", {
    timeZone: "Europe/Istanbul",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).formatToParts(new Date());
  const value = (type: Intl.DateTimeFormatPartTypes) => parts.find((part) => part.type === type)?.value ?? "";
  return `${value("year")}-${value("month")}-${value("day")}`;
}

function shiftDate(date: string, days: number): string {
  const shifted = new Date(`${date}T12:00:00Z`);
  shifted.setUTCDate(shifted.getUTCDate() + days);
  return shifted.toISOString().slice(0, 10);
}
