import { useEffect, useRef, useState } from "react";
import { Link, Navigate, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../api/AuthApiProvider";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { usePlatformCatalogApi } from "../../api/PlatformCatalogApiProvider";
import { useRentalApi } from "../../api/RentalApiProvider";
import { useTransferApi } from "../../api/TransferApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { BrandName } from "../../components/BrandName/BrandName";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalBookingStatus } from "../../components/RentalBookingStatus/RentalBookingStatus";
import { TransferBookingStatus } from "../../components/TransferBookingStatus/TransferBookingStatus";
import type { CustomerActivityItem, CustomerHome, CustomerHomePrimaryAction, CustomerHomeRepeatOpportunity } from "../../domain/customer";
import type { CleaningOrderStatus } from "../../domain/order";
import type { PlatformServiceState } from "../../domain/platformService";
import type { RentalBookingStatus as RentalStatus } from "../../domain/rental";
import type { TransferBookingStatus as TransferStatus } from "../../domain/transfer";
import { formatDate } from "../../utils/format";

function serviceIcon(service: CustomerActivityItem["service"]): "building" | "sparkles" | "car" {
  if (service === "RENTAL") return "building";
  if (service === "TRANSFER") return "car";
  return "sparkles";
}

function HomeStatus({ item }: { item: CustomerActivityItem }) {
  if (item.service === "RENTAL") {
    return <RentalBookingStatus status={item.status as RentalStatus} />;
  }
  if (item.service === "TRANSFER") {
    return <TransferBookingStatus status={item.status as TransferStatus} />;
  }
  return <OrderStatus status={item.status as CleaningOrderStatus} />;
}

export function ServiceCatalogPage() {
  const { t, i18n } = useTranslation();
  const authentication = useAuthentication();
  const catalogApi = usePlatformCatalogApi();
  const customerApi = useCustomerApi();
  const cleaningApi = useCleaningApi();
  const rentalApi = useRentalApi();
  const transferApi = useTransferApi();
  const [searchParams] = useSearchParams();
  const [services, setServices] = useState<PlatformServiceState[] | null>(null);
  const [failed, setFailed] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [home, setHome] = useState<CustomerHome | null>(null);
  const [homeLoading, setHomeLoading] = useState(false);
  const [homeFailed, setHomeFailed] = useState(false);
  const [homeReloadKey, setHomeReloadKey] = useState(0);
  const trackedImpressions = useRef(new Set<string>());
  const referralCode = searchParams.get("ref")?.trim();
  const authenticated = authentication.status === "READY" && authentication.current.authenticated;
  const russian = i18n.resolvedLanguage === "ru";
  const locale = russian ? "ru-RU" : "en-US";

  useEffect(() => {
    let active = true;
    setFailed(false);
    catalogApi.getServices()
      .then((result) => {
        if (active) setServices(result);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [catalogApi, reloadKey]);

  useEffect(() => {
    let active = true;
    if (!authenticated) {
      setHome(null);
      setHomeLoading(false);
      setHomeFailed(false);
      return () => {
        active = false;
      };
    }
    setHomeLoading(true);
    setHomeFailed(false);
    customerApi.getHome()
      .then((result) => {
        if (active) setHome(result);
      })
      .catch(() => {
        if (active) setHomeFailed(true);
      })
      .finally(() => {
        if (active) setHomeLoading(false);
      });
    return () => {
      active = false;
    };
  }, [authenticated, customerApi, homeReloadKey]);

  useEffect(() => {
    const action = home?.primaryAction;
    if (action?.type === "RENTAL_TRANSFER_ARRIVAL" || action?.type === "RENTAL_TRANSFER_CHECKOUT") {
      const context = action.type === "RENTAL_TRANSFER_ARRIVAL" ? "ARRIVAL" : "CHECKOUT";
      const key = `rental-transfer-${action.sourceEntityId}-${context}`;
      if (!trackedImpressions.current.has(key)) {
        trackedImpressions.current.add(key);
        void rentalApi.recordTransferContextShown(action.sourceEntityId, context).catch(() => undefined);
      }
    }
    const repeat = home?.repeatOpportunity;
    if (!repeat) return;
    const key = `repeat-${repeat.service}-${repeat.sourceEntityId}`;
    if (trackedImpressions.current.has(key)) return;
    trackedImpressions.current.add(key);
    if (repeat.service === "CLEANING") {
      void cleaningApi.recordRepeatShown(repeat.sourceEntityId).catch(() => undefined);
    } else if (repeat.service === "TRANSFER") {
      void transferApi.recordRepeatShown(repeat.sourceEntityId).catch(() => undefined);
    }
  }, [cleaningApi, home, rentalApi, transferApi]);

  if (failed) {
    return (
      <ErrorState
        message={t("catalog.loadError")}
        onRetry={() => setReloadKey((key) => key + 1)}
      />
    );
  }
  if (!services) return <LoadingState />;

  const cleaning = services.find(({ service }) => service === "CLEANING");
  if (referralCode && cleaning) {
    return <Navigate replace to={`/cleaning?ref=${encodeURIComponent(referralCode)}`} />;
  }

  return (
    <div className="page page--service-catalog">
      <header className="service-catalog__header">
        <span className="eyebrow"><BrandName /> services</span>
        <h1>{t("catalog.title")}</h1>
        <p>{t("catalog.subtitle")}</p>
      </header>

      {authenticated ? (
        <CustomerHomeContext
          failed={homeFailed}
          home={home}
          loading={homeLoading}
          locale={locale}
          russian={russian}
          onRetry={() => setHomeReloadKey((key) => key + 1)}
        />
      ) : null}

      <section className="service-catalog__services">
        <h2>{t("catalog.allServices")}</h2>
        <div className="service-catalog__grid">
          {services.map((state) => {
            if (state.service === "TRANSFER") {
              return <ServiceCard key={state.service} className="transfer" icon="car" path="/transfer" title={t("catalog.transfer.title")} text={t("catalog.transfer.text")} test={state.status === "IN_TEST"} />;
            }
            if (state.service === "RENTAL") {
              return <ServiceCard key={state.service} className="rent" icon="building" path="/rent" title={t("catalog.rent.title")} text={t("catalog.rent.text")} test={state.status === "IN_TEST"} />;
            }
            return <ServiceCard key={state.service} className="cleaning" icon="sparkles" path="/cleaning" title={t("catalog.cleaning.title")} text={t("catalog.cleaning.text")} test={state.status === "IN_TEST"} />;
          })}
        </div>
      </section>

      {services.length === 0 ? (
        <p className="service-catalog__empty">{t("catalog.empty")}</p>
      ) : null}
      <p className="service-catalog__note">{t("catalog.note")}</p>
    </div>
  );
}

interface CustomerHomeContextProps {
  failed: boolean;
  home: CustomerHome | null;
  loading: boolean;
  locale: string;
  russian: boolean;
  onRetry(): void;
}

function CustomerHomeContext({ failed, home, loading, locale, russian, onRetry }: CustomerHomeContextProps) {
  const { t } = useTranslation();
  if (loading && !home) {
    return <div className="customer-home-state">{t("catalog.home.loading")}</div>;
  }
  if (failed && !home) {
    return (
      <div className="customer-home-state customer-home-state--error" role="alert">
        <span>{t("catalog.home.loadError")}</span>
        <button type="button" onClick={onRetry}>{t("common.retry")}</button>
      </div>
    );
  }
  if (!home?.hasActivity) return null;

  return (
    <section className="customer-home" aria-label={t("catalog.home.sectionLabel")}>
      {home.activeTransaction ? (
        <ActiveTransactionCard
          count={home.activeTransactionCount}
          item={home.activeTransaction}
          locale={locale}
          russian={russian}
        />
      ) : (
        <div className="customer-home__activity-link">
          <Link to="/account/activity">{t("catalog.home.activity")}</Link>
        </div>
      )}
      <div className="customer-home__opportunities">
        {home.primaryAction ? <PrimaryActionCard action={home.primaryAction} locale={locale} /> : null}
        {home.repeatOpportunity ? <RepeatCard opportunity={home.repeatOpportunity} locale={locale} /> : null}
      </div>
    </section>
  );
}

function ActiveTransactionCard({ count, item, locale, russian }: {
  count: number;
  item: CustomerActivityItem;
  locale: string;
  russian: boolean;
}) {
  const { t } = useTranslation();
  const title = russian ? item.titleRu : item.titleEn;
  const date = item.scheduledEndDate
    ? t("activity.dateRange", { from: formatDate(item.scheduledDate, locale), to: formatDate(item.scheduledEndDate, locale) })
    : formatDate(item.scheduledDate, locale);
  return (
    <div className="customer-home__active-wrap">
      <div className="customer-home__section-heading">
        <h2>{t("catalog.home.activeTitle")}</h2>
        <Link to="/account/activity">{t("catalog.home.allActivity", { count })}</Link>
      </div>
      <Link className="customer-home-card customer-home-card--active" to={item.targetPath}>
        <span className={`customer-home-card__icon customer-home-card__icon--${item.service.toLowerCase()}`}>
          <Icon name={serviceIcon(item.service)} size={24} />
        </span>
        <span className="customer-home-card__body">
          <small>{t(`activity.services.${item.service}`)}</small>
          <strong>{title}</strong>
          <span>{date}{item.scheduledTime ? ` · ${item.scheduledTime.slice(0, 5)}` : ""}</span>
        </span>
        <span className="customer-home-card__aside">
          <HomeStatus item={item} />
          <Icon name="arrow-right" size={19} />
        </span>
      </Link>
    </div>
  );
}

function PrimaryActionCard({ action, locale }: { action: CustomerHomePrimaryAction; locale: string }) {
  const { t } = useTranslation();
  return (
    <Link className="customer-home-card customer-home-card--opportunity" to={action.targetPath}>
      <span className={`customer-home-card__icon customer-home-card__icon--${action.targetService.toLowerCase()}`}>
        <Icon name={serviceIcon(action.targetService)} size={24} />
      </span>
      <span className="customer-home-card__body">
        <small>{t("catalog.home.nextTitle")}</small>
        <strong>{t(`catalog.home.actions.${action.type}.title`)}</strong>
        <span>{t(`catalog.home.actions.${action.type}.text`, { date: formatDate(action.relevantDate, locale) })}</span>
        {action.benefit ? (
          <b className="customer-home-card__benefit">
            {t("catalog.home.transferBenefit", { percent: Math.round(action.benefit.discountRate * 100) })}
          </b>
        ) : null}
      </span>
      <Icon name="arrow-right" size={19} />
    </Link>
  );
}

function RepeatCard({ opportunity, locale }: { opportunity: CustomerHomeRepeatOpportunity; locale: string }) {
  const { t } = useTranslation();
  const completed = new Intl.DateTimeFormat(locale, { dateStyle: "medium" }).format(new Date(opportunity.sourceCompletedAt));
  return (
    <Link className="customer-home-card customer-home-card--opportunity" to={opportunity.targetPath}>
      <span className={`customer-home-card__icon customer-home-card__icon--${opportunity.service.toLowerCase()}`}>
        <Icon name={serviceIcon(opportunity.service)} size={24} />
      </span>
      <span className="customer-home-card__body">
        <small>{t("catalog.home.repeatTitle")}</small>
        <strong>{t(`catalog.home.repeat.${opportunity.service}.title`)}</strong>
        <span>{t("catalog.home.repeat.completed", { date: completed })}</span>
      </span>
      <Icon name="arrow-right" size={19} />
    </Link>
  );
}

interface ServiceCardProps {
  className: "rent" | "cleaning" | "transfer";
  icon: "building" | "sparkles" | "car";
  path: string;
  title: string;
  text: string;
  test: boolean;
}

function ServiceCard({ className, icon, path, title, text, test }: ServiceCardProps) {
  const { t } = useTranslation();
  return (
    <Link className={`service-card service-card--${className}`} to={path}>
      <span className="service-card__icon"><Icon name={icon} size={34} /></span>
      <span className="service-card__copy">
        <small><BrandName service={className === "rent" ? "rental" : className} /></small>
        <strong>{title}</strong>
        {test ? <b className="service-test-badge">{t("catalog.testBadge")}</b> : null}
        <span>{text}</span>
      </span>
      <Icon name="arrow-right" size={20} />
    </Link>
  );
}
