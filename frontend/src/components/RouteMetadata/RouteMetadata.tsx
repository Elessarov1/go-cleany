import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const SITE_ORIGIN = "https://loco-place.com";

interface RouteMeta {
  titleKey: string;
  descriptionKey: string;
  indexable: boolean;
  canonicalPath?: string;
}

interface RentalPropertySeoResponse {
  titleRu: string | null;
  titleEn: string | null;
  area: string | null;
  maxGuests: number | null;
  areaSqm: number | string | null;
  baseDailyPrice: number | string | null;
  currency: string | null;
}

function routeMeta(pathname: string): RouteMeta {
  if (pathname === "/") {
    return { titleKey: "titles.home", descriptionKey: "titles.homeDescription", indexable: true, canonicalPath: "/" };
  }
  if (pathname === "/privacy") {
    return { titleKey: "titles.privacy", descriptionKey: "titles.privacyDescription", indexable: true, canonicalPath: "/privacy" };
  }
  if (pathname === "/terms") {
    return { titleKey: "titles.terms", descriptionKey: "titles.termsDescription", indexable: true, canonicalPath: "/terms" };
  }
  if (pathname === "/cleaning") {
    return { titleKey: "titles.cleaning", descriptionKey: "titles.cleaningDescription", indexable: true, canonicalPath: "/cleaning" };
  }
  if (pathname === "/rent" || pathname === "/rent/properties") {
    return { titleKey: "titles.rent", descriptionKey: "titles.rentDescription", indexable: true, canonicalPath: "/rent" };
  }
  if (/^\/rent\/properties\/[^/]+$/.test(pathname)) {
    return { titleKey: "titles.rentPage", descriptionKey: "titles.rentPageDescription", indexable: true, canonicalPath: pathname };
  }
  if (pathname === "/transfer") {
    return { titleKey: "titles.transfer", descriptionKey: "titles.transferDescription", indexable: true, canonicalPath: "/transfer" };
  }
  if (pathname === "/notifications") {
    return { titleKey: "titles.notifications", descriptionKey: "titles.notificationsDescription", indexable: false };
  }
  if (pathname === "/account/activity") {
    return { titleKey: "titles.activity", descriptionKey: "titles.activityDescription", indexable: false };
  }
  if (pathname === "/account" || pathname.startsWith("/account/")) {
    return { titleKey: "titles.account", descriptionKey: "titles.accountDescription", indexable: false };
  }
  if (pathname.startsWith("/rent/bookings")) {
    return { titleKey: "titles.rentPage", descriptionKey: "titles.privateDescription", indexable: false };
  }
  if (pathname.startsWith("/cleaning/")) {
    return { titleKey: "titles.cleaningPage", descriptionKey: "titles.cleaningPageDescription", indexable: false };
  }
  if (pathname.startsWith("/transfer/")) {
    return { titleKey: "titles.transferPage", descriptionKey: "titles.transferPageDescription", indexable: false };
  }
  if (pathname === "/admin") {
    return { titleKey: "titles.admin", descriptionKey: "titles.privateDescription", indexable: false };
  }
  if (pathname.startsWith("/admin/")) {
    return { titleKey: "titles.adminPage", descriptionKey: "titles.privateDescription", indexable: false };
  }
  return { titleKey: "titles.fallback", descriptionKey: "titles.description", indexable: false };
}

function upsertMeta(selector: string, attributes: Record<string, string>, value: string): void {
  let element = document.querySelector<HTMLMetaElement>(selector);
  if (!element) {
    element = document.createElement("meta");
    Object.entries(attributes).forEach(([name, attributeValue]) => element?.setAttribute(name, attributeValue));
    document.head.appendChild(element);
  }
  element.setAttribute("content", value);
}

function upsertCanonical(url: string): void {
  let element = document.querySelector<HTMLLinkElement>('link[rel="canonical"]');
  if (!element) {
    element = document.createElement("link");
    element.rel = "canonical";
    document.head.appendChild(element);
  }
  element.href = url;
}

function removeCanonical(): void {
  document.querySelector<HTMLLinkElement>('link[rel="canonical"]')?.remove();
}

function removeMeta(selector: string): void {
  document.querySelector<HTMLMetaElement>(selector)?.remove();
}

function setDocumentMetadata(title: string, description: string): void {
  document.title = title;
  upsertMeta('meta[name="description"]', { name: "description" }, description);
  upsertMeta('meta[property="og:title"]', { property: "og:title" }, title);
  upsertMeta('meta[property="og:description"]', { property: "og:description" }, description);
}

export function RouteMetadata() {
  const { t, i18n } = useTranslation();
  const { pathname, search } = useLocation();
  const language = i18n.resolvedLanguage === "ru" ? "ru" : "en";

  useEffect(() => {
    const definition = routeMeta(pathname);
    const preview = new URLSearchParams(search).has("preview");
    const indexable = definition.indexable && !preview;
    const title = t(definition.titleKey);
    const description = t(definition.descriptionKey);

    document.documentElement.lang = language;
    setDocumentMetadata(title, description);
    upsertMeta('meta[name="robots"]', { name: "robots" }, indexable ? "index,follow" : "noindex,nofollow");
    upsertMeta('meta[property="og:site_name"]', { property: "og:site_name" }, "Loco Place");
    upsertMeta('meta[property="og:type"]', { property: "og:type" }, "website");
    upsertMeta('meta[property="og:locale"]', { property: "og:locale" }, language === "ru" ? "ru_RU" : "en_US");

    if (indexable && definition.canonicalPath) {
      const canonical = new URL(definition.canonicalPath, SITE_ORIGIN).toString();
      upsertCanonical(canonical);
      upsertMeta('meta[property="og:url"]', { property: "og:url" }, canonical);
    } else {
      removeCanonical();
      removeMeta('meta[property="og:url"]');
    }
  }, [language, pathname, search, t]);

  useEffect(() => {
    const match = pathname.match(/^\/rent\/properties\/([^/]+)$/);
    if (!match || new URLSearchParams(search).has("preview")) return;

    const controller = new AbortController();
    const locale = language === "ru" ? "ru-RU" : "en-US";

    fetch(`/api/v1/rental/properties/${match[1]}`, {
      credentials: "same-origin",
      signal: controller.signal,
    })
      .then(async (response) => {
        if (!response.ok) throw new Error(`Rental property metadata request failed: ${response.status}`);
        return response.json() as Promise<RentalPropertySeoResponse>;
      })
      .then((property) => {
        const title = language === "ru"
          ? property.titleRu ?? property.titleEn
          : property.titleEn ?? property.titleRu;
        if (!title) return;

        const area = property.area ?? (language === "ru" ? "Аланья" : "Alanya");
        const guests = property.maxGuests ?? "—";
        const areaSqm = property.areaSqm ?? "—";
        const priceValue = Number(property.baseDailyPrice);
        const price = Number.isFinite(priceValue)
          ? new Intl.NumberFormat(locale, { maximumFractionDigits: 2 }).format(priceValue)
          : property.baseDailyPrice ?? "—";
        const currency = property.currency ?? "TRY";

        setDocumentMetadata(
          t("titles.rentalPropertyTitle", { title, area }),
          t("titles.rentalPropertyDescription", { title, area, guests, areaSqm, price, currency }),
        );
      })
      .catch((error: unknown) => {
        if (error instanceof DOMException && error.name === "AbortError") return;
        // Generic route metadata remains valid when the optional SEO enrichment fails.
      });

    return () => controller.abort();
  }, [language, pathname, search, t]);

  return null;
}
