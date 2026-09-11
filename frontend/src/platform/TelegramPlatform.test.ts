import { afterEach, describe, expect, it, vi } from "vitest";
import { isTelegramWebAppAvailable, TelegramPlatform } from "./TelegramPlatform";

describe("TelegramPlatform", () => {
  afterEach(() => {
    vi.useRealTimers();
    delete window.Telegram;
    window.history.replaceState({}, "", "/");
  });

  it("does not classify an ordinary browser as Telegram when the public SDK is loaded", async () => {
    window.Telegram = { WebApp: webApp({ platform: "unknown" }) };

    await expect(isTelegramWebAppAvailable(0)).resolves.toBe(false);
  });

  it("uses launch data from the URL when Telegram Desktop has not populated the SDK yet", async () => {
    const initData = "query_id=query-1&user=%7B%22id%22%3A42%7D&auth_date=123&hash=signed";
    window.history.replaceState(
      {},
      "",
      `/#tgWebAppPlatform=tdesktop&tgWebAppData=${encodeURIComponent(initData)}`,
    );

    await expect(isTelegramWebAppAvailable(0)).resolves.toBe(true);
    expect(new TelegramPlatform().getAuthData()).toBe(initData);
  });

  it("waits for delayed Telegram Desktop initialization", async () => {
    vi.useFakeTimers();
    const sdk = webApp({ platform: "tdesktop" });
    window.Telegram = { WebApp: sdk };

    const available = isTelegramWebAppAvailable(100);
    window.setTimeout(() => {
      sdk.initData = "auth_date=123&hash=signed";
    }, 40);

    await vi.advanceTimersByTimeAsync(50);
    await expect(available).resolves.toBe(true);
  });

  it("keeps the Telegram shell when the client is known but auth data is missing", async () => {
    window.Telegram = { WebApp: webApp({ platform: "tdesktop" }) };

    await expect(isTelegramWebAppAvailable(0)).resolves.toBe(true);
    expect(new TelegramPlatform().getAuthData()).toBeNull();
  });
});

function webApp({ platform }: { platform: string }) {
  return {
    initData: "",
    platform,
    requestWriteAccess: vi.fn(),
    ready: vi.fn(),
    close: vi.fn(),
    openLink: vi.fn(),
  };
}
