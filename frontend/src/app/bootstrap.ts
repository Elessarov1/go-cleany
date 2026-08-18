import type { CleaningApi } from "../api/CleaningApi";
import { HttpCleaningApi } from "../api/HttpCleaningApi";
import { MockCleaningApi } from "../api/MockCleaningApi";
import { initializeI18n } from "../i18n";
import { BrowserPlatform } from "../platform/BrowserPlatform";
import type { Platform } from "../platform/Platform";
import {
  isTelegramWebAppAvailable,
  TelegramPlatform,
} from "../platform/TelegramPlatform";

export interface AppServices {
  platform: Platform;
  api: CleaningApi;
}

export async function bootstrap(): Promise<AppServices> {
  const platform: Platform = isTelegramWebAppAvailable()
    ? new TelegramPlatform()
    : new BrowserPlatform();

  await initializeI18n(platform.getLanguage());
  platform.ready();

  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim();
  const api: CleaningApi = baseUrl
    ? new HttpCleaningApi(baseUrl.replace(/\/$/, ""), platform)
    : new MockCleaningApi(platform);

  return { platform, api };
}

