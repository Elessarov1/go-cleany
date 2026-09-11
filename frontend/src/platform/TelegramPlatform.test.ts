import { afterEach, describe, expect, it, vi } from "vitest";
import { isTelegramWebAppAvailable, TelegramPlatform } from "./TelegramPlatform";

describe("TelegramPlatform", () => {
  afterEach(() => {
    delete window.Telegram;
    window.history.replaceState({}, "", "/");
  });

  it("does not classify an ordinary browser as Telegram when the public SDK is loaded", () => {
    window.Telegram = { WebApp: webApp({ platform: "unknown" }) };

    expect(isTelegramWebAppAvailable()).toBe(false);
  });

  it("uses signed launch data from a real Mini App URL", () => {
    const initData = "query_id=query-1&user=%7B%22id%22%3A42%7D&auth_date=123&hash=signed";
    window.history.replaceState(
      {},
      "",
      `/#tgWebAppPlatform=tdesktop&tgWebAppData=${encodeURIComponent(initData)}`,
    );

    expect(isTelegramWebAppAvailable()).toBe(true);
    expect(new TelegramPlatform().getAuthData()).toBe(initData);
  });

  it("keeps the Telegram shell for its embedded browser without treating it as authenticated", () => {
    window.Telegram = { WebApp: webApp({ platform: "tdesktop" }) };

    expect(isTelegramWebAppAvailable()).toBe(true);
    expect(new TelegramPlatform().getAuthData()).toBeNull();
  });

  it("uses Telegram native navigation to relaunch the Main Mini App", () => {
    const sdk = webApp({ platform: "tdesktop" });
    window.Telegram = { WebApp: sdk };

    new TelegramPlatform().openTelegramLink("https://t.me/go_cleany_bot?startapp");

    expect(sdk.openTelegramLink).toHaveBeenCalledWith("https://t.me/go_cleany_bot?startapp");
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
    openTelegramLink: vi.fn(),
  };
}
