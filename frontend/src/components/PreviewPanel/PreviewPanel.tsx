import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { getPreviewOrderId } from "../../api/MockCleaningApi";
import type { CleaningOrderStatus } from "../../domain/order";
import { changeLanguage, type AppLanguage } from "../../i18n";

const PREVIEW_ENABLED_KEY = "cleany.preview.enabled";
const PREVIEW_SCENARIO_KEY = "cleany.preview.scenario";

type PreviewScenario = CleaningOrderStatus | "empty";

const scenarios: PreviewScenario[] = [
  "empty",
  "NEW",
  "ACCEPTED",
  "AWAITING_REPORT",
  "ONSITE_ISSUE_REPORTED",
  "COMPLETED",
  "REJECTED",
  "CANCELLED",
];

export function PreviewPanel() {
  const { t, i18n } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
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

  if (!import.meta.env.DEV || !enabled) {
    return null;
  }

  const selectScenario = (nextScenario: PreviewScenario) => {
    setScenario(nextScenario);
    sessionStorage.setItem(PREVIEW_SCENARIO_KEY, nextScenario);
    if (nextScenario === "empty") {
      navigate("/orders?preview=true&scenario=empty");
      return;
    }
    navigate(
      `/orders/${getPreviewOrderId(nextScenario)}?preview=true&scenario=${nextScenario.toLowerCase()}`,
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
