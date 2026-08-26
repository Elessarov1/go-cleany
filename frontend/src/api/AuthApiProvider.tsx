import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
  type PropsWithChildren,
} from "react";
import type { CurrentAuthentication } from "../domain/authentication";
import type { AuthApi } from "./AuthApi";

type AuthenticationStatus = "LOADING" | "READY" | "ERROR";

interface AuthenticationContextValue {
  current: CurrentAuthentication;
  status: AuthenticationStatus;
  isAdmin: boolean;
  googleLoginUrl: string;
  googleAdminLoginUrl: string;
  logout(): Promise<void>;
  reload(): Promise<void>;
}

const ANONYMOUS: CurrentAuthentication = {
  authenticated: false,
  customerId: null,
  displayName: null,
  provider: null,
  roles: [],
};

const AuthenticationContext = createContext<AuthenticationContextValue | null>(null);

interface AuthApiProviderProps extends PropsWithChildren {
  api: AuthApi;
}

export function AuthApiProvider({ api, children }: AuthApiProviderProps) {
  const [current, setCurrent] = useState(ANONYMOUS);
  const [status, setStatus] = useState<AuthenticationStatus>("LOADING");

  const reload = async () => {
    setStatus("LOADING");
    try {
      setCurrent(await api.getCurrent());
      setStatus("READY");
    } catch {
      setCurrent(ANONYMOUS);
      setStatus("ERROR");
    }
  };

  useEffect(() => {
    void reload();
  }, [api]);

  const value = useMemo<AuthenticationContextValue>(() => ({
    current,
    status,
    isAdmin: current.roles.includes("ADMIN"),
    googleLoginUrl: api.googleLoginUrl(),
    googleAdminLoginUrl: api.googleAdminLoginUrl(),
    logout: async () => {
      setStatus("LOADING");
      try {
        await api.logout();
        setCurrent(ANONYMOUS);
        setStatus("READY");
      } catch {
        setStatus("ERROR");
      }
    },
    reload,
  }), [api, current, status]);

  return (
    <AuthenticationContext.Provider value={value}>
      {children}
    </AuthenticationContext.Provider>
  );
}

export function useAuthentication(): AuthenticationContextValue {
  const context = useContext(AuthenticationContext);
  if (!context) {
    throw new Error("useAuthentication must be used inside AuthApiProvider");
  }
  return context;
}
