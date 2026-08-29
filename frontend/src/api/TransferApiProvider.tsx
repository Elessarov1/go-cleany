import { createContext, type PropsWithChildren, useContext } from "react";
import type { TransferApi } from "./TransferApi";

const TransferApiContext = createContext<TransferApi | null>(null);

interface TransferApiProviderProps extends PropsWithChildren {
  api: TransferApi;
}

export function TransferApiProvider({ api, children }: TransferApiProviderProps) {
  return <TransferApiContext.Provider value={api}>{children}</TransferApiContext.Provider>;
}

export function useTransferApi(): TransferApi {
  const api = useContext(TransferApiContext);
  if (!api) throw new Error("useTransferApi must be used inside TransferApiProvider");
  return api;
}
