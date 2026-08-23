import type { Platform } from "../platform/Platform";
import { ApiError } from "./ApiError";

interface ApiErrorResponse {
  code?: string;
  message?: string;
  fieldErrors?: Record<string, string> | null;
}

export class HttpApiClient {
  constructor(
    private readonly baseUrl: string,
    private readonly platform: Platform,
  ) {}

  resolveUrl(path: string): string {
    return /^(https?:)?\/\//.test(path) ? path : `${this.baseUrl}${path}`;
  }

  async request<T>(path: string, init: RequestInit = {}): Promise<T> {
    const headers = new Headers(init.headers);
    headers.set("Accept", "application/json");
    if (init.body && !(init.body instanceof FormData)) headers.set("Content-Type", "application/json");
    this.addAuthorization(headers);

    const response = await fetch(`${this.baseUrl}${path}`, { ...init, headers });
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
    this.addAuthorization(headers);
    const response = await fetch(`${this.baseUrl}${path}`, { headers });
    if (!response.ok) throw new ApiError(`Request failed with status ${response.status}`, response.status);
    return response.blob();
  }

  private addAuthorization(headers: Headers): void {
    const authData = this.platform.getAuthData();
    if (authData) headers.set("Authorization", `tma ${authData}`);
  }
}
