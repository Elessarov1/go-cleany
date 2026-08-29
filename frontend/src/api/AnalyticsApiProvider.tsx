import { createContext, type PropsWithChildren, useContext } from "react";
import type { AnalyticsApi } from "./AnalyticsApi";

const AnalyticsApiContext = createContext<AnalyticsApi | null>(null);

interface AnalyticsApiProviderProps extends PropsWithChildren {
  api: AnalyticsApi;
}

export function AnalyticsApiProvider({ api, children }: AnalyticsApiProviderProps) {
  return <AnalyticsApiContext.Provider value={api}>{children}</AnalyticsApiContext.Provider>;
}

export function useAnalyticsApi(): AnalyticsApi {
  const api = useContext(AnalyticsApiContext);
  if (!api) throw new Error("useAnalyticsApi must be used inside AnalyticsApiProvider");
  return api;
}
