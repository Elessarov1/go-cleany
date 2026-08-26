import { useEffect, useState, type PropsWithChildren } from "react";
import { useTranslation } from "react-i18next";
import { usePlatformCatalogApi } from "../../api/PlatformCatalogApiProvider";
import type { PlatformService } from "../../domain/platformService";
import { ErrorState, LoadingState } from "../PageState/PageState";

interface ServiceAvailabilityGateProps extends PropsWithChildren {
  service: PlatformService;
}

export function ServiceAvailabilityGate({
  service,
  children,
}: ServiceAvailabilityGateProps) {
  const { t } = useTranslation();
  const api = usePlatformCatalogApi();
  const [available, setAvailable] = useState<boolean | null>(null);
  const [failed, setFailed] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let active = true;
    setFailed(false);
    setAvailable(null);
    api.getServices()
      .then((services) => {
        if (active) setAvailable(services.some((item) => item.service === service));
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey, service]);

  if (failed) {
    return (
      <ErrorState
        message={t("catalog.loadError")}
        onRetry={() => setReloadKey((key) => key + 1)}
      />
    );
  }
  if (available === null) return <LoadingState />;
  if (!available) {
    return <ErrorState message={t("catalog.unavailable")} />;
  }
  return children;
}
