import type { AccountIdentities, AccountLinkInitiated, CustomerActivity, CustomerHome, CustomerNotificationPage, CustomerProfile } from "../domain/customer";

export interface CustomerApi {
  captureTelegramAcquisition(publicCode: string): Promise<{ targetPath: string }>;
  getCurrentProfile(): Promise<CustomerProfile>;
  getActivity(): Promise<CustomerActivity>;
  getHome(): Promise<CustomerHome>;
  getAccountIdentities(): Promise<AccountIdentities>;
  initiateTelegramLink(): Promise<AccountLinkInitiated>;
  confirmTelegramLink(attemptId: string): Promise<AccountIdentities>;
  getNotifications(cursor?: string | null, size?: number): Promise<CustomerNotificationPage>;
  getNotificationUnreadCount(): Promise<number>;
  markNotificationRead(notificationId: number): Promise<void>;
  markAllNotificationsRead(): Promise<void>;
}
