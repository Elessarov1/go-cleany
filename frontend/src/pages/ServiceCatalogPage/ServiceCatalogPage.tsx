import { useEffect, useState } from "react";
import { Link, Navigate, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { usePlatformCatalogApi } from "../../api/PlatformCatalogApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { PlatformServiceState } from "../../domain/platformService";
import {
  ServiceCatalogScene,
  type ServiceCatalogSceneState,
} from "./ServiceCatalogScene";

export function ServiceCatalogPage() {
  const { t } = useTranslation();
  const api = usePlatformCatalogApi();
  const [searchParams] = useSearchParams();
  const [services, setServices] = useState<PlatformServiceState[] | null>(null);
  const [failed, setFailed] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [sceneState, setSceneState] = useState<ServiceCatalogSceneState>("idle");
  const referralCode = searchParams.get("ref")?.trim();

  useEffect(() => {
    let active = true;
    setFailed(false);
    api.getServices()
      .then((result) => {
        if (active) setServices(result);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey]);

  if (failed) {
    return (
      <ErrorState
        message={t("catalog.loadError")}
        onRetry={() => setReloadKey((key) => key + 1)}
      />
    );
  }
  if (!services) return <LoadingState />;

  const cleaning = services.find(({ service }) => service === "CLEANING");
  const rental = services.find(({ service }) => service === "RENTAL");
  if (referralCode && cleaning) {
    return <Navigate replace to={`/cleaning?ref=${encodeURIComponent(referralCode)}`} />;
  }

  return (
    <div className="page page--service-catalog">
      <div className="service-catalog__hero">
        <header className="service-catalog__header">
          <span className="eyebrow"><BrandName /> services</span>
          <h1>{t("catalog.title")}</h1>
          <p>{t("catalog.subtitle")}</p>
        </header>
        <ServiceCatalogScene activeService={sceneState} />
      </div>

      <div className="service-catalog__grid">
        {rental ? (
          <ServiceCard
            className="rent"
            icon="building"
            path="/rent"
            title={t("catalog.rent.title")}
            text={t("catalog.rent.text")}
            test={rental.status === "IN_TEST"}
            onActivate={() => setSceneState("rent")}
            onDeactivate={() => setSceneState("idle")}
          />
        ) : null}
        {cleaning ? (
          <ServiceCard
            className="cleaning"
            icon="sparkles"
            path="/cleaning"
            title={t("catalog.cleaning.title")}
            text={t("catalog.cleaning.text")}
            test={cleaning.status === "IN_TEST"}
            onActivate={() => setSceneState("cleaning")}
            onDeactivate={() => setSceneState("idle")}
          />
        ) : null}
      </div>

      {services.length === 0 ? (
        <p className="service-catalog__empty">{t("catalog.empty")}</p>
      ) : null}
      <p className="service-catalog__note">{t("catalog.note")}</p>
    </div>
  );
}

interface ServiceCardProps {
  className: "rent" | "cleaning";
  icon: "building" | "sparkles";
  path: string;
  title: string;
  text: string;
  test: boolean;
  onActivate: () => void;
  onDeactivate: () => void;
}

function ServiceCard({
  className,
  icon,
  path,
  title,
  text,
  test,
  onActivate,
  onDeactivate,
}: ServiceCardProps) {
  const { t } = useTranslation();
  return (
    <Link
      className={`service-card service-card--${className}`}
      to={path}
      onPointerEnter={onActivate}
      onPointerLeave={onDeactivate}
      onPointerDown={onActivate}
      onFocus={onActivate}
      onBlur={onDeactivate}
    >
      <span className="service-card__icon"><Icon name={icon} size={34} /></span>
      <span className="service-card__copy">
        <small><BrandName service={className === "rent" ? "rental" : "cleaning"} /></small>
        <strong>{title}</strong>
        {test ? <b className="service-test-badge">{t("catalog.testBadge")}</b> : null}
        <span>{text}</span>
      </span>
      <Icon name="arrow-right" size={20} />
    </Link>
  );
}
