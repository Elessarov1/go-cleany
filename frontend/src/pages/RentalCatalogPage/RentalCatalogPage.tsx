import { useEffect, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalAppliedCriteria } from "../../components/RentalAppliedCriteria/RentalAppliedCriteria";
import { RentalSearchForm } from "../../components/RentalSearchForm/RentalSearchForm";
import { RentalSearchResults } from "../../components/RentalSearchResults/RentalSearchResults";
import type { RentalConfiguration, RentalSearchRequest, RentalSearchResponse } from "../../domain/rental";
import { rentalLanguage } from "../../utils/rental";
import {
  emptyRentalSearchDraft,
  parseRentalSearchQuery,
  rememberRentalSearchExecution,
  rentalSearchKey,
  rentalSearchRequestFromDraft,
  serializeRentalSearch,
  validateRentalSearchDraft,
  type RentalSearchDraft,
  type RentalSearchField,
} from "../../utils/rentalSearch";

export function RentalCatalogPage() {
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const [searchParams, setSearchParams] = useSearchParams();
  const [configuration, setConfiguration] = useState<RentalConfiguration | null>(null);
  const [configurationError, setConfigurationError] = useState(false);
  const [draft, setDraft] = useState<RentalSearchDraft>(emptyRentalSearchDraft);
  const [errors, setErrors] = useState<Partial<Record<RentalSearchField, string>>>({});
  const [response, setResponse] = useState<RentalSearchResponse | null>(null);
  const [activeRequest, setActiveRequest] = useState<RentalSearchRequest | null>(null);
  const [searching, setSearching] = useState(false);
  const [searchError, setSearchError] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const generation = useRef(0);
  const pendingPreviousExecution = useRef<string | undefined>(undefined);
  const lastAppliedKey = useRef<string | null>(null);
  const searchStartedAt = useRef(0);
  const firstCardTracked = useRef(new Set<string>());
  const query = searchParams.toString();
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setConfigurationError(false);
    api.getConfiguration()
      .then((value) => { if (active) setConfiguration(value); })
      .catch(() => { if (active) setConfigurationError(true); });
    return () => { active = false; };
  }, [api]);

  useEffect(() => {
    if (!configuration) return undefined;
    const parsed = parseRentalSearchQuery(new URLSearchParams(query), configuration);
    setDraft(parsed.draft);
    setErrors(parsed.errors);
    setSearchError(false);
    if (!parsed.applied) {
      generation.current += 1;
      setSearching(false);
      setResponse(null);
      setActiveRequest(null);
      lastAppliedKey.current = null;
      return undefined;
    }

    const request = parsed.applied;
    const currentGeneration = ++generation.current;
    const controller = new AbortController();
    const previous = pendingPreviousExecution.current;
    pendingPreviousExecution.current = undefined;
    lastAppliedKey.current = rentalSearchKey(request);
    searchStartedAt.current = performance.now();
    setSearching(true);
    setResponse(null);
    setActiveRequest(request);
    api.search(request, controller.signal, previous)
      .then((value) => {
        if (currentGeneration !== generation.current) return;
        rememberRentalSearchExecution(request, value.searchExecutionId);
        setResponse(value);
      })
      .catch((error: unknown) => {
        if (currentGeneration !== generation.current) return;
        if (error instanceof DOMException && error.name === "AbortError") return;
        setSearchError(true);
      })
      .finally(() => {
        if (currentGeneration === generation.current) setSearching(false);
      });
    return () => controller.abort();
  }, [api, configuration, query, refreshKey]);

  useEffect(() => {
    if (!response || response.properties.length === 0) return undefined;
    if (firstCardTracked.current.has(response.searchExecutionId)) return undefined;
    firstCardTracked.current.add(response.searchExecutionId);
    const frame = requestAnimationFrame(() => {
      void api.recordFirstCardRendered(
        response.searchExecutionId,
        performance.now() - searchStartedAt.current,
      ).catch(() => undefined);
    });
    return () => cancelAnimationFrame(frame);
  }, [api, response]);

  const applyRequest = (request: RentalSearchRequest) => {
    const nextKey = rentalSearchKey(request);
    if (response && lastAppliedKey.current && lastAppliedKey.current !== nextKey) {
      pendingPreviousExecution.current = response.searchExecutionId;
    }
    if (nextKey === query) setRefreshKey((value) => value + 1);
    else setSearchParams(serializeRentalSearch(request));
  };

  const submit = () => {
    if (!configuration) return;
    const nextErrors = validateRentalSearchDraft(draft, configuration);
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;
    applyRequest(rentalSearchRequestFromDraft(draft));
  };

  if (configurationError) {
    return <ErrorState message={t("rental.catalog.loadError")} onRetry={() => window.location.reload()} />;
  }
  if (!configuration) return <LoadingState />;

  return (
    <div className="page page--rental-catalog">
      <header className="rental-hero rental-search-hero">
        <div>
          <span className="eyebrow"><BrandName service="rental" /> · Alanya</span>
          <h1>{t("rental.search.title")}</h1>
          <p>{t("rental.search.subtitle")}</p>
        </div>
        <span className="rental-hero__mark" aria-hidden="true"><Icon name="building" size={42} /></span>
      </header>

      <RentalSearchForm
        configuration={configuration}
        draft={draft}
        errors={errors}
        searching={searching}
        onChange={(value) => { setDraft(value); setErrors({}); }}
        onSubmit={submit}
        onBrowseAll={() => applyRequest({ view: "all" })}
      />

      {response && activeRequest ? (
        <section className="rental-search-results">
          <div className="rental-search-results__heading">
            <div>
              <span className="eyebrow">{t("rental.search.resultsEyebrow")}</span>
              <h2>{t("rental.search.results", { count: response.properties.length })}</h2>
            </div>
            <RentalAppliedCriteria criteria={response.criteria} locale={locale} />
          </div>
          <RentalSearchResults response={response} request={activeRequest} language={language} locale={locale} />
        </section>
      ) : null}
      {searching ? <RentalSearchSkeleton label={t("rental.search.searching")} /> : null}
      {searchError ? (
        <ErrorState message={t("rental.search.loadError")} onRetry={() => setRefreshKey((value) => value + 1)} />
      ) : null}
      {!activeRequest && !searching ? (
        <p className="rental-search-initial"><Icon name="calendar-plus" size={20} />{t("rental.search.initialHint")}</p>
      ) : null}
    </div>
  );
}

function RentalSearchSkeleton({ label }: { label: string }) {
  return (
    <div className="rental-search-skeleton" aria-label={label} aria-busy="true">
      {[0, 1].map((item) => <div key={item}><span /><i /><i /></div>)}
    </div>
  );
}
