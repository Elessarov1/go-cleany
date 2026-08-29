import { useEffect, useId, useRef, useState } from "react";
import { NavLink, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { usePlatformCatalogApi } from "../../api/PlatformCatalogApiProvider";
import type { PlatformService } from "../../domain/platformService";
import { BrandName } from "../BrandName/BrandName";
import { Icon, type IconName } from "../Icon/Icon";

interface ServiceSelectorProps {
  compact?: boolean;
}

interface ServiceOption {
  path: string;
  service: "cleaning" | "rental" | "transfer";
  icon: IconName;
  titleKey: string;
}

const SERVICES: Record<PlatformService, ServiceOption> = {
  CLEANING: { path: "/cleaning", service: "cleaning", icon: "sparkles", titleKey: "catalog.cleaning.title" },
  RENTAL: { path: "/rent", service: "rental", icon: "building", titleKey: "catalog.rent.title" },
  TRANSFER: { path: "/transfer", service: "transfer", icon: "car", titleKey: "catalog.transfer.title" },
};

const DEFAULT_ORDER: PlatformService[] = ["CLEANING", "RENTAL", "TRANSFER"];

export function ServiceSelector({ compact = false }: ServiceSelectorProps) {
  const { t } = useTranslation();
  const catalogApi = usePlatformCatalogApi();
  const location = useLocation();
  const menuId = useId();
  const containerRef = useRef<HTMLDivElement>(null);
  const [open, setOpen] = useState(false);
  const [serviceOrder, setServiceOrder] = useState<PlatformService[]>(DEFAULT_ORDER);

  useEffect(() => {
    let active = true;
    void catalogApi.getServices()
      .then((states) => {
        if (active) setServiceOrder(states.map((state) => state.service));
      })
      .catch(() => undefined);
    return () => {
      active = false;
    };
  }, [catalogApi]);

  useEffect(() => {
    setOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    if (!open) return undefined;

    const closeOnOutsideClick = (event: PointerEvent) => {
      if (!containerRef.current?.contains(event.target as Node)) setOpen(false);
    };
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") setOpen(false);
    };

    document.addEventListener("pointerdown", closeOnOutsideClick);
    document.addEventListener("keydown", closeOnEscape);
    return () => {
      document.removeEventListener("pointerdown", closeOnOutsideClick);
      document.removeEventListener("keydown", closeOnEscape);
    };
  }, [open]);

  return (
    <div
      className={`service-selector${compact ? " service-selector--compact" : ""}`}
      ref={containerRef}
    >
      <button
        className="service-selector__trigger"
        type="button"
        aria-controls={menuId}
        aria-expanded={open}
        aria-haspopup="menu"
        title={t("app.navigation.services")}
        onClick={() => setOpen((current) => !current)}
      >
        <Icon name="services" size={18} />
        <span>{t("app.navigation.services")}</span>
        <Icon name="arrow-right" size={16} />
      </button>

      <nav
        className="service-selector__menu"
        id={menuId}
        aria-label={t("app.navigation.services")}
        hidden={!open}
      >
        <strong>{t("catalog.title")}</strong>
        {serviceOrder.map((service) => SERVICES[service]).map((option) => (
          <NavLink
            className={({ isActive }) => `service-selector__item${isActive ? " is-active" : ""}`}
            key={option.path}
            to={option.path}
          >
            <span className="service-selector__icon"><Icon name={option.icon} size={22} /></span>
            <span>
              <BrandName service={option.service} />
              <small>{t(option.titleKey)}</small>
            </span>
            <Icon name="arrow-right" size={17} />
          </NavLink>
        ))}
      </nav>
    </div>
  );
}
