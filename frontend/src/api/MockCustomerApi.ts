import type { AccountIdentities, AccountLinkInitiated, CustomerActivity, CustomerHome, CustomerNotification, CustomerNotificationPage, CustomerProfile } from "../domain/customer";
import type { CustomerApi } from "./CustomerApi";

export class MockCustomerApi implements CustomerApi {
  async captureTelegramAcquisition(): Promise<{ targetPath: string }> {
    return { targetPath: "/" };
  }

  private linked = false;
  private notifications: CustomerNotification[] = [
    { id: 3, type: "SUPPORT_CASE_CREATED", targetPath: "/admin/support/cases/701", createdAt: new Date().toISOString(), readAt: null },
    { id: 2, type: "RENTAL_BOOKING_CONFIRMED", targetPath: "/rent/bookings/2", createdAt: new Date().toISOString(), readAt: null },
    { id: 1, type: "CLEANING_ORDER_COMPLETED", targetPath: "/cleaning/orders/1", createdAt: new Date(Date.now() - 86_400_000).toISOString(), readAt: null },
  ];
  async getCurrentProfile(): Promise<CustomerProfile> {
    await new Promise((resolve) => window.setTimeout(resolve, 120));
    return { phone: "+90 555 123 45 67" };
  }

  async getActivity(): Promise<CustomerActivity> {
    if (new URLSearchParams(window.location.search).get("scenario") === "empty") {
      return { activeAndUpcoming: [], history: [] };
    }
    return {
      activeAndUpcoming: [
        {
          service: "TRANSFER", entityId: 7, status: "REQUESTED",
          titleRu: "Трансфер в аэропорт GZP", titleEn: "Transfer to GZP airport",
          subtitleRu: "Седан", subtitleEn: "Sedan", scheduledDate: "2026-09-02",
          scheduledEndDate: null, scheduledTime: "08:30:00", occurredAt: new Date().toISOString(),
          amount: 1800, currency: "TRY", targetPath: "/transfer/bookings/7",
        },
        {
          service: "RENTAL", entityId: 4, status: "CONFIRMED",
          titleRu: "Квартира у моря", titleEn: "Apartment by the sea",
          subtitleRu: "Махмутлар", subtitleEn: "Mahmutlar", scheduledDate: "2026-09-05",
          scheduledEndDate: "2026-09-12", scheduledTime: null, occurredAt: new Date().toISOString(),
          amount: 14000, currency: "TRY", targetPath: "/rent/bookings/4",
        },
      ],
      history: [
        {
          service: "CLEANING", entityId: 12, status: "COMPLETED",
          titleRu: "Уборка квартиры", titleEn: "Apartment cleaning",
          subtitleRu: "Кестель · Hrm Residence", subtitleEn: "Kestel · Hrm Residence",
          scheduledDate: "2026-08-27", scheduledEndDate: null, scheduledTime: null,
          occurredAt: "2026-08-27T12:20:00Z", amount: 6000, currency: "TRY",
          targetPath: "/cleaning/orders/12",
        },
      ],
    };
  }

