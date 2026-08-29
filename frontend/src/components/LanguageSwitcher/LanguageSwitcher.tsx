import type { ChangeEvent } from "react";
import { useTranslation } from "react-i18next";
import {
  changeLanguage,
  SUPPORTED_LANGUAGES,
  type AppLanguage,
} from "../../i18n";

export function LanguageSwitcher() {
  const { t, i18n } = useTranslation();
  const activeLanguage: AppLanguage = i18n.resolvedLanguage === "ru" ? "ru" : "en";

  const handleChange = (event: ChangeEvent<HTMLSelectElement>) => {
    void changeLanguage(event.target.value as AppLanguage);
  };

  return (
    <div className="language-selector">
      <select
        value={activeLanguage}
        aria-label={t("language.label")}
        title={t("language.label")}
        onChange={handleChange}
      >
        {SUPPORTED_LANGUAGES.map((language) => (
          <option key={language} value={language}>
            {language.toUpperCase()}
          </option>
        ))}
      </select>
    </div>
  );
}
