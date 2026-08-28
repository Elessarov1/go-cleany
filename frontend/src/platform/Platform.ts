export interface PlatformUser {
  id: number;
  username?: string;
  firstName: string;
  lastName?: string;
}

export type PlatformKind = "TELEGRAM" | "WEB" | "PREVIEW";

export interface Platform {
  readonly kind: PlatformKind;
  getUser(): PlatformUser | null;
  getAuthData(): string | null;
  getLanguage(): string | null;
  getStartParameter(): string | null;
  ensureNotificationAccess(): Promise<boolean>;
  requestPhoneNumber(): Promise<boolean>;
  ready(): void;
  close(): void;
  openExternalLink(url: string): void;
}
