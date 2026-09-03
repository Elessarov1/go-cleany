import i18n from "i18next";
import { initReactI18next } from "react-i18next";

export const SUPPORTED_LANGUAGES = ["ru", "en"] as const;
export type AppLanguage = (typeof SUPPORTED_LANGUAGES)[number];

const LANGUAGE_KEY = "cleany.language";

type TranslationMessages = Record<string, unknown>;

const localeLoaders: Record<AppLanguage, () => Promise<{ default: TranslationMessages }>> = {
  en: () => import("./en.json"),
  ru: () => import("./ru.json"),
};

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

const languageTranslations = {
  en: {
    label: "Language",
  },
  ru: {
    label: "Язык",
  },
} as const;

const footerTranslations = {
  en: {
    tagline: "Everyday services in Alanya, in one place.",
    services: "Services",
    legal: "Legal",
    privacy: "Privacy",
    terms: "Terms of Use",
  },
  ru: {
    tagline: "Повседневные сервисы в Аланье — в одном месте.",
    services: "Сервисы",
    legal: "Правовая информация",
    privacy: "Конфиденциальность",
    terms: "Условия использования",
  },
} as const;

const legalTranslations = {
  en: {
    pilotNote: "Loco Place is currently in pilot. These pages describe the current product behavior and will be updated with the operator's legal details before commercial launch.",
    privacy: {
      title: "Privacy",
      intro: "This notice explains the main categories of data Loco Place currently uses to provide Cleaning and Rental services during the pilot.",
      sections: {
        first: {
          title: "Data we use",
          text: "We may process account identifiers from Google or Telegram, contact details you provide, order and booking information, referral or acquisition source data, and operational photos when a service workflow requires them.",
        },
        second: {
          title: "Why we use it",
          text: "We use this information to provide and manage services, communicate about orders and bookings, maintain account security, prevent abuse, and understand how customers discover and use Loco Place.",
        },
        third: {
          title: "Retention",
          text: "Operational media is retained only for the configured service period. Other pilot data may be retained while the pilot is running and may be cleared before the commercial launch.",
        },
        fourth: {
          title: "Third-party services",
          text: "Google and Telegram may process information when you use their authentication or communication features under their own policies. Loco Place does not sell personal data to advertisers.",
        },
      },
    },
    terms: {
      title: "Terms of Use",
      intro: "These pilot terms describe the basic rules for using Loco Place before the commercial launch.",
      sections: {
        first: {
          title: "Using the platform",
          text: "Loco Place lets you request Cleaning services and browse or book Rental properties where those services are enabled. Some functionality may still be marked as in test during the pilot.",
        },
        second: {
          title: "Your information",
          text: "Please provide accurate contact, address, booking and service information. You are responsible for keeping access to your Google or Telegram account secure.",
        },
        third: {
          title: "Prices, availability and cancellation",
          text: "Current prices, availability, cancellation options and payment conditions are shown in the relevant service flow. A request or booking is subject to the status and rules displayed in the application.",
        },
        fourth: {
          title: "Pilot availability",
          text: "During the pilot we may change, limit or temporarily disable features while the service is being prepared for commercial operation. Existing confirmed operations will be handled according to the status shown in the application.",
        },
      },
    },
  },
  ru: {
    pilotNote: "Loco Place пока работает в пилотном режиме. Эти страницы описывают текущее поведение продукта и будут дополнены юридическими реквизитами оператора до коммерческого запуска.",
    privacy: {
      title: "Конфиденциальность",
      intro: "Здесь перечислены основные категории данных, которые Loco Place сейчас использует для работы сервисов Cleaning и Rental в рамках пилота.",
      sections: {
        first: {
          title: "Какие данные мы используем",
          text: "Мы можем обрабатывать идентификаторы аккаунта Google или Telegram, предоставленные вами контактные данные, сведения о заказах и бронированиях, данные об источнике привлечения или реферальной программе, а также операционные фотографии, когда они требуются процессом оказания услуги.",
        },
        second: {
          title: "Для чего они нужны",
          text: "Эти данные используются для оказания и сопровождения услуг, уведомлений о заказах и бронированиях, защиты аккаунтов, предотвращения злоупотреблений и понимания того, как клиенты находят и используют Loco Place.",
        },
        third: {
          title: "Срок хранения",
          text: "Операционные медиафайлы хранятся только в течение настроенного для сервиса периода. Другие данные пилота могут храниться до завершения тестового периода и могут быть очищены перед коммерческим запуском.",
        },
        fourth: {
          title: "Сторонние сервисы",
          text: "Google и Telegram могут обрабатывать информацию при использовании их авторизации или коммуникационных функций в соответствии со своими правилами. Loco Place не продаёт персональные данные рекламодателям.",
        },
      },
    },
    terms: {
      title: "Условия использования",
      intro: "Эти условия описывают базовые правила использования Loco Place в течение пилотного периода до коммерческого запуска.",
      sections: {
        first: {
          title: "Использование платформы",
          text: "Loco Place позволяет заказывать услуги Cleaning и просматривать или бронировать объекты Rental там, где соответствующий сервис доступен. Во время пилота отдельные функции могут быть отмечены как тестовые.",
        },
        second: {
          title: "Ваши данные",
          text: "Указывайте корректные контакты, адреса и сведения, необходимые для заказа или бронирования. Вы отвечаете за безопасность доступа к своему аккаунту Google или Telegram.",
        },
        third: {
          title: "Цены, доступность и отмена",
          text: "Актуальные цены, доступность, возможность отмены и условия оплаты показываются непосредственно в соответствующем сценарии сервиса. Заказ или бронирование подчиняются статусу и правилам, отображаемым в приложении.",
        },
        fourth: {
          title: "Работа в пилотном режиме",
          text: "Во время пилота мы можем изменять, ограничивать или временно отключать отдельные функции, пока сервис готовится к коммерческой эксплуатации. Уже подтверждённые операции обрабатываются в соответствии со статусом, отображаемым в приложении.",
        },
      },
    },
  },
} as const;

