import type { Platform } from "../platform/Platform";
import type { CleaningConfiguration } from "../domain/configuration";
import type { AdminDashboard, AdminOrderDetails } from "../domain/admin";
import type {
  CleaningOrder,
  CreateCleaningOrderRequest,
} from "../domain/order";
import {
  CleaningApiError,
  type CleaningApi,
} from "./CleaningApi";

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

  getConfiguration(): Promise<CleaningConfiguration> {
    return this.request("/api/v1/config");
  }

  createOrder(request: CreateCleaningOrderRequest): Promise<CleaningOrder> {
    return this.request("/api/v1/orders", {
      method: "POST",
      body: JSON.stringify(request),
    });
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
      throw new CleaningApiError(
        `Request failed with status ${response.status}`,
        response.status,
      );
    }

    return (await response.json()) as T;
  }
}
