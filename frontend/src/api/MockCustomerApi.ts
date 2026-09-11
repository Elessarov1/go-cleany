import type { AccountDeletionChallenge, AccountIdentities, AccountLinkInitiated, CustomerActivity, CustomerActivityItem, CustomerHome, CustomerHomePrimaryAction, CustomerHomeRepeatOpportunity, CustomerNotification, CustomerNotificationPage, CustomerProfile } from "../domain/customer";
import { ApiError } from "./ApiError";
import type { CustomerApi } from "./CustomerApi";

export class MockCustomerApi implements CustomerApi {
  async captureTelegramAcquisition(): Promise<{ targetPath: string }> {
    return { targetPath: "/" };
  }

  private linked = false;
  private deletionChallenge: AccountDeletionChallenge | null = null;
  private notifications: CustomerNotification[] = [
    { id: 3, type: "SUPPORT_CASE_CREATED", action: { type: "OPEN_SUPPORT_CASE", caseId: 701 }, createdAt: new Date().toISOString(), readAt: null },
    { id: 2, type: "RENTAL_BOOKING_CONFIRMED", action: { type: "OPEN_TRANSACTION", service: "RENTAL", entityId: 2 }, createdAt: new Date().toISOString(), readAt: null },
    { id: 1, type: "CLEANING_ORDER_COMPLETED", action: { type: "OPEN_TRANSACTION", service: "CLEANING", entityId: 1 }, createdAt: new Date(Date.now() - 86_400_000).toISOString(), readAt: null },
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
          money: { amount: "1800", currency: "TRY" }, action: { type: "OPEN_TRANSACTION", service: "TRANSFER", entityId: 7 },
        },
        {
          service: "RENTAL", entityId: 4, status: "CONFIRMED",
          titleRu: "Квартира у моря", titleEn: "Apartment by the sea",
          subtitleRu: "Махмутлар", subtitleEn: "Mahmutlar", scheduledDate: "2026-09-05",
          scheduledEndDate: "2026-09-12", scheduledTime: null, occurredAt: new Date().toISOString(),
          money: { amount: "14000", currency: "TRY" }, action: { type: "OPEN_TRANSACTION", service: "RENTAL", entityId: 4 },
        },
      ],
      history: [
        {
          service: "CLEANING", entityId: 12, status: "COMPLETED",
          titleRu: "Уборка квартиры", titleEn: "Apartment cleaning",
          subtitleRu: "Кестель · Hrm Residence", subtitleEn: "Kestel · Hrm Residence",
          scheduledDate: "2026-08-27", scheduledEndDate: null, scheduledTime: null,
          occurredAt: "2026-08-27T12:20:00Z", money: { amount: "6000", currency: "TRY" },
          action: { type: "OPEN_TRANSACTION", service: "CLEANING", entityId: 12 },
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

    const activeTransaction: CustomerActivityItem = {
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
      money: { amount: "14000", currency: "TRY" },
      action: { type: "OPEN_TRANSACTION", service: "RENTAL", entityId: 4 },
    };
    const cleaningRepeat: CustomerHomeRepeatOpportunity = {
      service: "CLEANING" as const,
      sourceEntityId: 12,
      sourceCompletedAt: "2026-08-27T12:20:00Z",
      action: { type: "REPEAT_CLEANING", sourceOrderId: 12 },
    };
    const transferRepeat: CustomerHomeRepeatOpportunity = {
      service: "TRANSFER" as const,
      sourceEntityId: 7,
      sourceCompletedAt: "2026-08-29T08:30:00Z",
      action: { type: "REPEAT_TRANSFER", sourceBookingId: 7 },
    };
    const transferAction: CustomerHomePrimaryAction = {
      type: "RENTAL_TRANSFER_CHECKOUT" as const,
      sourceService: "RENTAL" as const,
      sourceEntityId: 4,
      targetService: "TRANSFER" as const,
      relevantDate: "2026-09-12",
      eligibleFrom: null,
      expiresOn: null,
      action: { type: "START_RENTAL_TRANSFER", rentalBookingId: 4, context: "CHECKOUT" },
      benefit: { type: "RENTAL_FIRST_TRANSFER" as const, discountRate: 0.1 },
    };
    const cleaningAction: CustomerHomePrimaryAction = {
      type: "RENTAL_CLEANING" as const,
      sourceService: "RENTAL" as const,
      sourceEntityId: 4,
      targetService: "CLEANING" as const,
      relevantDate: "2026-09-10",
      eligibleFrom: "2026-09-09",
      expiresOn: "2026-09-12",
      action: { type: "START_RENTAL_CLEANING", rentalBookingId: 4 },
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
      { identityId: 1, provider: "GOOGLE", issuer: "https://accounts.google.com", linked: true, username: null, writeAccessAllowed: false },
      { identityId: this.linked ? 2 : null, provider: "TELEGRAM", issuer: "https://telegram.org", linked: this.linked, username: this.linked ? "browser_preview" : null, writeAccessAllowed: this.linked },
      { identityId: null, provider: "APPLE", issuer: "https://appleid.apple.com", linked: false, username: null, writeAccessAllowed: false },
    ] };
  }

  async initiateTelegramLink(): Promise<AccountLinkInitiated> {
    return { id: crypto.randomUUID(), deepLink: "https://t.me/example?start=link_preview", expiresAt: new Date(Date.now() + 600_000).toISOString() };
  }

  async confirmTelegramLink(): Promise<AccountIdentities> {
    this.linked = true;
    return this.getAccountIdentities();
  }

  async createAccountDeletionRequest(): Promise<AccountDeletionChallenge> {
    const scenario = new URLSearchParams(window.location.search).get("scenario")?.toUpperCase();
    if (scenario === "ACCOUNT_DELETE_ADMIN") {
      throw new ApiError("Administrator accounts cannot be deleted", 409, "admin_account_self_deletion_forbidden");
    }
    this.deletionChallenge = {
      id: crypto.randomUUID(),
      provider: scenario === "ACCOUNT_DELETE_TMA" ? "TELEGRAM" : "GOOGLE",
      nonce: null,
      expiresAt: new Date(Date.now() + 300_000).toISOString(),
    };
    return this.deletionChallenge;
  }

  async confirmAccountDeletion(challengeId: string): Promise<void> {
    const scenario = new URLSearchParams(window.location.search).get("scenario")?.toUpperCase();
    if (!this.deletionChallenge || this.deletionChallenge.id !== challengeId) {
      throw new ApiError("Challenge expired", 409, "auth_challenge_expired");
    }
    if (scenario === "ACCOUNT_DELETE_BLOCKED") {
      throw new ApiError("Deletion is blocked", 409, "account_deletion_blocked_by_active_operation");
    }
    if (scenario === "ACCOUNT_DELETE_EXPIRED") {
      throw new ApiError("Proof expired", 401, "auth_challenge_expired");
    }
    if (scenario === "ACCOUNT_DELETE_NETWORK") {
      throw new TypeError("Network request failed");
    }
    this.deletionChallenge = null;
  }

  async getNotifications(cursor: string | null = null, size = 20): Promise<CustomerNotificationPage> {
    const start = cursor ? Number(cursor) : 0;
    const items = this.notifications.slice(start, start + size);
    const next = start + items.length;
    return { items, nextCursor: next < this.notifications.length ? String(next) : null, hasMore: next < this.notifications.length };
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
