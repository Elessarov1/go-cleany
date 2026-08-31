import { useEffect, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useSupportApi } from "../../api/SupportApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { AdminSupportCaseDetails } from "../../domain/support";

export function AdminSupportCasePage() {
  const { id } = useParams();
  const caseId = Number(id);
  const { t, i18n } = useTranslation();
  const api = useSupportApi();
  const [details, setDetails] = useState<AdminSupportCaseDetails | null>(null);
  const [failed, setFailed] = useState(false);
  const [pending, setPending] = useState(false);
  const [comment, setComment] = useState("");
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setDetails(null);
    setFailed(false);
    api.getAdminCase(caseId)
      .then((value) => { if (active) setDetails(value); })
      .catch(() => { if (active) setFailed(true); });
    return () => { active = false; };
  }, [api, caseId]);

  async function resolve(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!comment.trim()) return;
    setPending(true);
    setFailed(false);
    try {
      setDetails(await api.resolveAdminCase(caseId, comment.trim()));
      setComment("");
    } catch {
      setFailed(true);
    } finally {
      setPending(false);
    }
  }

  if ((!Number.isInteger(caseId) || caseId <= 0) || (failed && !details)) return <ErrorState message={t("support.admin.loadError")} />;
  if (!details) return <LoadingState />;
  const summary = details.summary;

  return (
    <div className="page page--admin-support-case">
      <Link className="back-link" to="/admin/support"><Icon name="arrow-left" size={17} />{t("common.back")}</Link>
      <header className="page-header page-header--compact"><span className="eyebrow">{t(`support.service.${summary.service}`)} · #{summary.sourceEntityId}</span><h1>{t("support.admin.case", { id: summary.id })}</h1><span className={`support-status support-status--${summary.status.toLowerCase()}`}>{t(`support.status.${summary.status}`)}</span></header>
      {failed ? <p className="form-alert" role="alert">{t("support.admin.actionError")}</p> : null}
      <div className="admin-support-case-grid">
        <section className="admin-support-panel"><h2>{t("support.admin.problemTitle")}</h2><dl className="detail-list"><div><dt>{t("support.admin.category")}</dt><dd>{t(`support.category.${summary.category}`)}</dd></div><div><dt>{t("support.admin.createdAt")}</dt><dd>{formatTimestamp(summary.createdAt, locale)}</dd></div><div><dt>{t("support.admin.description")}</dt><dd>{details.description || t("common.notProvided")}</dd></div></dl></section>
        <section className="admin-support-panel"><h2>{t("support.admin.customerTitle")}</h2><dl className="detail-list"><div><dt>{t("support.admin.customer")}</dt><dd>{summary.customerName}</dd></div><div><dt>{t("support.admin.phone")}</dt><dd>{summary.customerPhone}</dd></div><div><dt>{t("support.admin.customerId")}</dt><dd>{summary.customerId}</dd></div></dl><Link className="button button--secondary button--full" to={summary.sourceAdminPath}>{t("support.admin.openSource")}<Icon name="arrow-right" size={17} /></Link></section>
      </div>
      {summary.status === "RESOLVED" ? (
        <section className="admin-support-resolution"><h2>{t("support.admin.resolutionTitle")}</h2><p>{details.resolutionComment}</p><small>{details.resolvedAt ? formatTimestamp(details.resolvedAt, locale) : null}{details.resolvedByCustomerId ? ` · ${t("support.admin.resolvedBy", { id: details.resolvedByCustomerId })}` : ""}</small></section>
      ) : (
        <form className="admin-support-resolution" onSubmit={resolve}><h2>{t("support.admin.resolveTitle")}</h2><label><span>{t("support.admin.resolutionComment")}</span><textarea rows={5} required maxLength={2000} value={comment} onChange={(event) => setComment(event.target.value)} /></label><button className="button button--primary button--full" type="submit" disabled={pending || !comment.trim()}>{pending ? t("support.admin.resolving") : t("support.admin.resolve")}</button></form>
      )}
    </div>
  );
}

function formatTimestamp(value: string, locale: string): string {
  return new Intl.DateTimeFormat(locale, { dateStyle: "medium", timeStyle: "short" }).format(new Date(value));
}
