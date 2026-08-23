import type { CleaningApi } from "../api/CleaningApi";
import type { CustomerApi } from "../api/CustomerApi";
import { HttpCleaningApi } from "../api/HttpCleaningApi";
import { HttpApiClient } from "../api/HttpApiClient";
import { HttpCustomerApi } from "../api/HttpCustomerApi";
import { HttpRentalApi } from "../api/HttpRentalApi";
import { MockCleaningApi } from "../api/MockCleaningApi";
import { MockCustomerApi } from "../api/MockCustomerApi";
import { MockRentalApi } from "../api/MockRentalApi";
import type { RentalApi } from "../api/RentalApi";
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
  customerApi: CustomerApi;
  rentalApi: RentalApi;
}

export async function bootstrap(): Promise<AppServices> {
  const platform: Platform = isTelegramWebAppAvailable()
    ? new TelegramPlatform()
    : new BrowserPlatform();

  await initializeI18n(platform.getLanguage());
  platform.ready();

  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim();
  const normalizedBaseUrl = baseUrl?.replace(/\/$/, "") ?? "";
  const api: CleaningApi = baseUrl
    ? new HttpCleaningApi(normalizedBaseUrl, platform)
    : new MockCleaningApi(platform);
  const httpClient = new HttpApiClient(normalizedBaseUrl, platform);
  const customerApi: CustomerApi = baseUrl
    ? new HttpCustomerApi(httpClient)
    : new MockCustomerApi();
  const rentalApi: RentalApi = baseUrl
    ? new HttpRentalApi(httpClient)
    : new MockRentalApi(platform);

  return { platform, api, customerApi, rentalApi };
}
