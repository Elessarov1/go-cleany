import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { usePlatformCatalogApi } from "../../api/PlatformCatalogApiProvider";
import { ConfirmationDialog } from "../../components/ConfirmationDialog/ConfirmationDialog";
import { Icon } from "../../components/Icon/Icon";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type {
  PlatformService,
  PlatformServiceState,
  PlatformServiceStatus,
} from "../../domain/platformService";

const STATUSES: PlatformServiceStatus[] = ["ENABLED", "IN_TEST", "DISABLED"];

export function AdminServiceCatalogPage() {
  const { t } = useTranslation();
  const api = usePlatformCatalogApi();
  const [states, setStates] = useState<PlatformServiceState[] | null>(null);
  const [failed, setFailed] = useState(false);
  const [saving, setSaving] = useState<PlatformService | null>(null);
  const [confirmDisable, setConfirmDisable] = useState<PlatformService | null>(null);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let active = true;
    setFailed(false);
    api.getAdminStates()
      .then((result) => {
        if (active) setStates(result);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey]);

  const update = (service: PlatformService, status: PlatformServiceStatus) => {
    setSaving(service);
    setFailed(false);
    api.updateStatus(service, status)
      .then((updated) => setStates((current) => current?.map((item) => (
        item.service === service ? updated : item
      )) ?? null))
      .catch(() => setFailed(true))
      .finally(() => setSaving(null));
  };

  const selectStatus = (service: PlatformService, status: PlatformServiceStatus) => {
    if (status === "DISABLED") {
      setConfirmDisable(service);
      return;
    }
    update(service, status);
  };

  if (!states && failed) {
    return (
      <ErrorState
        message={t("adminPlatform.stateLoadError")}
        onRetry={() => setReloadKey((key) => key + 1)}
      />
    );
  }
  if (!states) return <LoadingState />;

  return (
    <div className="page page--admin-service-catalog">
      <header className="page-header admin-platform-header">
        <span className="eyebrow"><BrandName /> services / admin</span>
        <h1>{t("adminPlatform.title")}</h1>
        <p>{t("adminPlatform.subtitle")}</p>
      </header>
      {failed ? <p className="admin-service-state__error" role="alert">{t("adminPlatform.stateSaveError")}</p> : null}
      <div className="admin-service-grid">
        {states.map((state) => (
          <AdminServiceCard
            key={state.service}
            state={state}
            disabled={saving === state.service}
            onStatusChange={selectStatus}
          />
        ))}
      </div>
      {confirmDisable ? (
        <ConfirmationDialog
          title={t("adminPlatform.disableTitle")}
          description={t("adminPlatform.disableDescription")}
          confirmLabel={t("adminPlatform.disableConfirm")}
          destructive
          pending={saving === confirmDisable}
          onCancel={() => setConfirmDisable(null)}
          onConfirm={() => {
            const service = confirmDisable;
            setConfirmDisable(null);
            update(service, "DISABLED");
          }}
        />
      ) : null}
    </div>
  );
}

interface AdminServiceCardProps {
  state: PlatformServiceState;
  disabled: boolean;
  onStatusChange: (service: PlatformService, status: PlatformServiceStatus) => void;
}

function AdminServiceCard({ state, disabled, onStatusChange }: AdminServiceCardProps) {
  const { t } = useTranslation();
  const rental = state.service === "RENTAL";
  const slug = rental ? "rent" : "cleaning";
  return (
    <section className={`admin-service-panel admin-service-panel--${slug}`}>
      <Link className="admin-service-card" to={rental ? "/admin/rent" : "/admin/cleaning"}>
        <span><Icon name={rental ? "building" : "sparkles"} size={31} /></span>
        <div>
          <small><BrandName service={rental ? "rental" : "cleaning"} /></small>
          <strong>{t(`adminPlatform.${slug}`)}</strong>
          <p>{t(`adminPlatform.${slug}Text`)}</p>
        </div>
        <Icon name="arrow-right" size={20} />
      </Link>
      <div className="admin-service-state">
        <label htmlFor={`service-status-${state.service}`}>{t("adminPlatform.statusLabel")}</label>
        <select
          id={`service-status-${state.service}`}
          value={state.status}
          disabled={disabled}
          onChange={(event) => onStatusChange(
            state.service,
            event.target.value as PlatformServiceStatus,
          )}
        >
          {STATUSES.map((status) => (
            <option key={status} value={status}>{t(`adminPlatform.status.${status}`)}</option>
          ))}
        </select>
        {state.status === "IN_TEST" ? <p>{t("adminPlatform.inTestDescription")}</p> : null}
      </div>
    </section>
  );
}
