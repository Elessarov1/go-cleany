import { RouterProvider } from "react-router-dom";
import type { CleaningApi } from "../api/CleaningApi";
import { CleaningApiProvider } from "../api/CleaningApiProvider";
import type { Platform } from "../platform/Platform";
import { PlatformProvider } from "../platform/PlatformProvider";
import { router } from "./router";

interface AppProps {
  platform: Platform;
  api: CleaningApi;
}

export function App({ platform, api }: AppProps) {
  return (
    <PlatformProvider platform={platform}>
      <CleaningApiProvider api={api}>
        <RouterProvider router={router} />
      </CleaningApiProvider>
    </PlatformProvider>
  );
}

