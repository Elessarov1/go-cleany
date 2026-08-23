import { createContext, type PropsWithChildren, useContext } from "react";
import type { RentalApi } from "./RentalApi";

const RentalApiContext = createContext<RentalApi | null>(null);

interface RentalApiProviderProps extends PropsWithChildren {
  api: RentalApi;
}

export function RentalApiProvider({ api, children }: RentalApiProviderProps) {
  return <RentalApiContext.Provider value={api}>{children}</RentalApiContext.Provider>;
}

export function useRentalApi(): RentalApi {
  const api = useContext(RentalApiContext);
  if (!api) throw new Error("useRentalApi must be used inside RentalApiProvider");
  return api;
}
