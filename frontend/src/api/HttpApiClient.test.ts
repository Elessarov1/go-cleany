import { afterEach, describe, expect, it, vi } from "vitest";
import type { Platform } from "../platform/Platform";
import { HttpApiClient } from "./HttpApiClient";

describe("HttpApiClient authentication", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("authenticates every Telegram Mini App request without relying on cookies or CSRF", async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce(jsonResponse({ accepted: true }))
      .mockResolvedValueOnce(jsonResponse({ accepted: true }))
      .mockResolvedValueOnce(new Response("image", { status: 200 }));
    vi.stubGlobal("fetch", fetchMock);
    const client = new HttpApiClient("", platformWithAuthData("signed-init-data"));

    await client.request("/api/test", { method: "POST", body: "{}" });
    await client.generatedFetch("/api/generated", { method: "POST", body: "{}" });
    await client.requestBlob("/api/media/1");

    expect(fetchMock).toHaveBeenCalledTimes(3);
    for (const [, init] of fetchMock.mock.calls) {
      const headers = new Headers(init?.headers);
      expect(headers.get("Authorization")).toBe("tma signed-init-data");
      expect(headers.has("X-XSRF-TOKEN")).toBe(false);
    }
  });

  it("keeps cookie-session CSRF protection for standalone web writes", async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce(jsonResponse({ headerName: "X-XSRF-TOKEN", token: "csrf-token" }))
      .mockResolvedValueOnce(jsonResponse({ accepted: true }));
    vi.stubGlobal("fetch", fetchMock);
    const client = new HttpApiClient("", platformWithAuthData(null));

    await client.request("/api/test", { method: "POST", body: "{}" });

    expect(fetchMock).toHaveBeenCalledTimes(2);
    const csrfCall = fetchMock.mock.calls.at(0)!;
    const requestCall = fetchMock.mock.calls.at(1)!;
    expect(csrfCall[0]).toBe("/api/v1/auth/csrf");
    const requestHeaders = new Headers(requestCall[1]?.headers);
    expect(requestHeaders.get("X-XSRF-TOKEN")).toBe("csrf-token");
    expect(requestHeaders.has("Authorization")).toBe(false);
  });
});

function platformWithAuthData(authData: string | null): Platform {
  return { getAuthData: () => authData } as Platform;
}

function jsonResponse(body: unknown): Response {
  return new Response(JSON.stringify(body), {
    status: 200,
    headers: { "Content-Type": "application/json" },
  });
}
