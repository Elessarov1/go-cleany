import { RouterProvider } from "react-router-dom";
import type { CleaningApi } from "../api/CleaningApi";
import { CleaningApiProvider } from "../api/CleaningApiProvider";
import type { CustomerApi } from "../api/CustomerApi";
import { CustomerApiProvider } from "../api/CustomerApiProvider";
import type { RentalApi } from "../api/RentalApi";
import { RentalApiProvider } from "../api/RentalApiProvider";
import type { PlatformCatalogApi } from "../api/PlatformCatalogApi";
import { PlatformCatalogApiProvider } from "../api/PlatformCatalogApiProvider";
import type { AuthApi } from "../api/AuthApi";
import { AuthApiProvider } from "../api/AuthApiProvider";
import type { AnalyticsApi } from "../api/AnalyticsApi";
import { AnalyticsApiProvider } from "../api/AnalyticsApiProvider";
import type { TransferApi } from "../api/TransferApi";
import { TransferApiProvider } from "../api/TransferApiProvider";
import type { Platform } from "../platform/Platform";
import { PlatformProvider } from "../platform/PlatformProvider";
import { router } from "./router";

interface AppProps {
  platform: Platform;
  api: CleaningApi;
  customerApi: CustomerApi;
  rentalApi: RentalApi;
  platformCatalogApi: PlatformCatalogApi;
  authApi: AuthApi;
  analyticsApi: AnalyticsApi;
  transferApi: TransferApi;
}

export function App({ platform, api, customerApi, rentalApi, platformCatalogApi, authApi, analyticsApi, transferApi }: AppProps) {
  return (
    <PlatformProvider platform={platform}>
      <AuthApiProvider api={authApi}>
        <CustomerApiProvider api={customerApi}>
          <CleaningApiProvider api={api}>
            <RentalApiProvider api={rentalApi}>
              <PlatformCatalogApiProvider api={platformCatalogApi}>
                <AnalyticsApiProvider api={analyticsApi}>
                  <TransferApiProvider api={transferApi}>
                    <RouterProvider router={router} />
                  </TransferApiProvider>
                </AnalyticsApiProvider>
              </PlatformCatalogApiProvider>
            </RentalApiProvider>
          </CleaningApiProvider>
        </CustomerApiProvider>
      </AuthApiProvider>
    </PlatformProvider>
  );
}
