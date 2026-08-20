import type { Platform } from "../platform/Platform";
import type { CleaningConfiguration } from "../domain/configuration";
import type { CustomerProfile } from "../domain/customer";
import type {
  AdminDashboard,
  AdminOrderDetails,
  AdminReferralOverview,
  PartnerPayout,
  ReferralPartner,
} from "../domain/admin";
import type {
  CleaningOrder,
  CleaningOrderQuote,
  CleaningOrderQuoteRequest,
  CreateCleaningOrderRequest,
  ReferralSummary,
} from "../domain/order";
import {
  CleaningApiError,
  type CleaningApi,
} from "./CleaningApi";

interface ApiErrorResponse {
  code?: string;
  message?: string;
  fieldErrors?: Record<string, string> | null;
}

export class HttpCleaningApi implements CleaningApi {
  constructor(
    private readonly baseUrl: string,
    private readonly platform: Platform,
  ) {}

  async hasAdminAccess(): Promise<boolean> {
    try {
      await this.request<{ authorized: boolean }>("/api/v1/admin/access");
      return true;
    } catch (error) {
      if (error instanceof CleaningApiError && error.status === 403) {
        return false;
      }
      throw error;
    }
  }

  getAdminDashboard(limit = 100): Promise<AdminDashboard> {
    return this.request(`/api/v1/admin/dashboard?limit=${limit}`);
  }

  getAdminOrder(id: number): Promise<AdminOrderDetails> {
    return this.request(`/api/v1/admin/orders/${id}`);
  }

  getAdminIssuePhoto(orderId: number, photoId: number): Promise<Blob> {
    return this.requestBlob(`/api/v1/admin/orders/${orderId}/issues/photos/${photoId}`);
  }

  resolveAdminIssue(orderId: number, resolutionComment: string): Promise<AdminOrderDetails> {
    return this.request(`/api/v1/admin/orders/${orderId}/issues/resolve`, {
      method: "POST",
      body: JSON.stringify({ resolutionComment }),
    });
  }

  getAdminReferralOverview(): Promise<AdminReferralOverview> {
    return this.request("/api/v1/admin/referrals");
  }

  createReferralPartner(name: string): Promise<ReferralPartner> {
    return this.request("/api/v1/admin/referrals/partners", {
      method: "POST",
      body: JSON.stringify({ name }),
    });
  }

  markPartnerPayoutPaid(id: number): Promise<PartnerPayout> {
    return this.request(`/api/v1/admin/referrals/payouts/${id}/paid`, {
      method: "POST",
    });
  }

  getConfiguration(): Promise<CleaningConfiguration> {
    return this.request("/api/v1/config");
  }

  getCurrentCustomerProfile(): Promise<CustomerProfile> {
    return this.request("/api/v1/customers/me");
  }

  quoteOrder(request: CleaningOrderQuoteRequest): Promise<CleaningOrderQuote> {
    return this.request("/api/v1/orders/quote", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  createOrder(request: CreateCleaningOrderRequest): Promise<CleaningOrder> {
    return this.request("/api/v1/orders", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  getReferralSummary(): Promise<ReferralSummary> {
    return this.request("/api/v1/referrals/me");
  }

  getOrders(): Promise<CleaningOrder[]> {
    return this.request("/api/v1/orders");
  }

  getOrder(id: number): Promise<CleaningOrder> {
    return this.request(`/api/v1/orders/${id}`);
  }

  cancelOrder(id: number): Promise<CleaningOrder> {
    return this.request(`/api/v1/orders/${id}/cancel`, { method: "POST" });
  }

  private async request<T>(path: string, init: RequestInit = {}): Promise<T> {
    const headers = new Headers(init.headers);
    headers.set("Accept", "application/json");

    if (init.body) {
      headers.set("Content-Type", "application/json");
    }

    const authData = this.platform.getAuthData();
    if (authData) {
      headers.set("Authorization", `tma ${authData}`);
    }

    const response = await fetch(`${this.baseUrl}${path}`, {
      ...init,
      headers,
    });

    if (!response.ok) {
      let apiError: ApiErrorResponse | null = null;
      try {
        apiError = (await response.json()) as ApiErrorResponse;
      } catch {
        // Preserve a useful status-only error when the response is not JSON.
      }
      throw new CleaningApiError(
        apiError?.message ?? `Request failed with status ${response.status}`,
        response.status,
        apiError?.code,
        apiError?.fieldErrors ?? {},
      );
    }

    return (await response.json()) as T;
  }

  private async requestBlob(path: string): Promise<Blob> {
    const headers = new Headers({ Accept: "image/jpeg, image/png" });
    const authData = this.platform.getAuthData();
    if (authData) {
      headers.set("Authorization", `tma ${authData}`);
    }

    const response = await fetch(`${this.baseUrl}${path}`, { headers });
    if (!response.ok) {
      throw new CleaningApiError(
        `Request failed with status ${response.status}`,
        response.status,
      );
    }
    return response.blob();
  }
}
