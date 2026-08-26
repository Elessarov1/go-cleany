import { createContext, type PropsWithChildren, useContext } from "react";
import type { PlatformCatalogApi } from "./PlatformCatalogApi";

const PlatformCatalogApiContext = createContext<PlatformCatalogApi | null>(null);

interface PlatformCatalogApiProviderProps extends PropsWithChildren {
  api: PlatformCatalogApi;
}

export function PlatformCatalogApiProvider({
  api,
  children,
}: PlatformCatalogApiProviderProps) {
  return (
    <PlatformCatalogApiContext.Provider value={api}>
      {children}
    </PlatformCatalogApiContext.Provider>
  );
}

export function usePlatformCatalogApi(): PlatformCatalogApi {
  const api = useContext(PlatformCatalogApiContext);
  if (!api) {
    throw new Error("usePlatformCatalogApi must be used inside PlatformCatalogApiProvider");
  }
  return api;
}
