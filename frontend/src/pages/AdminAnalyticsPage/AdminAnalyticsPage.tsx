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

  const number = new Intl.NumberFormat(i18n.language);
  const decimal = new Intl.NumberFormat(i18n.language, { maximumFractionDigits: 2 });
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
            {(["ALL", "CLEANING", "RENTAL", "TRANSFER"] as AnalyticsService[]).map((item) => (
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
          <section className="admin-analytics__section" aria-labelledby="business-health-heading">
            <SectionHeading
              id="business-health-heading"
              title={t("analytics.businessHealth.title")}
              subtitle={t("analytics.businessHealth.subtitle")}
            />
            <div className="admin-analytics__cards admin-analytics__cards--business-health">
              <MetricCard
                label={t("analytics.metrics.tasksPerActiveCustomer")}
                value={decimal.format(overview.businessHealth.completedTasksPerActiveCustomer)}
                detail={t("analytics.activeCustomers", { count: overview.businessHealth.activeCustomers })}
              />
              <MetricCard
                label={t("analytics.metrics.repeat90Days")}
                value={formatRate(overview.retention.repeat90Days.rate, percent, t("analytics.insufficientData"))}
                detail={formatCohort(overview.retention.repeat90Days, number, t)}
              />
              <MetricCard
                label={t("analytics.metrics.twoPlusTasks")}
                value={number.format(overview.businessHealth.customersWithTwoPlusCompletedTasks)}
              />
              <MetricCard
                label={t("analytics.metrics.twoPlusServices")}
                value={number.format(overview.businessHealth.customersUsingTwoPlusServices)}
                detail={percent.format(overview.businessHealth.crossServiceCustomerRate)}
              />
              <MetricCard
                label={t("analytics.metrics.completedTasks")}
                value={number.format(overview.businessHealth.completedTasks)}
              />
            </div>
          </section>

          <section className="admin-analytics__section" aria-labelledby="retention-heading">
            <SectionHeading
              id="retention-heading"
              title={t("analytics.retention.title")}
              subtitle={t("analytics.retention.subtitle")}
            />
            <div className="admin-analytics__cards admin-analytics__cards--retention">
              <MetricCard
                label={t("analytics.metrics.repeat30Days")}
                value={formatRate(overview.retention.repeat30Days.rate, percent, t("analytics.insufficientData"))}
                detail={formatCohort(overview.retention.repeat30Days, number, t)}
              />
              <MetricCard
                label={t("analytics.metrics.repeat90Days")}
                value={formatRate(overview.retention.repeat90Days.rate, percent, t("analytics.insufficientData"))}
                detail={formatCohort(overview.retention.repeat90Days, number, t)}
              />
              <MetricCard
                label={t("analytics.metrics.secondOrderConversion")}
                value={formatRate(overview.retention.secondOrderConversion.rate, percent, t("analytics.insufficientData"))}
                detail={formatCohort(overview.retention.secondOrderConversion, number, t)}
              />
              <MetricCard
                label={t("analytics.metrics.timeToSecondTask")}
                value={overview.retention.medianDaysToSecondTask === null
                  ? t("analytics.insufficientData")
                  : t("analytics.days", { count: decimal.format(overview.retention.medianDaysToSecondTask) })}
              />
            </div>
          </section>

          <section className="admin-analytics__panel admin-analytics__panel--spaced">
            <SectionHeading
              contained
              title={t("analytics.transitions.title")}
              subtitle={t("analytics.transitions.subtitle")}
            />
            <div className="admin-analytics__transition-grid">
              {overview.transitions.map((transition) => (
                <article className="admin-analytics__transition" key={`${transition.fromService}-${transition.toService}`}>
                  <span>{t(`analytics.services.${transition.fromService}`)} <span aria-hidden="true">→</span> {t(`analytics.services.${transition.toService}`)}</span>
                  <strong>{formatRate(transition.conversionRate, percent, t("analytics.insufficientData"))}</strong>
                  <small>{t("analytics.transitions.result", {
                    converted: number.format(transition.convertedCustomers),
                    cohort: number.format(transition.cohortCustomers),
                  })}</small>
                </article>
              ))}
            </div>
          </section>

          {service === "ALL" || service === "RENTAL" ? (
            <section className="admin-analytics__section" aria-labelledby="rental-transfer-heading">
              <SectionHeading
                id="rental-transfer-heading"
                title={t("analytics.rentalToTransfer.title")}
                subtitle={t("analytics.rentalToTransfer.subtitle")}
              />
              <div className="admin-analytics__cards admin-analytics__cards--retention">
                <MetricCard
                  label={t("analytics.rentalToTransfer.shown")}
                  value={number.format(overview.rentalToTransfer.total.shownSources)}
                />
                <MetricCard
                  label={t("analytics.rentalToTransfer.started")}
                  value={number.format(overview.rentalToTransfer.total.startedSources)}
                  detail={formatRate(
                    overview.rentalToTransfer.total.startRate,
                    percent,
                    t("analytics.insufficientData"),
                  )}
                />
                <MetricCard
                  label={t("analytics.rentalToTransfer.created")}
                  value={number.format(overview.rentalToTransfer.total.createdSources)}
                  detail={formatRate(
                    overview.rentalToTransfer.total.creationRate,
                    percent,
                    t("analytics.insufficientData"),
                  )}
                />
                <MetricCard
                  label={t("analytics.rentalToTransfer.completed")}
                  value={number.format(overview.rentalToTransfer.total.completedSources)}
                  detail={formatRate(
                    overview.rentalToTransfer.total.completionRate,
                    percent,
                    t("analytics.insufficientData"),
                  )}
                />
                <MetricCard
                  label={t("analytics.rentalToTransfer.medianTime")}
                  value={overview.rentalToTransfer.total.medianHoursToCreation === null
                    ? t("analytics.insufficientData")
                    : t("analytics.hours", {
                      count: decimal.format(overview.rentalToTransfer.total.medianHoursToCreation),
                    })}
                />
              </div>
              {overview.rentalToTransfer.byContext.length > 0 ? (
                <div className="admin-analytics__repeat-grid admin-analytics__rental-transfer-grid">
                  {overview.rentalToTransfer.byContext.map(({ context, funnel }) => (
                    <article className="admin-analytics__repeat" key={context}>
                      <h3>{t(`analytics.rentalToTransfer.context.${context}`)}</h3>
                      <dl>
                        <div><dt>{t("analytics.rentalToTransfer.shown")}</dt><dd>{number.format(funnel.shownSources)}</dd></div>
                        <div><dt>{t("analytics.rentalToTransfer.started")}</dt><dd>{number.format(funnel.startedSources)}</dd></div>
                        <div><dt>{t("analytics.rentalToTransfer.created")}</dt><dd>{number.format(funnel.createdSources)}</dd></div>
                        <div><dt>{t("analytics.rentalToTransfer.completed")}</dt><dd>{number.format(funnel.completedSources)}</dd></div>
                        <div><dt>{t("analytics.rentalToTransfer.startRate")}</dt><dd>{formatRate(funnel.startRate, percent, t("analytics.insufficientData"))}</dd></div>
                        <div><dt>{t("analytics.rentalToTransfer.creationRate")}</dt><dd>{formatRate(funnel.creationRate, percent, t("analytics.insufficientData"))}</dd></div>
                        <div><dt>{t("analytics.rentalToTransfer.completionRate")}</dt><dd>{formatRate(funnel.completionRate, percent, t("analytics.insufficientData"))}</dd></div>
                        <div><dt>{t("analytics.rentalToTransfer.medianTime")}</dt><dd>{funnel.medianHoursToCreation === null
                          ? t("analytics.insufficientData")
                          : t("analytics.hours", { count: decimal.format(funnel.medianHoursToCreation) })}</dd></div>
                      </dl>
                    </article>
                  ))}
                </div>
              ) : null}
            </section>
          ) : null}

          <section className="admin-analytics__section" aria-labelledby="repeat-actions-heading">
            <SectionHeading
              id="repeat-actions-heading"
              title={t("analytics.repeatActions.title")}
              subtitle={t("analytics.repeatActions.subtitle")}
            />
            {overview.repeatActions.length === 0 ? (
              <p className="admin-analytics__empty admin-analytics__empty--card">{t("analytics.repeatActions.empty")}</p>
            ) : (
              <div className="admin-analytics__repeat-grid">
                {overview.repeatActions.map((metric) => (
                  <article className="admin-analytics__repeat" key={metric.service}>
                    <h3>{t(`analytics.services.${metric.service}`)}</h3>
                    <dl>
                      <div><dt>{t("analytics.repeatActions.shown")}</dt><dd>{number.format(metric.shownSources)}</dd></div>
                      <div><dt>{t("analytics.repeatActions.started")}</dt><dd>{number.format(metric.startedSources)}</dd></div>
                      <div><dt>{t("analytics.repeatActions.created")}</dt><dd>{number.format(metric.createdRepeatSources)}</dd></div>
                      <div><dt>{t("analytics.repeatActions.completed")}</dt><dd>{number.format(metric.completedRepeatSources)}</dd></div>
                      <div><dt>{t("analytics.repeatActions.startRate")}</dt><dd>{formatRate(metric.startRate, percent, t("analytics.insufficientData"))}</dd></div>
                      <div><dt>{t("analytics.repeatActions.completionRate")}</dt><dd>{formatRate(metric.completionRate, percent, t("analytics.insufficientData"))}</dd></div>
                      <div><dt>{t("analytics.repeatActions.medianTime")}</dt><dd>{metric.medianHoursToRepeat === null
                        ? t("analytics.insufficientData")
                        : t("analytics.hours", { count: decimal.format(metric.medianHoursToRepeat) })}</dd></div>
                    </dl>
                  </article>
                ))}
              </div>
            )}
          </section>

          <section className="admin-analytics__section" aria-labelledby="average-checks-heading">
            <SectionHeading
              id="average-checks-heading"
              title={t("analytics.averageChecks.title")}
              subtitle={t("analytics.averageChecks.subtitle")}
            />
            {overview.averageChecks.length === 0 ? (
              <p className="admin-analytics__empty admin-analytics__empty--card">{t("analytics.averageChecks.empty")}</p>
            ) : (
              <div className="admin-analytics__cards admin-analytics__cards--averages">
                {overview.averageChecks.map((check) => (
                  <MetricCard
                    key={`${check.service}-${check.currency}`}
                    label={t("analytics.metrics.averageCheck", {
                      service: t(`analytics.services.${check.service}`),
                      currency: check.currency,
                    })}
                    value={formatMoney(check.amount, check.currency, i18n.language, t("analytics.noData"))}
                    detail={t("analytics.averageChecks.completed", { count: number.format(check.completedTransactions) })}
                  />
                ))}
              </div>
            )}
          </section>

          <section className="admin-analytics__panel">
            <div className="admin-analytics__section-heading">
              <div>
                <h2>{t("analytics.acquisition.title")}</h2>
                <p>{t("analytics.acquisition.subtitle")}</p>
              </div>
              <span>{t("analytics.metrics.newCustomers")}: {number.format(overview.customers.newCustomers)}</span>
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

function SectionHeading({ id, title, subtitle, contained = false }: { id?: string; title: string; subtitle: string; contained?: boolean }) {
  return <div className={`admin-analytics__section-heading${contained ? "" : " admin-analytics__section-heading--standalone"}`}>
    <div>
      <h2 id={id}>{title}</h2>
      <p>{subtitle}</p>
    </div>
  </div>;
}

function formatRate(rate: number | null, formatter: Intl.NumberFormat, empty: string): string {
  return rate === null ? empty : formatter.format(rate);
}

function formatCohort(
  metric: { cohortCustomers: number; convertedCustomers: number },
  formatter: Intl.NumberFormat,
  t: (key: string, options?: Record<string, unknown>) => string,
): string {
  return metric.cohortCustomers === 0
    ? t("analytics.retention.noMatureCohort")
    : t("analytics.retention.result", {
      converted: formatter.format(metric.convertedCustomers),
      cohort: formatter.format(metric.cohortCustomers),
    });
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
