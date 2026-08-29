import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

function titleKey(pathname: string): string {
  if (pathname === "/") return "titles.home";
  if (pathname === "/privacy") return "titles.privacy";
  if (pathname === "/terms") return "titles.terms";
  if (pathname === "/notifications") return "titles.notifications";
  if (pathname === "/account" || pathname.startsWith("/account/")) return "titles.account";
  if (pathname === "/rent" || pathname === "/rent/properties") return "titles.rent";
  if (pathname.startsWith("/rent/")) return "titles.rentPage";
  if (pathname === "/cleaning") return "titles.cleaning";
  if (pathname.startsWith("/cleaning/")) return "titles.cleaningPage";
  if (pathname === "/transfer") return "titles.transfer";
  if (pathname.startsWith("/transfer/")) return "titles.transferPage";
  if (pathname === "/admin") return "titles.admin";
  if (pathname.startsWith("/admin/")) return "titles.adminPage";
  return "titles.fallback";
}

function setMeta(selector: string, value: string) {
  document.querySelector<HTMLMetaElement>(selector)?.setAttribute("content", value);
}

export function RouteMetadata() {
  const { t, i18n } = useTranslation();
  const { pathname } = useLocation();

  useEffect(() => {
    const title = t(titleKey(pathname));
    const description = t("titles.description");
    document.title = title;
    document.documentElement.lang = i18n.resolvedLanguage === "ru" ? "ru" : "en";
    setMeta('meta[name="description"]', description);
    setMeta('meta[property="og:title"]', title);
    setMeta('meta[property="og:description"]', description);
  }, [i18n.resolvedLanguage, pathname, t]);

  return null;
}
