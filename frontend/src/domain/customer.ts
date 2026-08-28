export interface CustomerProfile {
  phone: string | null;
}

export type ExternalIdentityProvider = "GOOGLE" | "TELEGRAM";

export interface AccountIdentity {
  provider: ExternalIdentityProvider;
  linked: boolean;
  username: string | null;
  writeAccessAllowed: boolean;
}

export interface AccountIdentities {
  identities: AccountIdentity[];
}

export interface AccountLinkInitiated {
  deepLink: string;
  expiresAt: string;
}
