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
}

export function App({ platform, api, customerApi, rentalApi, platformCatalogApi, authApi }: AppProps) {
  return (
    <PlatformProvider platform={platform}>
      <AuthApiProvider api={authApi}>
        <CustomerApiProvider api={customerApi}>
          <CleaningApiProvider api={api}>
            <RentalApiProvider api={rentalApi}>
              <PlatformCatalogApiProvider api={platformCatalogApi}>
                <RouterProvider router={router} />
              </PlatformCatalogApiProvider>
            </RentalApiProvider>
          </CleaningApiProvider>
        </CustomerApiProvider>
      </AuthApiProvider>
    </PlatformProvider>
  );
}
