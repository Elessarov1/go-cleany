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
      .then((updated) => setStates((current) => replaceAndSort(current, updated)))
      .catch(() => setFailed(true))
      .finally(() => setSaving(null));
  };

  const updateDisplayOrder = (
    service: PlatformService,
    displayOrder: number,
  ): Promise<void> => {
    setSaving(service);
    setFailed(false);
    return api.updateDisplayOrder(service, displayOrder)
      .then((updated) => setStates((current) => replaceAndSort(current, updated)))
      .catch((error: unknown) => {
        setFailed(true);
        throw error;
      })
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
      <Link className="admin-analytics-entry" to="/admin/analytics">
        <span><Icon name="chart" size={25} /></span>
        <div>
          <strong>{t("analytics.title")}</strong>
          <p>{t("analytics.entrySubtitle")}</p>
        </div>
        <Icon name="arrow-right" size={20} />
      </Link>
      {failed ? <p className="admin-service-state__error" role="alert">{t("adminPlatform.stateSaveError")}</p> : null}
      <div className="admin-service-grid">
        {states.map((state) => (
          <AdminServiceCard
            key={state.service}
            state={state}
            disabled={saving === state.service}
            onStatusChange={selectStatus}
            onDisplayOrderChange={updateDisplayOrder}
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
  onDisplayOrderChange: (service: PlatformService, displayOrder: number) => Promise<void>;
}

function AdminServiceCard({ state, disabled, onStatusChange, onDisplayOrderChange }: AdminServiceCardProps) {
  const { t } = useTranslation();
  const [orderValue, setOrderValue] = useState(state.displayOrder.toString());
  const rental = state.service === "RENTAL";
  const transfer = state.service === "TRANSFER";
  const slug = transfer ? "transfer" : rental ? "rent" : "cleaning";

  useEffect(() => {
    setOrderValue(state.displayOrder.toString());
  }, [state.displayOrder]);

  const saveDisplayOrder = () => {
    const parsed = Number(orderValue);
    if (!Number.isInteger(parsed) || parsed < 0 || parsed > 9999) {
      setOrderValue(state.displayOrder.toString());
      return;
    }
    if (parsed === state.displayOrder) return;
    void onDisplayOrderChange(state.service, parsed)
      .catch(() => setOrderValue(state.displayOrder.toString()));
  };

  return (
    <section className={`admin-service-panel admin-service-panel--${slug}`}>
      <Link className="admin-service-card" to={transfer ? "/admin/transfer" : rental ? "/admin/rent" : "/admin/cleaning"}>
        <span><Icon name={transfer ? "car" : rental ? "building" : "sparkles"} size={31} /></span>
        <div>
          <small><BrandName service={transfer ? "transfer" : rental ? "rental" : "cleaning"} /></small>
          <strong>{t(`adminPlatform.${slug}`)}</strong>
          <p>{t(`adminPlatform.${slug}Text`)}</p>
        </div>
        <Icon name="arrow-right" size={20} />
      </Link>
      <div className="admin-service-state">
        <div className="admin-service-state__controls">
          <label>
            <span>{t("adminPlatform.statusLabel")}</span>
            <select
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
          </label>
          <label>
            <span>{t("adminPlatform.displayOrderLabel")}</span>
            <input
              aria-describedby={`service-order-hint-${state.service}`}
              type="number"
              min={0}
              max={9999}
              step={10}
              value={orderValue}
              disabled={disabled}
              onChange={(event) => setOrderValue(event.target.value)}
              onBlur={saveDisplayOrder}
              onKeyDown={(event) => {
                if (event.key === "Enter") event.currentTarget.blur();
              }}
            />
          </label>
        </div>
        <small id={`service-order-hint-${state.service}`} className="admin-service-state__hint">{t("adminPlatform.displayOrderHint")}</small>
        {state.status === "IN_TEST" ? <p>{t("adminPlatform.inTestDescription")}</p> : null}
      </div>
    </section>
  );
}

function replaceAndSort(
  current: PlatformServiceState[] | null,
  updated: PlatformServiceState,
): PlatformServiceState[] | null {
  if (!current) return null;
  return current
    .map((item) => item.service === updated.service ? updated : item)
    .sort((left, right) => left.displayOrder - right.displayOrder
      || left.service.localeCompare(right.service));
}
