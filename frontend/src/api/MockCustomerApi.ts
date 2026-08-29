import type { AccountIdentities, AccountLinkInitiated, CustomerNotification, CustomerNotificationPage, CustomerProfile } from "../domain/customer";
import type { CustomerApi } from "./CustomerApi";

export class MockCustomerApi implements CustomerApi {
  async captureTelegramAcquisition(): Promise<{ targetPath: string }> {
    return { targetPath: "/" };
  }

  private linked = false;
  private notifications: CustomerNotification[] = [
    { id: 2, type: "RENTAL_BOOKING_CONFIRMED", targetPath: "/rent/bookings/2", createdAt: new Date().toISOString(), readAt: null },
    { id: 1, type: "CLEANING_ORDER_COMPLETED", targetPath: "/cleaning/orders/1", createdAt: new Date(Date.now() - 86_400_000).toISOString(), readAt: null },
  ];
  async getCurrentProfile(): Promise<CustomerProfile> {
    await new Promise((resolve) => window.setTimeout(resolve, 120));
    return { phone: "+90 555 123 45 67" };
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
