import { createContext, type PropsWithChildren, useContext } from "react";
import type { SupportApi } from "./SupportApi";

const SupportApiContext = createContext<SupportApi | null>(null);

export function SupportApiProvider({ api, children }: PropsWithChildren<{ api: SupportApi }>) {
  return <SupportApiContext.Provider value={api}>{children}</SupportApiContext.Provider>;
}

export function useSupportApi(): SupportApi {
  const api = useContext(SupportApiContext);
  if (!api) throw new Error("useSupportApi must be used inside SupportApiProvider");
  return api;
}
