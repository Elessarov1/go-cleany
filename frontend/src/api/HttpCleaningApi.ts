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
  CleaningRepeatReminder,
  CleaningRepeatReminderSelection,
  CleaningRepeatPrefill,
  CreateCleaningOrderRequest,
  ReferralSummary,
} from "../domain/order";
import {
  CleaningApiError,
  type CleaningApi,
} from "./CleaningApi";
import { generatedWire, HttpApiClient } from "./HttpApiClient";
import {
  CleaningApi as GeneratedCleaningApi,
  CleaningConfigurationToJSON,
  CleaningOrderQuoteRequestFromJSON,
  CleaningOrderQuoteToJSON,
  CleaningOrderToJSON,
  CleaningRepeatPrefillToJSON,
  CleaningRepeatReminderToJSON,
  Configuration,
  CreateCleaningOrderRequestFromJSON,
  ReferralSummaryToJSON,
} from "@locoplace/api-client";

export class HttpCleaningApi implements CleaningApi {
  private readonly generated: GeneratedCleaningApi;

  constructor(private readonly client: HttpApiClient) {
    this.generated = new GeneratedCleaningApi(new Configuration({
      basePath: client.basePath,
      fetchApi: client.generatedFetch,
    }));
  }

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
    return this.client.generated(this.generated.getCleaningConfiguration())
      .then((value) => generatedWire<CleaningConfiguration>(CleaningConfigurationToJSON(value)));
  }

  quoteOrder(request: CleaningOrderQuoteRequest): Promise<CleaningOrderQuote> {
    return this.client.generated(this.generated.quoteCleaningOrder({
      cleaningOrderQuoteRequest: CleaningOrderQuoteRequestFromJSON(request),
    })).then((value) => generatedWire<CleaningOrderQuote>(CleaningOrderQuoteToJSON(value)));
  }

  createOrder(request: CreateCleaningOrderRequest): Promise<CleaningOrder> {
    return this.client.generated(this.generated.createCleaningOrder({
      idempotencyKey: crypto.randomUUID(),
      createCleaningOrderRequest: CreateCleaningOrderRequestFromJSON(request),
    })).then((value) => generatedWire<CleaningOrder>(CleaningOrderToJSON(value)));
  }

  getReferralSummary(): Promise<ReferralSummary> {
    return this.client.generated(this.generated.getCleaningReferralSummary())
      .then((value) => generatedWire<ReferralSummary>(ReferralSummaryToJSON(value)));
  }

  async getOrders(): Promise<CleaningOrder[]> {
    const orders: CleaningOrder[] = [];
    let cursor: string | undefined;
    do {
      const page = await this.client.generated(this.generated.getCleaningOrders({ cursor, size: 50 }));
      orders.push(...page.items.map((value) => generatedWire<CleaningOrder>(CleaningOrderToJSON(value))));
      cursor = page.hasMore ? page.nextCursor ?? undefined : undefined;
    } while (cursor);
    return orders;
  }

  getOrder(id: number): Promise<CleaningOrder> {
    return this.client.generated(this.generated.getCleaningOrder({ orderId: id }))
      .then((value) => generatedWire<CleaningOrder>(CleaningOrderToJSON(value)));
  }

  async recordRepeatShown(id: number): Promise<void> {
    await this.client.generated(this.generated.recordCleaningRepeatShown({ orderId: id }));
  }

  getRepeatPrefill(id: number): Promise<CleaningRepeatPrefill> {
    return this.client.generated(this.generated.getCleaningRepeatPrefill({ orderId: id }))
      .then((value) => generatedWire<CleaningRepeatPrefill>(CleaningRepeatPrefillToJSON(value)));
  }

  getRepeatReminder(id: number): Promise<CleaningRepeatReminder> {
    return this.client.generated(this.generated.getCleaningRepeatReminder({ orderId: id }))
      .then((value) => generatedWire<CleaningRepeatReminder>(CleaningRepeatReminderToJSON(value)));
  }

  updateRepeatReminder(
    id: number,
    selection: CleaningRepeatReminderSelection,
  ): Promise<CleaningRepeatReminder> {
    return this.client.generated(this.generated.updateCleaningRepeatReminder({
      orderId: id,
      cleaningRepeatReminderRequest: { selection },
    })).then((value) => generatedWire<CleaningRepeatReminder>(CleaningRepeatReminderToJSON(value)));
  }

  getReportPhoto(orderId: number, mediaId: number): Promise<Blob> {
    return this.client.generated(this.generated.getCleaningReportPhoto({ orderId, mediaId }));
  }

  cancelOrder(id: number): Promise<CleaningOrder> {
    return this.client.generated(this.generated.cancelCleaningOrder({ orderId: id }))
      .then((value) => generatedWire<CleaningOrder>(CleaningOrderToJSON(value)));
  }

  private async request<T>(path: string, init: RequestInit = {}): Promise<T> {
    return this.client.request(path, init);
  }

  private requestBlob(path: string): Promise<Blob> {
    return this.client.requestBlob(path);
  }
}
