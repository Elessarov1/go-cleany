import type { AccountIdentities, AccountLinkInitiated, CustomerActivity, CustomerHome, CustomerNotificationPage, CustomerProfile } from "../domain/customer";

export interface CustomerApi {
  captureTelegramAcquisition(publicCode: string): Promise<{ targetPath: string }>;
  getCurrentProfile(): Promise<CustomerProfile>;
  getActivity(): Promise<CustomerActivity>;
  getHome(): Promise<CustomerHome>;
  getAccountIdentities(): Promise<AccountIdentities>;
  initiateTelegramLink(): Promise<AccountLinkInitiated>;
  confirmTelegramLink(token: string): Promise<AccountIdentities>;
  getNotifications(page?: number, size?: number): Promise<CustomerNotificationPage>;
  getNotificationUnreadCount(): Promise<number>;
  markNotificationRead(notificationId: number): Promise<void>;
  markAllNotificationsRead(): Promise<void>;
}
