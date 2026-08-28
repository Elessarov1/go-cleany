import type { AccountIdentities, AccountLinkInitiated, CustomerNotificationPage, CustomerProfile } from "../domain/customer";

export interface CustomerApi {
  getCurrentProfile(): Promise<CustomerProfile>;
  getAccountIdentities(): Promise<AccountIdentities>;
  initiateTelegramLink(): Promise<AccountLinkInitiated>;
  confirmTelegramLink(token: string): Promise<AccountIdentities>;
  getNotifications(page?: number, size?: number): Promise<CustomerNotificationPage>;
  getNotificationUnreadCount(): Promise<number>;
  markNotificationRead(notificationId: number): Promise<void>;
  markAllNotificationsRead(): Promise<void>;
}
