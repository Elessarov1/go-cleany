import type { CleaningApi } from "../api/CleaningApi";
import type { CustomerApi } from "../api/CustomerApi";
import { HttpCleaningApi } from "../api/HttpCleaningApi";
import { HttpApiClient } from "../api/HttpApiClient";
import { HttpCustomerApi } from "../api/HttpCustomerApi";
import { HttpRentalApi } from "../api/HttpRentalApi";
import type { RentalApi } from "../api/RentalApi";
import type { PlatformCatalogApi } from "../api/PlatformCatalogApi";
import { HttpPlatformCatalogApi } from "../api/HttpPlatformCatalogApi";
import type { AuthApi } from "../api/AuthApi";
import { HttpAuthApi } from "../api/HttpAuthApi";
import type { AnalyticsApi } from "../api/AnalyticsApi";
import { HttpAnalyticsApi } from "../api/HttpAnalyticsApi";
import type { TransferApi } from "../api/TransferApi";
import { HttpTransferApi } from "../api/HttpTransferApi";
import type { SupportApi } from "../api/SupportApi";
import { HttpSupportApi } from "../api/HttpSupportApi";
import { initializeI18n } from "../i18n";
import type { Platform } from "../platform/Platform";
import { WebPlatform } from "../platform/WebPlatform";
import {
  isTelegramWebAppAvailable,
  TelegramPlatform,
} from "../platform/TelegramPlatform";

export interface AppServices {
  platform: Platform;
  api: CleaningApi;
  customerApi: CustomerApi;
  rentalApi: RentalApi;
  platformCatalogApi: PlatformCatalogApi;
  authApi: AuthApi;
  analyticsApi: AnalyticsApi;
  transferApi: TransferApi;
  supportApi: SupportApi;
}

export async function bootstrap(): Promise<AppServices> {
  if (import.meta.env.VITE_PREVIEW_MODE === "true") {
    const { bootstrapPreview } = await import("./bootstrapPreview");
    return bootstrapPreview();
  }

  const platform: Platform = isTelegramWebAppAvailable()
    ? new TelegramPlatform()
    : new WebPlatform();

  await initializeI18n(platform.getLanguage());
  platform.ready();

  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim();
  const normalizedBaseUrl = baseUrl?.replace(/\/$/, "") ?? "";
  const httpClient = new HttpApiClient(normalizedBaseUrl, platform);
  const api: CleaningApi = new HttpCleaningApi(httpClient);
  const customerApi: CustomerApi = new HttpCustomerApi(httpClient);
  const rentalApi: RentalApi = new HttpRentalApi(httpClient);
  const platformCatalogApi: PlatformCatalogApi = new HttpPlatformCatalogApi(httpClient);
  const authApi: AuthApi = new HttpAuthApi(httpClient);
  const analyticsApi: AnalyticsApi = new HttpAnalyticsApi(httpClient);
  const transferApi: TransferApi = new HttpTransferApi(httpClient);
  const supportApi: SupportApi = new HttpSupportApi(httpClient);

  return { platform, api, customerApi, rentalApi, platformCatalogApi, authApi, analyticsApi, transferApi, supportApi };
}
