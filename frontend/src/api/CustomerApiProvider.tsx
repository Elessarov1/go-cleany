import { createContext, type PropsWithChildren, useContext } from "react";
import type { CustomerApi } from "./CustomerApi";

const CustomerApiContext = createContext<CustomerApi | null>(null);

interface CustomerApiProviderProps extends PropsWithChildren {
  api: CustomerApi;
}

export function CustomerApiProvider({ api, children }: CustomerApiProviderProps) {
  return <CustomerApiContext.Provider value={api}>{children}</CustomerApiContext.Provider>;
}

export function useCustomerApi(): CustomerApi {
  const api = useContext(CustomerApiContext);
  if (!api) throw new Error("useCustomerApi must be used inside CustomerApiProvider");
  return api;
}
