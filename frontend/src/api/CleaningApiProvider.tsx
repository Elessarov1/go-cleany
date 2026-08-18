import { createContext, type PropsWithChildren, useContext } from "react";
import type { CleaningApi } from "./CleaningApi";

const CleaningApiContext = createContext<CleaningApi | null>(null);

interface CleaningApiProviderProps extends PropsWithChildren {
  api: CleaningApi;
}

export function CleaningApiProvider({ api, children }: CleaningApiProviderProps) {
  return (
    <CleaningApiContext.Provider value={api}>
      {children}
    </CleaningApiContext.Provider>
  );
}

export function useCleaningApi(): CleaningApi {
  const api = useContext(CleaningApiContext);
  if (!api) {
    throw new Error("useCleaningApi must be used inside CleaningApiProvider");
  }
  return api;
}

