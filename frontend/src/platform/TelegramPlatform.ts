import type { Platform, PlatformUser } from "./Platform";

interface TelegramUser {
  id: number;
  username?: string;
  first_name: string;
  last_name?: string;
  language_code?: string;
}

interface TelegramWebApp {
  initData: string;
  initDataUnsafe?: {
    user?: TelegramUser;
  };
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