  async getHome(): Promise<CustomerHome> {
    const scenario = new URLSearchParams(window.location.search).get("scenario")?.toUpperCase();
    if (scenario === "HOME_ERROR") {
      throw new Error("Customer home preview failure");
    }
    if (scenario === "HOME_NEW") {
      return {
        hasActivity: false,
        activeTransaction: null,
        activeTransactionCount: 0,
        primaryAction: null,
        repeatOpportunity: null,
      };
    }

    const activeTransaction = {
      service: "RENTAL" as const,
      entityId: 4,
      status: "CONFIRMED",
      titleRu: "Квартира у моря",
      titleEn: "Apartment by the sea",
      subtitleRu: "Махмутлар",
      subtitleEn: "Mahmutlar",
      scheduledDate: "2026-09-05",
      scheduledEndDate: "2026-09-12",
      scheduledTime: null,
      occurredAt: new Date().toISOString(),
      amount: 14000,
      currency: "TRY",
      targetPath: "/rent/bookings/4",
    };
    const cleaningRepeat = {
      service: "CLEANING" as const,
      sourceEntityId: 12,
      sourceCompletedAt: "2026-08-27T12:20:00Z",
      targetPath: "/cleaning?repeatFrom=12",
    };
    const transferRepeat = {
      service: "TRANSFER" as const,
      sourceEntityId: 7,
      sourceCompletedAt: "2026-08-29T08:30:00Z",
      targetPath: "/transfer?repeatFrom=7",
    };
    const transferAction = {
      type: "RENTAL_TRANSFER_CHECKOUT" as const,
      sourceService: "RENTAL" as const,
      sourceEntityId: 4,
      targetService: "TRANSFER" as const,
      relevantDate: "2026-09-12",
      eligibleFrom: null,
      expiresOn: null,
      targetPath: "/transfer?rentalBooking=4&rentalContext=CHECKOUT",
      benefit: { type: "RENTAL_FIRST_TRANSFER" as const, discountRate: 0.1 },
    };
    const cleaningAction = {
      type: "RENTAL_CLEANING" as const,
      sourceService: "RENTAL" as const,
      sourceEntityId: 4,
      targetService: "CLEANING" as const,
      relevantDate: "2026-09-10",
      eligibleFrom: "2026-09-09",
      expiresOn: "2026-09-12",
      targetPath: "/cleaning?rentalBooking=4&promo=RC23456789",
      benefit: null,
    };

    if (scenario === "HOME_REPEAT") {
      return {
        hasActivity: true,
        activeTransaction: null,
        activeTransactionCount: 0,
        primaryAction: null,
        repeatOpportunity: transferRepeat,
      };
    }
    if (scenario === "HOME_UNAVAILABLE") {
      return {
        hasActivity: true,
        activeTransaction,
        activeTransactionCount: 1,
        primaryAction: null,
        repeatOpportunity: null,
      };
    }
    return {
      hasActivity: true,
      activeTransaction,
      activeTransactionCount: scenario === "HOME_MULTI_ACTIVE" ? 3 : 1,
      primaryAction: scenario === "HOME_RENTAL_CLEANING" ? cleaningAction : transferAction,
      repeatOpportunity: scenario === "HOME_RENTAL_CLEANING" ? transferRepeat : cleaningRepeat,
    };
  }

  async getAccountIdentities(): Promise<AccountIdentities> {
    return { identities: [
      { provider: "GOOGLE", linked: true, username: null, writeAccessAllowed: false },
      { provider: "TELEGRAM", linked: this.linked, username: this.linked ? "browser_preview" : null, writeAccessAllowed: this.linked },
    ] };
  }

  async initiateTelegramLink(): Promise<AccountLinkInitiated> {
    return { deepLink: "https://t.me/example/app?startapp=preview", expiresAt: new Date(Date.now() + 600_000).toISOString() };
  }

  async confirmTelegramLink(): Promise<AccountIdentities> {
    this.linked = true;
    return this.getAccountIdentities();
  }

  async getNotifications(page = 0, size = 20): Promise<CustomerNotificationPage> {
    const content = this.notifications.slice(page * size, (page + 1) * size);
    return { content, page, size, totalElements: this.notifications.length, totalPages: Math.ceil(this.notifications.length / size) };
  }

  async getNotificationUnreadCount(): Promise<number> {
    return this.notifications.filter((notification) => notification.readAt === null).length;
  }

  async markNotificationRead(notificationId: number): Promise<void> {
    this.notifications = this.notifications.map((notification) => notification.id === notificationId
      ? { ...notification, readAt: notification.readAt ?? new Date().toISOString() }
      : notification);
  }

  async markAllNotificationsRead(): Promise<void> {
    const now = new Date().toISOString();
    this.notifications = this.notifications.map((notification) => ({ ...notification, readAt: notification.readAt ?? now }));
  }
}
