import type { Platform, PlatformUser } from "./Platform";

interface TelegramUser {
  id: number;
  username?: string;
  first_name: string;
  last_name?: string;
  language_code?: string;
  allows_write_to_pm?: boolean;
}

interface TelegramWebApp {
  initData: string;
  platform?: string;
  initDataUnsafe?: {
    user?: TelegramUser;
    start_param?: string;
  };
  requestWriteAccess(callback?: (allowed: boolean) => void): void;
  requestContact?(callback?: (shared: boolean) => void): void;
  ready(): void;
  close(): void;
  openLink(url: string): void;
}

const TELEGRAM_BOOTSTRAP_WAIT_MS = 1_000;
const TELEGRAM_BOOTSTRAP_POLL_MS = 25;

declare global {
  interface Window {
    Telegram?: {
      WebApp?: TelegramWebApp;
    };
  }
}

function getWebApp(): TelegramWebApp | null {
  return window.Telegram?.WebApp ?? null;
}

function normalizedParameter(value: string | null | undefined): string | null {
  const normalized = value?.trim();
  return normalized ? normalized : null;
}

function parameterFromLocation(name: string): string | null {
  const queryValue = normalizedParameter(new URLSearchParams(window.location.search).get(name));
  if (queryValue) {
    return queryValue;
  }

  const hash = window.location.hash.startsWith("#")
    ? window.location.hash.slice(1)
    : window.location.hash;
  return normalizedParameter(new URLSearchParams(hash).get(name));
}

function telegramAuthData(): string | null {
  return normalizedParameter(getWebApp()?.initData)
    ?? parameterFromLocation("tgWebAppData");
}

function hasTelegramContextHint(): boolean {
  if (telegramAuthData()) {
    return true;
  }

  const platform = normalizedParameter(getWebApp()?.platform)
    ?? parameterFromLocation("tgWebAppPlatform");
  return platform !== null && platform.toLowerCase() !== "unknown";
}

export class TelegramPlatform implements Platform {
  readonly kind = "TELEGRAM" as const;

  private writeAccessGrantedInSession = false;
  private phoneRequestedInSession = false;

  getUser(): PlatformUser | null {
    const user = getWebApp()?.initDataUnsafe?.user;
    if (!user) {
      return null;
    }

    return {
      id: user.id,
      username: user.username,
      firstName: user.first_name,
      lastName: user.last_name,
    };
  }

  getAuthData(): string | null {
    return telegramAuthData();
  }

  getLanguage(): string | null {
    return getWebApp()?.initDataUnsafe?.user?.language_code ?? null;
  }

  getStartParameter(): string | null {
    const webApp = getWebApp();

    const unsafeStartParameter = normalizedParameter(webApp?.initDataUnsafe?.start_param);
    if (unsafeStartParameter) {
      return unsafeStartParameter;
    }

    const initDataStartParameter = normalizedParameter(
      new URLSearchParams(telegramAuthData() ?? "").get("start_param"),
    );
    if (initDataStartParameter) {
      return initDataStartParameter;
    }

    return parameterFromLocation("tgWebAppStartParam");
  }

  ensureNotificationAccess(): Promise<boolean> {
    const webApp = getWebApp();
    if (
      this.writeAccessGrantedInSession ||
      webApp?.initDataUnsafe?.user?.allows_write_to_pm === true
    ) {
      return Promise.resolve(true);
    }

    if (typeof webApp?.requestWriteAccess !== "function") {
      return Promise.resolve(false);
    }

    return new Promise((resolve) => {
      webApp.requestWriteAccess((allowed) => {
        if (allowed) {
          this.writeAccessGrantedInSession = true;
        }
        resolve(allowed);
      });
    });
  }

  requestPhoneNumber(): Promise<boolean> {
    const webApp = getWebApp();
    if (
      this.phoneRequestedInSession ||
      typeof webApp?.requestContact !== "function"
    ) {
      return Promise.resolve(false);
    }

    this.phoneRequestedInSession = true;
    return new Promise((resolve) => {
      webApp.requestContact!((shared) => resolve(shared));
    });
  }

  ready(): void {
    getWebApp()?.ready();
  }

  close(): void {
    const webApp = getWebApp();
    if (webApp) {
      webApp.close();
      return;
    }
    window.history.back();
  }

  openExternalLink(url: string): void {
    const webApp = getWebApp();
    if (webApp) {
      webApp.openLink(url);
      return;
    }
    window.open(url, "_blank", "noopener,noreferrer");
  }
}

export async function isTelegramWebAppAvailable(
  waitMs = TELEGRAM_BOOTSTRAP_WAIT_MS,
): Promise<boolean> {
  if (telegramAuthData()) {
    return true;
  }
  if (!hasTelegramContextHint()) {
    return false;
  }

  const deadline = Date.now() + Math.max(0, waitMs);
  while (Date.now() < deadline) {
    await new Promise((resolve) => window.setTimeout(resolve, TELEGRAM_BOOTSTRAP_POLL_MS));
    if (telegramAuthData()) {
      return true;
    }
  }

  // Keep the Telegram shell even when launch data never arrives. This prevents
  // an embedded Google OAuth flow and lets the UI explain how to reopen the TMA.
  return hasTelegramContextHint();
}
