import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useSupportApi } from "../../api/SupportApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { PlatformService } from "../../domain/platformService";
import type { AdminSupportCasePage, SupportCaseStatus } from "../../domain/support";

type StatusFilter = SupportCaseStatus | "ALL";
type ServiceFilter = PlatformService | "ALL";

export function AdminSupportCasesPage() {
  const { t, i18n } = useTranslation();
  const api = useSupportApi();
  const [status, setStatus] = useState<StatusFilter>("OPEN");
  const [service, setService] = useState<ServiceFilter>("ALL");
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<AdminSupportCasePage | null>(null);
  const [failed, setFailed] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setResult(null);
    setFailed(false);
    api.getAdminCases({ status, service, page, size: 20 })
      .then((value) => { if (active) setResult(value); })
      .catch(() => { if (active) setFailed(true); });
    return () => { active = false; };
  }, [api, page, reloadKey, service, status]);

  if (failed && !result) return <ErrorState message={t("support.admin.loadError")} onRetry={() => setReloadKey((value) => value + 1)} />;

  return (
    <div className="page page--admin-support">
      <header className="page-header">
        <span className="eyebrow"><BrandName /> / support</span>
        <h1>{t("support.admin.title")}</h1>
        <p>{t("support.admin.subtitle")}</p>
      </header>
      <div className="admin-support-filters">
        <label><span>{t("support.admin.statusFilter")}</span><select value={status} onChange={(event) => { setStatus(event.target.value as StatusFilter); setPage(0); }}><option value="OPEN">{t("support.status.OPEN")}</option><option value="RESOLVED">{t("support.status.RESOLVED")}</option><option value="ALL">{t("support.admin.all")}</option></select></label>
        <label><span>{t("support.admin.serviceFilter")}</span><select value={service} onChange={(event) => { setService(event.target.value as ServiceFilter); setPage(0); }}><option value="ALL">{t("support.admin.all")}</option>{(["CLEANING", "RENTAL", "TRANSFER"] as PlatformService[]).map((value) => <option key={value} value={value}>{t(`support.service.${value}`)}</option>)}</select></label>
      </div>
      {!result ? <LoadingState /> : result.content.length === 0 ? (
        <section className="admin-support-empty"><Icon name="check" size={28} /><h2>{t("support.admin.emptyTitle")}</h2><p>{t("support.admin.emptyText")}</p></section>
      ) : (
        <>
          <div className="admin-support-list">
            {result.content.map((supportCase) => (
              <Link className="admin-support-card" to={`/admin/support/cases/${supportCase.id}`} key={supportCase.id}>
                <div className="admin-support-card__top"><span>{t("support.admin.case", { id: supportCase.id })}</span><strong>{t(`support.status.${supportCase.status}`)}</strong></div>
                <h2>{t(`support.category.${supportCase.category}`)}</h2>
                <p>{t(`support.service.${supportCase.service}`)} · #{supportCase.sourceEntityId}</p>
                <dl><div><dt>{t("support.admin.customer")}</dt><dd>{supportCase.customerName}</dd></div><div><dt>{t("support.admin.age")}</dt><dd>{relativeAge(supportCase.createdAt, locale)}</dd></div></dl>
                <Icon name="arrow-right" size={19} />
              </Link>
            ))}
          </div>
          {result.totalPages > 1 ? (
            <div className="admin-support-pagination"><button className="button button--secondary" type="button" disabled={page === 0} onClick={() => setPage((value) => value - 1)}>{t("common.back")}</button><span>{t("support.admin.page", { current: page + 1, total: result.totalPages })}</span><button className="button button--secondary" type="button" disabled={page + 1 >= result.totalPages} onClick={() => setPage((value) => value + 1)}>{t("common.next")}</button></div>
          ) : null}
        </>
      )}
    </div>
  );
}

function relativeAge(createdAt: string, locale: string): string {
  const minutes = Math.max(0, Math.floor((Date.now() - new Date(createdAt).getTime()) / 60_000));
  const formatter = new Intl.RelativeTimeFormat(locale, { numeric: "always" });
  if (minutes < 60) return formatter.format(-minutes, "minute");
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return formatter.format(-hours, "hour");
  return formatter.format(-Math.floor(hours / 24), "day");
}
