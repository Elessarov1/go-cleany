export interface PlatformUser {
  id: number;
  username?: string;
  firstName: string;
  lastName?: string;
}

export interface Platform {
  getUser(): PlatformUser | null;
  getAuthData(): string | null;
  getLanguage(): string | null;
  ensureNotificationAccess(): Promise<boolean>;
  ready(): void;
  close(): void;
  openExternalLink(url: string): void;
}
