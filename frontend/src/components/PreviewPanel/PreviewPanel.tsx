import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { getPreviewOrderId } from "../../api/MockCleaningApi";
import type { CleaningOrderStatus } from "../../domain/order";
import { changeLanguage, type AppLanguage } from "../../i18n";
import { usePlatform } from "../../platform/PlatformProvider";

const PREVIEW_ENABLED_KEY = "cleany.preview.enabled";
const PREVIEW_SCENARIO_KEY = "cleany.preview.scenario";

type PreviewScenario = CleaningOrderStatus
  | "empty"
  | "SERVICE_CATALOG"
  | "SERVICE_CATALOG_CLEANING_DISABLED"
  | "SERVICE_CATALOG_RENTAL_DISABLED"
  | "SERVICE_CATALOG_CLEANING_IN_TEST_ADMIN"
  | "SERVICE_CATALOG_CLEANING_IN_TEST_CUSTOMER"
  | "CLEANING_SHELL"
  | "ADMIN_SERVICE_CATALOG"
  | "ADMIN_CLEANING_ORDER"
  | "RENT_CATALOG"
  | "RENT_PROPERTY"
  | "RENT_DATE_RANGE_EMPTY"
  | "RENT_DATE_RANGE_CHECK_IN"
  | "RENT_DATE_RANGE"
  | "RENT_MONTHLY_EMPTY"
  | "RENT_MONTHLY"
  | "RENT_MONTHLY_UNAVAILABLE"
  | "RENT_BOOKINGS"
  | "RENT_CONFIRMED"
  | "RENT_BENEFIT"
  | "ADMIN_RENT_PROPERTIES"
  | "ADMIN_RENT_EDITOR"
  | "ADMIN_RENT_CALENDAR"
  | "ADMIN_RENT_BOOKING"
  | "WEB_UNAUTHENTICATED"
  | "WEB_CUSTOMER"
  | "WEB_ADMIN";

const scenarios: PreviewScenario[] = [
  "empty",
  "NEW",
  "ACCEPTED",
  "AWAITING_REPORT",
  "ONSITE_ISSUE_REPORTED",
  "COMPLETED",
  "REJECTED",
  "CANCELLED",
  "SERVICE_CATALOG",
  "SERVICE_CATALOG_CLEANING_DISABLED",
  "SERVICE_CATALOG_RENTAL_DISABLED",
  "SERVICE_CATALOG_CLEANING_IN_TEST_ADMIN",
  "SERVICE_CATALOG_CLEANING_IN_TEST_CUSTOMER",
  "CLEANING_SHELL",
  "ADMIN_SERVICE_CATALOG",
  "ADMIN_CLEANING_ORDER",
  "RENT_CATALOG",
  "RENT_PROPERTY",
  "RENT_DATE_RANGE_EMPTY",
  "RENT_DATE_RANGE_CHECK_IN",
  "RENT_DATE_RANGE",
  "RENT_MONTHLY_EMPTY",
  "RENT_MONTHLY",
  "RENT_MONTHLY_UNAVAILABLE",
  "RENT_BOOKINGS",
  "RENT_CONFIRMED",
  "RENT_BENEFIT",
  "ADMIN_RENT_PROPERTIES",
  "ADMIN_RENT_EDITOR",
  "ADMIN_RENT_CALENDAR",
  "ADMIN_RENT_BOOKING",
  "WEB_UNAUTHENTICATED",
  "WEB_CUSTOMER",
  "WEB_ADMIN",
];

