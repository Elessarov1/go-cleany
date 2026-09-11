import type { AccountDeletionChallenge, AccountIdentities, AccountLinkInitiated, CustomerActivity, CustomerHome, CustomerNotificationPage, CustomerProfile } from "../domain/customer";

export interface CustomerApi {
  captureTelegramAcquisition(publicCode: string): Promise<{ targetPath: string }>;
  getCurrentProfile(): Promise<CustomerProfile>;
  getActivity(): Promise<CustomerActivity>;
  getHome(): Promise<CustomerHome>;
  getAccountIdentities(): Promise<AccountIdentities>;
  initiateTelegramLink(): Promise<AccountLinkInitiated>;
  confirmTelegramLink(attemptId: string): Promise<AccountIdentities>;
  createAccountDeletionRequest(): Promise<AccountDeletionChallenge>;
  confirmAccountDeletion(challengeId: string, telegramInitData?: string | null): Promise<void>;
  getNotifications(cursor?: string | null, size?: number): Promise<CustomerNotificationPage>;
  getNotificationUnreadCount(): Promise<number>;
  markNotificationRead(notificationId: number): Promise<void>;
  markAllNotificationsRead(): Promise<void>;
}
