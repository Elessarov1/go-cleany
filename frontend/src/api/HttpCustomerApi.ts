import type { AccountIdentities, AccountLinkInitiated, CustomerActivity, CustomerNotificationPage, CustomerProfile } from "../domain/customer";
import type { CustomerApi } from "./CustomerApi";
import { HttpApiClient } from "./HttpApiClient";

export class HttpCustomerApi implements CustomerApi {
  constructor(private readonly client: HttpApiClient) {}

  captureTelegramAcquisition(publicCode: string): Promise<{ targetPath: string }> {
    return this.client.request("/api/v1/acquisition/telegram", {
      method: "POST",
      body: JSON.stringify({ publicCode }),
    });
  }

  getCurrentProfile(): Promise<CustomerProfile> {
    return this.client.request("/api/v1/customers/me");
  }

  getActivity(): Promise<CustomerActivity> {
    return this.client.request("/api/v1/account/activity");
  }

  getAccountIdentities(): Promise<AccountIdentities> {
    return this.client.request("/api/v1/account/identities");
  }

  initiateTelegramLink(): Promise<AccountLinkInitiated> {
    return this.client.request("/api/v1/account/link/telegram", { method: "POST" });
  }

  confirmTelegramLink(token: string): Promise<AccountIdentities> {
    return this.client.request("/api/v1/account/link/telegram/confirm", {
      method: "POST",
      body: JSON.stringify({ token }),
    });
  }

  getNotifications(page = 0, size = 20): Promise<CustomerNotificationPage> {
    return this.client.request(`/api/v1/account/notifications?page=${page}&size=${size}`);
  }

  async getNotificationUnreadCount(): Promise<number> {
    const response = await this.client.request<{ unreadCount: number }>("/api/v1/account/notifications/unread-count");
    return response.unreadCount;
  }

  markNotificationRead(notificationId: number): Promise<void> {
    return this.client.request(`/api/v1/account/notifications/${notificationId}/read`, { method: "POST" });
  }

  markAllNotificationsRead(): Promise<void> {
    return this.client.request("/api/v1/account/notifications/read-all", { method: "POST" });
  }
}