export function PreviewPanel() {
  const { t, i18n } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const platform = usePlatform();
  const query = useMemo(() => new URLSearchParams(location.search), [location.search]);
  const requested = query.get("preview") === "true";
  const [enabled, setEnabled] = useState(
    () => requested || sessionStorage.getItem(PREVIEW_ENABLED_KEY) === "true",
  );
  const queryScenario = query.get("scenario")?.toUpperCase();
  const [scenario, setScenario] = useState<PreviewScenario>(() => {
    if (scenarios.includes(queryScenario as PreviewScenario)) {
      return queryScenario as PreviewScenario;
    }
    const stored = sessionStorage.getItem(PREVIEW_SCENARIO_KEY);
    return scenarios.includes(stored as PreviewScenario)
      ? (stored as PreviewScenario)
      : "empty";
  });

  useEffect(() => {
    if (requested) {
      sessionStorage.setItem(PREVIEW_ENABLED_KEY, "true");
      setEnabled(true);
    }
  }, [requested]);

  useEffect(() => {
    if (queryScenario && scenarios.includes(queryScenario as PreviewScenario)) {
      const next = queryScenario as PreviewScenario;
      sessionStorage.setItem(PREVIEW_SCENARIO_KEY, next);
      setScenario(next);
    }
  }, [queryScenario]);

  if (platform.kind !== "PREVIEW" || !import.meta.env.DEV || !enabled) {
    return null;
  }

  const selectScenario = (nextScenario: PreviewScenario) => {
    setScenario(nextScenario);
    sessionStorage.setItem(PREVIEW_SCENARIO_KEY, nextScenario);
    if (nextScenario === "empty") {
      navigate("/cleaning/orders?preview=true&scenario=empty");
      return;
    }
    const rentalRoutes: Partial<Record<PreviewScenario, string>> = {
      SERVICE_CATALOG: "/?preview=true&scenario=service_catalog",
      SERVICE_CATALOG_CLEANING_DISABLED: "/?preview=true&scenario=service_catalog_cleaning_disabled",
      SERVICE_CATALOG_RENTAL_DISABLED: "/?preview=true&scenario=service_catalog_rental_disabled",
      SERVICE_CATALOG_CLEANING_IN_TEST_ADMIN: "/?preview=true&scenario=service_catalog_cleaning_in_test_admin",
      SERVICE_CATALOG_CLEANING_IN_TEST_CUSTOMER: "/?preview=true&scenario=service_catalog_cleaning_in_test_customer",
      CLEANING_SHELL: "/cleaning?preview=true&scenario=cleaning_shell",
      ADMIN_SERVICE_CATALOG: "/admin?preview=true&scenario=admin_service_catalog",
      ADMIN_CLEANING_ORDER: `/admin/cleaning/orders/${getPreviewOrderId("ONSITE_ISSUE_REPORTED")}?preview=true&scenario=admin_cleaning_order`,
      RENT_CATALOG: "/rent?preview=true&scenario=rent_catalog",
      RENT_PROPERTY: "/rent/properties/kestel-sea-breeze?preview=true&scenario=rent_property",
      RENT_DATE_RANGE_EMPTY: "/rent/properties/kestel-sea-breeze?preview=true&scenario=rent_date_range_empty",
      RENT_DATE_RANGE_CHECK_IN: "/rent/properties/kestel-sea-breeze?preview=true&scenario=rent_date_range_check_in",
      RENT_DATE_RANGE: "/rent/properties/kestel-sea-breeze?preview=true&scenario=rent_date_range",
      RENT_MONTHLY_EMPTY: "/rent/properties/kestel-sea-breeze?preview=true&scenario=rent_monthly_empty",
      RENT_MONTHLY: "/rent/properties/kestel-sea-breeze?preview=true&scenario=rent_monthly",
      RENT_MONTHLY_UNAVAILABLE: "/rent/properties/kestel-sea-breeze?preview=true&scenario=rent_monthly_unavailable",
      RENT_BOOKINGS: "/rent/bookings?preview=true&scenario=rent_bookings",
      RENT_CONFIRMED: "/rent/bookings/501?preview=true&scenario=rent_confirmed",
      RENT_BENEFIT: "/rent/bookings/501?preview=true&scenario=rent_benefit",
      ADMIN_RENT_PROPERTIES: "/admin/rent/properties?preview=true&scenario=admin_rent_properties",
      ADMIN_RENT_EDITOR: "/admin/rent/properties/201?preview=true&scenario=admin_rent_editor",
      ADMIN_RENT_CALENDAR: "/admin/rent/properties/201/calendar?preview=true&scenario=admin_rent_calendar",
      ADMIN_RENT_BOOKING: "/admin/rent/bookings/501?preview=true&scenario=admin_rent_booking",
      WEB_UNAUTHENTICATED: "/admin?preview=true&scenario=web_unauthenticated",
      WEB_CUSTOMER: "/admin?preview=true&scenario=web_customer",
      WEB_ADMIN: "/admin?preview=true&scenario=web_admin",
    };
    const rentalRoute = rentalRoutes[nextScenario];
    if (rentalRoute) {
      if (nextScenario.startsWith("WEB_")) {
        window.location.assign(rentalRoute);
        return;
      }
      navigate(rentalRoute);
      return;
    }
    navigate(
      `/cleaning/orders/${getPreviewOrderId(nextScenario as CleaningOrderStatus)}?preview=true&scenario=${nextScenario.toLowerCase()}`,
    );
  };

  return (
    <aside className="preview-panel">
      <strong>{t("preview.title")}</strong>
      <label>
        <span>{t("preview.language")}</span>
        <select
          value={i18n.resolvedLanguage === "ru" ? "ru" : "en"}
          onChange={(event) => void changeLanguage(event.target.value as AppLanguage)}
        >
          <option value="ru">RU</option>
          <option value="en">EN</option>
        </select>
      </label>
      <label>
        <span>{t("preview.scenario")}</span>
        <select
          value={scenario}
          onChange={(event) => selectScenario(event.target.value as PreviewScenario)}
        >
          {scenarios.map((item) => (
            <option key={item} value={item}>
              {t(`preview.scenarios.${item}`)}
            </option>
          ))}
        </select>
      </label>
    </aside>
  );
}
