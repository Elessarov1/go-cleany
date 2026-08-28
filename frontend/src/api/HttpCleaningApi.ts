import type { CleaningConfiguration } from "../domain/configuration";
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
import { HttpApiClient } from "./HttpApiClient";

export class HttpCleaningApi implements CleaningApi {
  constructor(private readonly client: HttpApiClient) {}

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
    return this.request("/api/v1/cleaning/configuration");
  }

  quoteOrder(request: CleaningOrderQuoteRequest): Promise<CleaningOrderQuote> {
    return this.request("/api/v1/cleaning/orders/quote", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  createOrder(request: CreateCleaningOrderRequest): Promise<CleaningOrder> {
    return this.request("/api/v1/cleaning/orders", {
      method: "POST",
      body: JSON.stringify(request),
    });
  }

  getReferralSummary(): Promise<ReferralSummary> {
    return this.request("/api/v1/referrals/me");
  }

  getOrders(): Promise<CleaningOrder[]> {
    return this.request("/api/v1/cleaning/orders");
  }

  getOrder(id: number): Promise<CleaningOrder> {
    return this.request(`/api/v1/cleaning/orders/${id}`);
  }

  getReportPhoto(orderId: number, mediaId: number): Promise<Blob> {
    return this.requestBlob(`/api/v1/cleaning/orders/${orderId}/report/photos/${mediaId}`);
  }

  cancelOrder(id: number): Promise<CleaningOrder> {
    return this.request(`/api/v1/cleaning/orders/${id}/cancel`, { method: "POST" });
  }

  private async request<T>(path: string, init: RequestInit = {}): Promise<T> {
    return this.client.request(path, init);
  }

  private requestBlob(path: string): Promise<Blob> {
    return this.client.requestBlob(path);
  }
}
