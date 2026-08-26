export type AuthenticationProvider = "TELEGRAM" | "GOOGLE";

export type PlatformRole = "ADMIN";

export interface LoginProviderAvailability {
  available: boolean;
}

export interface LoginProviders {
  google: LoginProviderAvailability;
}

export interface CurrentAuthentication {
  authenticated: boolean;
  customerId: number | null;
  displayName: string | null;
  provider: AuthenticationProvider | null;
  roles: PlatformRole[];
  loginProviders: LoginProviders;
}
