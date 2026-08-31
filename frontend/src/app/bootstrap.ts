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
import type { PlatformCatalogApi } from "../api/PlatformCatalogApi";
import { HttpPlatformCatalogApi } from "../api/HttpPlatformCatalogApi";
import { MockPlatformCatalogApi } from "../api/MockPlatformCatalogApi";
import type { AuthApi } from "../api/AuthApi";
import { HttpAuthApi } from "../api/HttpAuthApi";
import { MockAuthApi } from "../api/MockAuthApi";
import type { AnalyticsApi } from "../api/AnalyticsApi";
import { HttpAnalyticsApi } from "../api/HttpAnalyticsApi";
import { MockAnalyticsApi } from "../api/MockAnalyticsApi";
import type { TransferApi } from "../api/TransferApi";
import { HttpTransferApi } from "../api/HttpTransferApi";
import { MockTransferApi } from "../api/MockTransferApi";
import type { SupportApi } from "../api/SupportApi";
import { HttpSupportApi } from "../api/HttpSupportApi";
import { MockSupportApi } from "../api/MockSupportApi";
import { initializeI18n } from "../i18n";
import { PreviewPlatform } from "../platform/PreviewPlatform";
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
  const preview = import.meta.env.VITE_PREVIEW_MODE === "true";
  const previewPlatform = new URLSearchParams(window.location.search).get("platform") === "telegram"
    ? "TELEGRAM"
    : "PREVIEW";
  const platform: Platform = isTelegramWebAppAvailable()
    ? new TelegramPlatform()
    : preview
      ? new PreviewPlatform(previewPlatform)
      : new WebPlatform();

  await initializeI18n(platform.getLanguage());
  platform.ready();

  const baseUrl = import.meta.env.VITE_API_BASE_URL?.trim();
  const normalizedBaseUrl = baseUrl?.replace(/\/$/, "") ?? "";
  const httpClient = new HttpApiClient(normalizedBaseUrl, platform);
  const api: CleaningApi = preview
    ? new MockCleaningApi(platform)
    : new HttpCleaningApi(httpClient);
  const customerApi: CustomerApi = preview
    ? new MockCustomerApi()
    : new HttpCustomerApi(httpClient);
  const rentalApi: RentalApi = preview
    ? new MockRentalApi(platform)
    : new HttpRentalApi(httpClient);
  const platformCatalogApi: PlatformCatalogApi = preview
    ? new MockPlatformCatalogApi()
    : new HttpPlatformCatalogApi(httpClient);
  const authApi: AuthApi = preview
    ? new MockAuthApi()
    : new HttpAuthApi(httpClient);
  const analyticsApi: AnalyticsApi = preview
    ? new MockAnalyticsApi()
    : new HttpAnalyticsApi(httpClient);
  const transferApi: TransferApi = preview
    ? new MockTransferApi()
    : new HttpTransferApi(httpClient);
  const supportApi: SupportApi = preview
    ? new MockSupportApi(async (service, sourceEntityId) => {
        if (service === "CLEANING") return (await api.getOrder(sourceEntityId)).status === "COMPLETED";
        if (service === "RENTAL") return (await rentalApi.getBooking(sourceEntityId)).status === "COMPLETED";
        return (await transferApi.getBooking(sourceEntityId)).status === "COMPLETED";
      })
    : new HttpSupportApi(httpClient);

  return { platform, api, customerApi, rentalApi, platformCatalogApi, authApi, analyticsApi, transferApi, supportApi };
}
