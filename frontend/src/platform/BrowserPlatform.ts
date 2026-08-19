import type { Platform, PlatformUser } from "./Platform";

const BROWSER_USER: PlatformUser = {
  id: 900_001,
  username: "browser_preview",
  firstName: "Alex",
};

export class BrowserPlatform implements Platform {
  getUser(): PlatformUser {
    return BROWSER_USER;
  }

  getAuthData(): null {
    return null;
  }

  getLanguage(): string {
    return navigator.language;
  }

  async ensureNotificationAccess(): Promise<boolean> {
    return true;
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
