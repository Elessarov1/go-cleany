import { createContext, type PropsWithChildren, useContext } from "react";
import type { Platform } from "./Platform";

const PlatformContext = createContext<Platform | null>(null);

interface PlatformProviderProps extends PropsWithChildren {
  platform: Platform;
}

export function PlatformProvider({
  platform,
  children,
}: PlatformProviderProps) {
  return (
    <PlatformContext.Provider value={platform}>
      {children}
    </PlatformContext.Provider>
  );
}

export function usePlatform(): Platform {
  const platform = useContext(PlatformContext);
  if (!platform) {
    throw new Error("usePlatform must be used inside PlatformProvider");
  }
  return platform;
}

