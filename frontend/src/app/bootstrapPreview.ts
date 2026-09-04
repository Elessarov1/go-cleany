import { MockAnalyticsApi } from "../api/MockAnalyticsApi";
import { MockAuthApi } from "../api/MockAuthApi";
import { MockCleaningApi } from "../api/MockCleaningApi";
import { MockCustomerApi } from "../api/MockCustomerApi";
import { MockPlatformCatalogApi } from "../api/MockPlatformCatalogApi";
import { MockRentalApi } from "../api/MockRentalApi";
import { MockSupportApi } from "../api/MockSupportApi";
import { MockTransferApi } from "../api/MockTransferApi";
import { initializeI18n } from "../i18n";
import { PreviewPlatform } from "../platform/PreviewPlatform";
import type { AppServices } from "./bootstrap";

export async function bootstrapPreview(): Promise<AppServices> {
  const previewPlatform = new URLSearchParams(window.location.search).get("platform") === "telegram"
    ? "TELEGRAM"
    : "PREVIEW";
  const platform = new PreviewPlatform(previewPlatform);

  await initializeI18n(platform.getLanguage());
  platform.ready();

  const api = new MockCleaningApi(platform);
  const customerApi = new MockCustomerApi();
  const rentalApi = new MockRentalApi(platform);
  const platformCatalogApi = new MockPlatformCatalogApi();
  const authApi = new MockAuthApi();
  const analyticsApi = new MockAnalyticsApi();
  const transferApi = new MockTransferApi();
  const supportApi = new MockSupportApi(async (service, sourceEntityId) => {
    if (service === "CLEANING") return (await api.getOrder(sourceEntityId)).status === "COMPLETED";
    if (service === "RENTAL") return (await rentalApi.getBooking(sourceEntityId)).status === "COMPLETED";
    return (await transferApi.getBooking(sourceEntityId)).status === "COMPLETED";
  });

  return { platform, api, customerApi, rentalApi, platformCatalogApi, authApi, analyticsApi, transferApi, supportApi };
}
