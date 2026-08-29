import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import en from "./en.json";
import ru from "./ru.json";

export type AppLanguage = "ru" | "en";

const LANGUAGE_KEY = "cleany.language";

const themeTranslations = {
  en: {
    label: "Color theme",
    light: "Light theme",
    dark: "Dark theme",
  },
  ru: {
    label: "Цветовая тема",
    light: "Светлая тема",
    dark: "Тёмная тема",
  },
} as const;

function resolveLanguage(platformLanguage: string | null): AppLanguage {
  const storedLanguage = localStorage.getItem(LANGUAGE_KEY);
  if (storedLanguage === "ru" || storedLanguage === "en") {
    return storedLanguage;
  }

  return platformLanguage?.toLowerCase().startsWith("ru") ? "ru" : "en";
}

export async function initializeI18n(
  platformLanguage: string | null,
): Promise<void> {
  const language = resolveLanguage(platformLanguage);

  if (!i18n.isInitialized) {
    await i18n.use(initReactI18next).init({
      resources: {
        en: { translation: { ...en, theme: themeTranslations.en } },
        ru: { translation: { ...ru, theme: themeTranslations.ru } },
      },
      lng: language,
      fallbackLng: "en",
      supportedLngs: ["en", "ru"],
      interpolation: {
        escapeValue: false,
      },
    });
  }

  document.documentElement.lang = language;
}

export async function changeLanguage(language: AppLanguage): Promise<void> {
  localStorage.setItem(LANGUAGE_KEY, language);
  document.documentElement.lang = language;
  await i18n.changeLanguage(language);
}

export default i18n;
