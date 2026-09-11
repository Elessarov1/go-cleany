import type { Platform } from "../platform/Platform";
import { ApiError } from "./ApiError";
import { ResponseError } from "@locoplace/api-client";

interface ApiErrorResponse {
  code?: string;
  message?: string;
  fieldErrors?: Record<string, string> | null;
}

export class HttpApiClient {
  private csrfToken: Promise<{ headerName: string; token: string }> | null = null;

  constructor(
    private readonly baseUrl: string,
    private readonly platform: Platform,
  ) {}

  get basePath(): string {
    return this.baseUrl;
  }

  resolveUrl(path: string): string {
    return /^(https?:)?\/\//.test(path) ? path : `${this.baseUrl}${path}`;
  }

  async request<T>(path: string, init: RequestInit = {}): Promise<T> {
    const headers = new Headers(init.headers);
    headers.set("Accept", "application/json");
    if (init.body && !(init.body instanceof FormData)) headers.set("Content-Type", "application/json");
    const telegramAuthenticated = this.addTelegramAuthorization(headers);
    if (isStateChanging(init.method) && !telegramAuthenticated) {
      const csrf = await this.getCsrfToken();
      headers.set(csrf.headerName, csrf.token);
    }

    const response = await fetch(`${this.baseUrl}${path}`, {
      ...init,
      headers,
      credentials: "include",
    });
    if (!response.ok) {
      let apiError: ApiErrorResponse | null = null;
      try {
        apiError = (await response.json()) as ApiErrorResponse;
      } catch {
        // A status-only error is still useful when the response is not JSON.
      }
      throw new ApiError(
        apiError?.message ?? `Request failed with status ${response.status}`,
        response.status,
        apiError?.code,
        apiError?.fieldErrors ?? {},
      );
    }
    if (response.status === 204) return undefined as T;
    return (await response.json()) as T;
  }

  async requestBlob(path: string): Promise<Blob> {
    const headers = new Headers();
    this.addTelegramAuthorization(headers);
    const response = await fetch(`${this.baseUrl}${path}`, {
      headers,
      credentials: "include",
    });
    if (!response.ok) throw new ApiError(`Request failed with status ${response.status}`, response.status);
    return response.blob();
  }

  readonly generatedFetch: typeof fetch = async (input, init = {}) => {
    const headers = new Headers(init.headers);
    headers.set("Accept", "application/json");
    const telegramAuthenticated = this.addTelegramAuthorization(headers);
    if (isStateChanging(init.method) && !telegramAuthenticated) {
      const csrf = await this.getCsrfToken();
      headers.set(csrf.headerName, csrf.token);
    }
    return fetch(input, { ...init, headers, credentials: "include" });
  };

  async generated<T>(operation: Promise<T>): Promise<T> {
    try {
      return await operation;
    } catch (error) {
      if (error instanceof ResponseError) {
        let apiError: ApiErrorResponse | null = null;
        try {
          apiError = (await error.response.clone().json()) as ApiErrorResponse;
        } catch {
          // Preserve the status even when an upstream response is not JSON.
        }
        throw new ApiError(
          apiError?.message ?? `Request failed with status ${error.response.status}`,
          error.response.status,
          apiError?.code,
          apiError?.fieldErrors ?? {},
        );
      }
      throw error;
    }
  }

  private addTelegramAuthorization(headers: Headers): boolean {
    const authData = this.platform.getAuthData();
    if (!authData) return false;
    headers.set("Authorization", `tma ${authData}`);
    return true;
  }

  private getCsrfToken(): Promise<{ headerName: string; token: string }> {
    if (!this.csrfToken) {
      this.csrfToken = fetch(`${this.baseUrl}/api/v1/auth/csrf`, {
        headers: { Accept: "application/json" },
        credentials: "include",
      }).then(async (response) => {
        if (!response.ok) {
          throw new ApiError(
            `CSRF token request failed with status ${response.status}`,
            response.status,
          );
        }
        return response.json() as Promise<{ headerName: string; token: string }>;
      }).catch((error) => {
        this.csrfToken = null;
        throw error;
      });
    }
    return this.csrfToken;
  }
}

export function generatedWire<T>(value: unknown): T {
  return value as T;
}

function isStateChanging(method?: string): boolean {
  return !["GET", "HEAD", "OPTIONS", "TRACE"].includes((method ?? "GET").toUpperCase());
}
