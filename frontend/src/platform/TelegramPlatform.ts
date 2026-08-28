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

declare global {
  interface Window {
    Telegram?: {
      WebApp?: TelegramWebApp;
    };
  }
}

function getWebApp(): TelegramWebApp {
  const webApp = window.Telegram?.WebApp;
  if (!webApp) {
    throw new Error("Telegram WebApp is not available");
  }
  return webApp;
}

export class TelegramPlatform implements Platform {
  readonly kind = "TELEGRAM" as const;

  private writeAccessGrantedInSession = false;
  private phoneRequestedInSession = false;

  getUser(): PlatformUser | null {
    const user = getWebApp().initDataUnsafe?.user;
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
    return getWebApp().initData || null;
  }

  getLanguage(): string | null {
    return getWebApp().initDataUnsafe?.user?.language_code ?? null;
  }

  getStartParameter(): string | null {
    return getWebApp().initDataUnsafe?.start_param ?? null;
  }

  ensureNotificationAccess(): Promise<boolean> {
    const webApp = getWebApp();
    if (
      this.writeAccessGrantedInSession ||
      webApp.initDataUnsafe?.user?.allows_write_to_pm === true
    ) {
      return Promise.resolve(true);
    }

    if (typeof webApp.requestWriteAccess !== "function") {
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
      typeof webApp.requestContact !== "function"
    ) {
      return Promise.resolve(false);
    }

    this.phoneRequestedInSession = true;
    return new Promise((resolve) => {
      webApp.requestContact!((shared) => resolve(shared));
    });
  }

  ready(): void {
    getWebApp().ready();
  }

  close(): void {
    getWebApp().close();
  }

  openExternalLink(url: string): void {
    getWebApp().openLink(url);
  }
}

export function isTelegramWebAppAvailable(): boolean {
  return Boolean(window.Telegram?.WebApp?.initData);
}
