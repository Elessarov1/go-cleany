import { useEffect, useState, type FormEvent } from "react";
import { useTranslation } from "react-i18next";
import { useSupportApi } from "../../api/SupportApiProvider";
import type { PlatformService } from "../../domain/platformService";
import type { SupportCaseCategory, TransactionSupport } from "../../domain/support";
import { Icon } from "../Icon/Icon";

const CATEGORIES: SupportCaseCategory[] = [
  "PROVIDER_LATE", "PROVIDER_NO_SHOW", "QUALITY_PROBLEM", "BOOKING_PROBLEM", "OTHER",
];

export function TransactionCarePanel({
  service,
  sourceEntityId,
}: {
  service: PlatformService;
  sourceEntityId: number;
}) {
  const { t, i18n } = useTranslation();
  const api = useSupportApi();
  const [state, setState] = useState<TransactionSupport | null>(null);
  const [failed, setFailed] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [helpOpen, setHelpOpen] = useState(false);
  const [problemOpen, setProblemOpen] = useState(false);
  const [pending, setPending] = useState(false);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setState(null);
    setFailed(false);
    api.getTransactionSupport(service, sourceEntityId)
      .then((value) => { if (active) setState(value); })
      .catch(() => { if (active) setFailed(true); });
    return () => { active = false; };
  }, [api, reloadKey, service, sourceEntityId]);

  async function good() {
    setPending(true);
    setFailed(false);
    try {
      setState(await api.submitFeedback({ service, sourceEntityId, outcome: "GOOD", category: null, comment: null }));
    } catch {
      setFailed(true);
    } finally {
      setPending(false);
    }
  }

  async function submitProblem(category: SupportCaseCategory, comment: string | null) {
    setPending(true);
    setFailed(false);
    try {
      setState(await api.submitFeedback({ service, sourceEntityId, outcome: "PROBLEM", category, comment }));
      setProblemOpen(false);
    } catch {
      setFailed(true);
    } finally {
      setPending(false);
    }
  }

  async function submitHelp(category: SupportCaseCategory, description: string | null) {
    setPending(true);
    setFailed(false);
    try {
      const supportCase = await api.createCase({ service, sourceEntityId, category, description });
      setState((current) => current ? { ...current, latestCase: supportCase } : current);
      setHelpOpen(false);
    } catch {
      setFailed(true);
    } finally {
      setPending(false);
    }
  }

  const latestCase = state?.latestCase ?? null;
  const hasOpenCase = latestCase?.status === "OPEN";

  return (
    <section className="transaction-care" aria-labelledby={`transaction-care-${service}-${sourceEntityId}`}>
      <div className="transaction-care__heading">
        <span><Icon name="support" size={22} /></span>
        <div>
          <h2 id={`transaction-care-${service}-${sourceEntityId}`}>{t("support.customer.title")}</h2>
          <p>{t("support.customer.subtitle")}</p>
        </div>
      </div>

      {failed ? (
        <div className="transaction-care__error" role="alert">
          <span>{t("support.customer.error")}</span>
          <button type="button" onClick={() => setReloadKey((value) => value + 1)}>{t("common.retry")}</button>
        </div>
      ) : null}

      {latestCase ? (
        <div className={`transaction-care__case transaction-care__case--${latestCase.status.toLowerCase()}`}>
          <div><strong>{t("support.customer.caseTitle", { id: latestCase.id })}</strong><span>{t(`support.status.${latestCase.status}`)}</span></div>
          <p>{t(`support.category.${latestCase.category}`)}</p>
          <time dateTime={latestCase.createdAt}>{new Intl.DateTimeFormat(locale, { dateStyle: "medium", timeStyle: "short" }).format(new Date(latestCase.createdAt))}</time>
          {latestCase.status === "RESOLVED" && latestCase.resolutionComment ? (
            <div className="transaction-care__resolution"><span>{t("support.customer.resolution")}</span><p>{latestCase.resolutionComment}</p></div>
          ) : null}
        </div>
      ) : null}

      {state?.feedbackEligible ? (
        <div className="transaction-care__feedback">
          <strong>{t("support.customer.feedbackTitle")}</strong>
          <p>{t("support.customer.feedbackText")}</p>
          <div className="transaction-care__feedback-actions">
            <button className="button button--secondary" type="button" disabled={pending} onClick={() => void good()}><Icon name="check" size={17} />{t("support.customer.good")}</button>
            <button className="button button--secondary" type="button" disabled={pending} onClick={() => setProblemOpen(true)}>{t("support.customer.problem")}</button>
          </div>
          {problemOpen ? <IssueForm pending={pending} submitLabel={t("support.customer.sendFeedback")} onCancel={() => setProblemOpen(false)} onSubmit={submitProblem} /> : null}
        </div>
      ) : state?.feedback ? (
        <p className="transaction-care__thanks"><Icon name="check" size={17} />{t("support.customer.feedbackSaved")}</p>
      ) : null}

      {!hasOpenCase && !helpOpen ? (
        <button className="button button--secondary button--full" type="button" onClick={() => setHelpOpen(true)}>
          <Icon name="support" size={18} />{latestCase?.status === "RESOLVED" ? t("support.customer.needMoreHelp") : t("support.customer.needHelp")}
        </button>
      ) : null}
      {helpOpen ? <IssueForm pending={pending} submitLabel={t("support.customer.openCase")} onCancel={() => setHelpOpen(false)} onSubmit={submitHelp} /> : null}
    </section>
  );
}

function IssueForm({
  pending,
  submitLabel,
  onCancel,
  onSubmit,
}: {
  pending: boolean;
  submitLabel: string;
  onCancel: () => void;
  onSubmit: (category: SupportCaseCategory, comment: string | null) => Promise<void>;
}) {
  const { t } = useTranslation();
  const [category, setCategory] = useState<SupportCaseCategory>("OTHER");
  const [comment, setComment] = useState("");

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    void onSubmit(category, comment.trim() || null);
  }

  return (
    <form className="transaction-care__form" onSubmit={submit}>
      <label><span>{t("support.customer.category")}</span><select value={category} onChange={(event) => setCategory(event.target.value as SupportCaseCategory)}>{CATEGORIES.map((value) => <option key={value} value={value}>{t(`support.category.${value}`)}</option>)}</select></label>
      <label><span>{t("support.customer.comment")}</span><textarea maxLength={2000} rows={4} value={comment} onChange={(event) => setComment(event.target.value)} /></label>
      <div className="transaction-care__form-actions">
        <button className="button button--secondary" type="button" disabled={pending} onClick={onCancel}>{t("common.cancel")}</button>
        <button className="button button--primary" type="submit" disabled={pending}>{pending ? t("support.customer.sending") : submitLabel}</button>
      </div>
    </form>
  );
}
