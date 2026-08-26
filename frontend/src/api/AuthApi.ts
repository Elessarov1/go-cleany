import type { CurrentAuthentication } from "../domain/authentication";

export interface AuthApi {
  getCurrent(): Promise<CurrentAuthentication>;
  logout(): Promise<void>;
  googleLoginUrl(returnTo?: string): string;
  googleAdminLoginUrl(): string;
}
