import type { Platform, PlatformUser } from "./Platform";

export class WebPlatform implements Platform {
  readonly kind = "WEB" as const;

  getUser(): PlatformUser | null {
    return null;
  }

  getAuthData(): null {
    return null;
  }

  getLanguage(): string {
    return navigator.language;
  }

  getStartParameter(): null {
    return null;
  }

  async ensureNotificationAccess(): Promise<boolean> {
    return true;
  }

  async requestPhoneNumber(): Promise<boolean> {
    return false;
  }

  ready(): void {
    // Ordinary browsers require no platform handshake.
  }

  close(): void {
    window.history.back();
  }

  openExternalLink(url: string): void {
    window.open(url, "_blank", "noopener,noreferrer");
  }
}
