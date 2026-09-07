import i18n, { SUPPORTED_LANGUAGES, type AppLanguage } from "./index";

type TranslationOverrides = Record<string, unknown>;

const publicCopy: Record<AppLanguage, TranslationOverrides> = {
  ru: {
    app: {
      tagline: "Бытовые задачи в Аланье — без самостоятельного поиска исполнителей",
    },
    create: {
      title: "Уборка квартиры в Аланье по фиксированной цене",
      subtitle: "Выберите район, размер квартиры и тип уборки. Цена известна заранее, клинер подтвердит дату, а после выполнения вы получите фотоотчёт.",
      details: {
        referralCodeHint: "Введите код друга или партнёра, чтобы получить доступную скидку на первый заказ.",
      },
      rentalContext: {
        prefilled: "Адрес и телефон уже заполнены из вашего бронирования Loco Rental.",
      },
    },
    cleaning: {
      REGULAR: {
        description: "Поддерживающая уборка, чтобы освежить квартиру и вернуть повседневный порядок.",
      },
      DEEP: {
        description: "Более тщательная уборка кухни, ванной, дверей и доступных поверхностей при заметных загрязнениях.",
      },
    },
    created: {
      text: "Заказ отправлен команде клинеров. Мы сообщим о подтверждении в Loco и продублируем его в Telegram, если он подключён.",
    },
    catalog: {
      title: "Уборка, аренда квартир и трансфер в Аланье",
      subtitle: "Выберите нужную услугу, заранее посмотрите цену и доступность, а Loco организует остальное — без долгих поисков и лишних звонков.",
      note: "Повторный заказ — без заполнения с нуля: адрес, контакты и история уже будут в Loco.",
      rent: {
        title: "Арендовать квартиру",
        text: "Квартиры в Аланье от недели — свободные даты и цена видны до бронирования",
      },
      cleaning: {
        title: "Заказать уборку",
        text: "Уборка квартиры по фиксированной цене с фотоотчётом после выполнения",
      },
      transfer: {
        title: "Заказать трансфер",
        text: "Трансфер между Аланьей и аэропортами Антальи или Газипаши по фиксированной цене",
      },
      home: {
        allActivity: "Все задачи · {{count}}",
        actions: {
          RENTAL_TRANSFER_ARRIVAL: {
            title: "Заказать трансфер из аэропорта",
            text: "Дата заезда и адрес квартиры уже заполнены: {{date}}",
          },
          RENTAL_TRANSFER_CHECKOUT: {
            title: "Заказать трансфер в аэропорт",
            text: "Дата выезда и адрес подачи уже заполнены: {{date}}",
          },
          RENTAL_CLEANING: {
            title: "Заказать уборку перед выездом",
            text: "Адрес уже заполнен, персональная выгода доступна на {{date}}",
          },
        },
      },
    },
    rental: {
      catalog: {
        title: "Аренда квартир в Аланье от недели",
        subtitle: "Выберите квартиру, проверьте свободные даты и узнайте итоговую стоимость до бронирования.",
        emptyTitle: "Сейчас нет доступных квартир",
        emptyText: "Мы показываем только квартиры с открытыми датами. Проверьте каталог позже.",
      },
      booking: {
        selectDates: "Выберите даты заезда и выезда — итоговая стоимость появится сразу.",
        selectMonthlyStart: "Выберите дату начала и срок проживания — мы проверим свободные даты и покажем итоговую стоимость.",
        confirm: "Забронировать квартиру",
      },
      bookings: {
        subtitle: "Все предстоящие и завершённые проживания в одном месте.",
      },
      bookingDetails: {
        cleaningTitle: "Уборка перед выездом",
        cleaningText: "Закажите уборку этой квартиры без повторного заполнения: адрес и телефон уже добавлены.",
        transferTitle: "Трансфер к заезду или выезду",
        transferText: "Дата и адрес уже заполнены — выберите аэропорт, время и машину.",
      },
    },
    transfer: {
      title: "Трансфер из аэропорта в Аланью и обратно",
      subtitle: "Выберите аэропорт, машину, дату и время. Цена фиксируется заранее, а Loco назначит водителя и сообщит о подтверждении поездки.",
      unavailableTitle: "Трансфер временно недоступен",
      unavailableText: "Сейчас мы не принимаем новые заявки. Попробуйте немного позже.",
      submit: "Заказать трансфер",
      summary: {
        requested: "Онлайн-оплата не требуется. После заявки Loco назначит водителя и сообщит, когда поездка будет подтверждена.",
      },
      landing: {
        title: "Что вы получаете",
        directions: "Поездки из аэропорта в Аланью и обратно",
        price: "Фиксированная цена известна до отправки заявки",
        driver: "Loco назначит водителя и сообщит о подтверждении",
      },
    },
    account: {
      subtitle: "Подключите Telegram и выберите, где получать подтверждения и изменения по заказам.",
      activityText: "Все ваши уборки, бронирования квартир и трансферы — текущие и завершённые — собраны в одном месте.",
      nudgeText: "Подключите Telegram, чтобы получать подтверждения, изменения статусов и сообщения о готовности фотоотчётов.",
    },
    customerHub: {
      title: "Мои задачи",
      subtitle: "Следите за текущими заказами, возвращайтесь к прошлым и проверяйте важные обновления.",
    },
    activity: {
      subtitle: "Все ваши уборки, бронирования квартир и трансферы — текущие и завершённые — в одном месте.",
    },
    notifications: {
      subtitle: "Подтверждения, изменения статусов и напоминания по вашим задачам.",
      emptyText: "Здесь появятся подтверждения, изменения статусов и полезные напоминания.",
    },
    support: {
      customer: {
        subtitle: "Расскажите, что произошло. Номер заказа и его детали уже добавлены — повторять их не нужно.",
        feedbackText: "Ваш ответ помогает нам контролировать качество и быстрее исправлять проблемы.",
        openCase: "Сообщить о проблеме",
      },
    },
    adminRental: {
      notifications: {
        description: "Получайте сообщения о новых бронированиях и отменах клиентов в Loco Rental.",
      },
    },
    analytics: {
      campaigns: {
        targetServices: {
          RENTAL: "Loco Rental",
        },
      },
    },
    footer: {
      tagline: "Уборка, аренда квартир и трансфер в Аланье — Loco организует остальное.",
    },
    legal: {
      privacy: {
        intro: "Здесь перечислены основные категории данных, которые Loco Place использует для работы сервисов Cleaning, Rental и Transfer в рамках пилота.",
      },
      terms: {
        sections: {
          first: {
            text: "Loco Place позволяет заказывать уборку, просматривать и бронировать квартиры, а также оформлять трансфер там, где соответствующий сервис доступен. Во время пилота отдельные функции могут быть отмечены как тестовые.",
          },
        },
      },
    },
    titles: {
      fallback: "Loco Place — сервисы в Аланье",
      description: "Loco Place помогает заказать уборку квартиры, арендовать жильё и оформить трансфер между Аланьей и аэропортами.",
      home: "Уборка, аренда квартир и трансфер в Аланье | Loco Place",
      homeDescription: "Закажите уборку квартиры, арендуйте жильё или оформите трансфер между Аланьей и аэропортами. Цены и доступность видны до заказа.",
      cleaning: "Уборка квартир в Аланье | Loco Cleaning",
      cleaningDescription: "Обычная и генеральная уборка квартир в Махмутларе, Каргыджаке и Кестеле. Фиксированная цена, подтверждение клинера и фотоотчёт.",
      cleaningPage: "Заказ на уборку | Loco Cleaning",
      cleaningPageDescription: "Управление вашим заказом на уборку квартиры в Аланье.",
      rent: "Аренда квартир в Аланье от недели | Loco Rental",
      rentDescription: "Квартиры в Аланье для проживания от недели до нескольких месяцев. Свободные даты и итоговая стоимость видны до бронирования.",
      rentPage: "Квартира в Аланье | Loco Rental",
      rentPageDescription: "Посмотрите фотографии, характеристики, свободные даты и стоимость аренды квартиры в Аланье.",
      rentalPropertyTitle: "{{title}} — квартира в {{area}}, Аланья | Loco Rental",
      rentalPropertyDescription: "{{title}} в районе {{area}}, Аланья: до {{guests}} гостей, площадь {{areaSqm}} м², аренда от {{price}} {{currency}} за сутки. Свободные даты доступны в календаре.",
      transfer: "Трансфер в Аланью из Антальи и Газипаши | Loco Transfer",
      transferDescription: "Закажите трансфер между Аланьей и аэропортами Антальи или Газипаши. Выбор машины, фиксированная цена и подтверждение водителя.",
      transferPage: "Заявка на трансфер | Loco Transfer",
      transferPageDescription: "Управление вашей заявкой на трансфер между Аланьей и аэропортом.",
      account: "Аккаунт | Loco Place",
      accountDescription: "Управление аккаунтом Loco Place и каналами уведомлений.",
      activity: "Мои задачи | Loco Place",
      activityDescription: "История уборок, бронирований квартир и трансферов в вашем аккаунте Loco Place.",
      notifications: "Уведомления | Loco Place",
      notificationsDescription: "Подтверждения, изменения статусов и напоминания по вашим задачам в Loco Place.",
      admin: "Админка | Loco Place",
      adminPage: "Раздел админки | Loco Place",
      privateDescription: "Защищённая страница аккаунта Loco Place.",
      privacyDescription: "Как Loco Place использует и хранит данные клиентов во время пилотного периода.",
      termsDescription: "Условия использования сервисов Loco Place во время пилотного периода.",
    },
  },
  en: {
    app: {
      tagline: "Everyday tasks in Alanya, without searching for providers yourself",
    },
    create: {
      title: "Apartment cleaning in Alanya at a fixed price",
      subtitle: "Choose the area, apartment size and cleaning type. See the price upfront, get the date confirmed by a cleaner, and receive a photo report after completion.",
      details: {
        referralCodeHint: "Enter a code from a friend or partner to receive an available first-order discount.",
      },
      rentalContext: {
        prefilled: "The address and phone are already filled from your Loco Rental booking.",
      },
    },
    cleaning: {
      REGULAR: {
        description: "A maintenance clean to refresh the apartment and restore everyday order.",
      },
      DEEP: {
        description: "A more thorough clean of the kitchen, bathroom, doors and reachable surfaces when the apartment needs extra attention.",
      },
    },
    created: {
      text: "Your order was sent to the cleaning team. We will confirm it in Loco and also notify you in Telegram when connected.",
    },
    catalog: {
      title: "Cleaning, apartment rentals and airport transfers in Alanya",
      subtitle: "Choose what you need, see the price and availability upfront, and let Loco handle the rest — without lengthy searches or extra calls.",
      note: "Book again without starting over — your address, contact details and order history stay in Loco.",
      rent: {
        title: "Rent an apartment",
        text: "Alanya apartments from one week, with availability and pricing shown before booking",
      },
      cleaning: {
        title: "Book cleaning",
        text: "Apartment cleaning at a fixed price, with a photo report after completion",
      },
      transfer: {
        title: "Book a transfer",
        text: "Fixed-price transfers between Alanya and Antalya or Gazipaşa airports",
      },
      home: {
        allActivity: "All tasks · {{count}}",
        actions: {
          RENTAL_TRANSFER_ARRIVAL: {
            title: "Book an airport pickup",
            text: "Your check-in date and apartment address are already filled: {{date}}",
          },
          RENTAL_TRANSFER_CHECKOUT: {
            title: "Book a transfer to the airport",
            text: "Your checkout date and pickup address are already filled: {{date}}",
          },
          RENTAL_CLEANING: {
            title: "Book cleaning before checkout",
            text: "The address is already filled and your personal benefit is available for {{date}}",
          },
        },
      },
    },
    rental: {
      catalog: {
        title: "Apartment rentals in Alanya from one week",
        subtitle: "Choose an apartment, check available dates, and see the full price before booking.",
        emptyTitle: "No apartments are currently available",
        emptyText: "We show only apartments with open dates. Please check the catalog again later.",
      },
      booking: {
        selectDates: "Choose check-in and check-out dates — the full price will appear immediately.",
        selectMonthlyStart: "Choose a start date and length of stay — we will check availability and show the full price.",
        confirm: "Book apartment",
      },
      bookings: {
        subtitle: "All upcoming and completed stays in one place.",
      },
      bookingDetails: {
        cleaningTitle: "Cleaning before checkout",
        cleaningText: "Book cleaning for this apartment without entering everything again: the address and phone are already added.",
        transferTitle: "Transfer for check-in or checkout",
        transferText: "The date and address are already filled — choose the airport, time and vehicle.",
      },
    },
    transfer: {
      title: "Airport transfers to and from Alanya",
      subtitle: "Choose the airport, vehicle, date and time. The price is fixed upfront, and Loco will assign a driver and confirm your ride.",
      unavailableTitle: "Transfers are temporarily unavailable",
      unavailableText: "We are not accepting new requests right now. Please try again later.",
      submit: "Book transfer",
      summary: {
        requested: "No online payment is required. After your request, Loco will assign a driver and tell you when the ride is confirmed.",
      },
      landing: {
        title: "What you get",
        directions: "Rides from the airport to Alanya and back",
        price: "A fixed price shown before you send the request",
        driver: "Loco assigns the driver and confirms the ride",
      },
    },
    account: {
      subtitle: "Connect Telegram and choose where to receive confirmations and order updates.",
      activityText: "All your cleanings, apartment bookings and transfers — current and completed — are kept in one place.",
      nudgeText: "Connect Telegram to receive confirmations, status changes and photo-report updates.",
    },
    customerHub: {
      title: "My tasks",
      subtitle: "Follow current orders, return to past ones and check important updates.",
    },
    activity: {
      subtitle: "All your cleanings, apartment bookings and transfers — current and completed — in one place.",
    },
    notifications: {
      subtitle: "Confirmations, status changes and reminders for your tasks.",
      emptyText: "Confirmations, status changes and useful reminders will appear here.",
    },
    support: {
      customer: {
        subtitle: "Tell us what happened. The order number and details are already attached, so you do not need to repeat them.",
        feedbackText: "Your answer helps us monitor quality and resolve problems faster.",
        openCase: "Report a problem",
      },
    },
    adminRental: {
      notifications: {
        description: "Receive Telegram messages about new Loco Rental bookings and customer cancellations.",
      },
    },
    analytics: {
      campaigns: {
        targetServices: {
          RENTAL: "Loco Rental",
        },
      },
    },
    footer: {
      tagline: "Cleaning, apartment rentals and airport transfers in Alanya — Loco handles the rest.",
    },
    legal: {
      privacy: {
        intro: "This notice explains the main categories of data Loco Place uses to provide Cleaning, Rental and Transfer services during the pilot.",
      },
      terms: {
        sections: {
          first: {
            text: "Loco Place lets you book cleaning, browse and reserve apartments, and request airport transfers wherever the relevant service is available. Some functionality may still be marked as in test during the pilot.",
          },
        },
      },
    },
    titles: {
      fallback: "Loco Place — services in Alanya",
      description: "Loco Place helps you book apartment cleaning, rent a home and arrange airport transfers in Alanya.",
      home: "Cleaning, Rentals & Airport Transfers in Alanya | Loco Place",
      homeDescription: "Book apartment cleaning, rent a home or arrange a transfer between Alanya and the airports. See prices and availability before you order.",
      cleaning: "Apartment Cleaning in Alanya | Loco Cleaning",
      cleaningDescription: "Book regular or deep apartment cleaning in Mahmutlar, Kargıcak or Kestel. Fixed pricing, cleaner confirmation and a completion photo report.",
      cleaningPage: "Cleaning Order | Loco Cleaning",
      cleaningPageDescription: "Manage your apartment-cleaning order in Alanya.",
      rent: "Apartment Rentals in Alanya from One Week | Loco Rental",
      rentDescription: "Apartments in Alanya for stays from one week to several months. Check available dates and see the full price before booking.",
      rentPage: "Apartment in Alanya | Loco Rental",
      rentPageDescription: "View photos, details, available dates and the rental price for an apartment in Alanya.",
      rentalPropertyTitle: "{{title}} — apartment in {{area}}, Alanya | Loco Rental",
      rentalPropertyDescription: "{{title}} in {{area}}, Alanya: up to {{guests}} guests, {{areaSqm}} m², from {{price}} {{currency}} per day. Available dates are shown in the calendar.",
      transfer: "Alanya Airport Transfers | Loco Transfer",
      transferDescription: "Book transfers between Alanya and Antalya or Gazipaşa airports. Choose a vehicle, see the fixed price and receive driver confirmation.",
      transferPage: "Transfer Request | Loco Transfer",
      transferPageDescription: "Manage your transfer request between Alanya and the airport.",
      account: "Account | Loco Place",
      accountDescription: "Manage your Loco Place account and notification channels.",
      activity: "My Tasks | Loco Place",
      activityDescription: "Your cleaning orders, apartment bookings and transfers in Loco Place.",
      notifications: "Notifications | Loco Place",
      notificationsDescription: "Confirmations, status changes and reminders for your Loco Place tasks.",
      admin: "Admin | Loco Place",
      adminPage: "Admin Section | Loco Place",
      privateDescription: "A protected Loco Place account page.",
      privacyDescription: "How Loco Place uses and retains customer data during the pilot period.",
      termsDescription: "Terms for using Loco Place services during the pilot period.",
    },
  },
};

function normalizeLanguage(language: string | undefined): AppLanguage | null {
  const normalized = language?.split("-")[0] as AppLanguage | undefined;
  return normalized && SUPPORTED_LANGUAGES.includes(normalized) ? normalized : null;
}

function apply(language: string | undefined): void {
  const normalized = normalizeLanguage(language);
  if (!normalized || !i18n.hasResourceBundle(normalized, "translation")) return;
  i18n.addResourceBundle(normalized, "translation", publicCopy[normalized], true, true);
}

export function applyPublicCopyOverrides(): void {
  for (const language of SUPPORTED_LANGUAGES) {
    apply(language);
  }
  i18n.on("languageChanged", apply);
}
