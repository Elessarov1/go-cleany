import type { Platform, PlatformUser } from "./Platform";

const PREVIEW_USER: PlatformUser = {
  id: 900_001,
  username: "browser_preview",
  firstName: "Alex",
};

export class PreviewPlatform implements Platform {
  constructor(readonly kind: "PREVIEW" | "TELEGRAM" = "PREVIEW") {}

  getUser(): PlatformUser {
    return PREVIEW_USER;
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
    // Preview mode requires no platform handshake.
  }

  close(): void {
    window.history.back();
  }

  openExternalLink(url: string): void {
    window.open(url, "_blank", "noopener,noreferrer");
  }
}