function isAppLanguage(value: string | null): value is AppLanguage {
  return value !== null && SUPPORTED_LANGUAGES.includes(value as AppLanguage);
}

function resolveLanguage(platformLanguage: string | null): AppLanguage {
  const storedLanguage = localStorage.getItem(LANGUAGE_KEY);
  if (isAppLanguage(storedLanguage)) {
    return storedLanguage;
  }

  return platformLanguage?.toLowerCase().startsWith("ru") ? "ru" : "en";
}

async function loadTranslation(language: AppLanguage): Promise<TranslationMessages> {
  const { default: messages } = await localeLoaders[language]();
  const legalTitles = language === "ru"
    ? {
        privacy: "Конфиденциальность · Loco Place",
        terms: "Условия использования · Loco Place",
      }
    : {
        privacy: "Privacy · Loco Place",
        terms: "Terms of Use · Loco Place",
      };

  return {
    ...messages,
    theme: themeTranslations[language],
    language: languageTranslations[language],
    footer: footerTranslations[language],
    legal: legalTranslations[language],
    titles: {
      ...(messages.titles as TranslationMessages),
      ...legalTitles,
    },
  };
}

async function ensureLanguageLoaded(language: AppLanguage): Promise<void> {
  if (i18n.hasResourceBundle(language, "translation")) {
    return;
  }

  i18n.addResourceBundle(language, "translation", await loadTranslation(language), true, true);
}

export async function initializeI18n(
  platformLanguage: string | null,
): Promise<void> {
  const language = resolveLanguage(platformLanguage);

  if (!i18n.isInitialized) {
    const translation = await loadTranslation(language);
    await i18n.use(initReactI18next).init({
      resources: {
        [language]: {
          translation,
        },
      },
      lng: language,
      fallbackLng: "en",
      supportedLngs: SUPPORTED_LANGUAGES,
      interpolation: {
        escapeValue: false,
      },
    });
  }

  document.documentElement.lang = language;
}

export async function changeLanguage(language: AppLanguage): Promise<void> {
  await ensureLanguageLoaded(language);
  localStorage.setItem(LANGUAGE_KEY, language);
  document.documentElement.lang = language;
  await i18n.changeLanguage(language);
}

export default i18n;
