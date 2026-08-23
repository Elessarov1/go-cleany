import { RouterProvider } from "react-router-dom";
import type { CleaningApi } from "../api/CleaningApi";
import { CleaningApiProvider } from "../api/CleaningApiProvider";
import type { CustomerApi } from "../api/CustomerApi";
import { CustomerApiProvider } from "../api/CustomerApiProvider";
import type { RentalApi } from "../api/RentalApi";
import { RentalApiProvider } from "../api/RentalApiProvider";
import type { Platform } from "../platform/Platform";
import { PlatformProvider } from "../platform/PlatformProvider";
import { router } from "./router";

interface AppProps {
  platform: Platform;
  api: CleaningApi;
  customerApi: CustomerApi;
  rentalApi: RentalApi;
}

export function App({ platform, api, customerApi, rentalApi }: AppProps) {
  return (
    <PlatformProvider platform={platform}>
      <CustomerApiProvider api={customerApi}>
        <CleaningApiProvider api={api}>
          <RentalApiProvider api={rentalApi}>
            <RouterProvider router={router} />
          </RentalApiProvider>
        </CleaningApiProvider>
      </CustomerApiProvider>
    </PlatformProvider>
  );
}
