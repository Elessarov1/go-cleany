import { useTranslation } from "react-i18next";
import { changeLanguage, type AppLanguage } from "../../i18n";

export function LanguageSwitcher() {
  const { i18n } = useTranslation();
  const activeLanguage: AppLanguage = i18n.resolvedLanguage === "ru" ? "ru" : "en";

  return (
    <div className="language-switcher" aria-label="Language">
      {(["ru", "en"] as AppLanguage[]).map((language) => (
        <button
          key={language}
          className={activeLanguage === language ? "is-active" : undefined}
          type="button"
          aria-pressed={activeLanguage === language}
          onClick={() => void changeLanguage(language)}
        >
          {language.toUpperCase()}
        </button>
      ))}
    </div>
  );
}

