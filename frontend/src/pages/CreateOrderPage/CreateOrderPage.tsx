import { useEffect, useMemo, useState, type FormEvent } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { CleaningApiError } from "../../api/CleaningApi";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ServiceInfoDialog } from "../../components/ServiceInfoDialog/ServiceInfoDialog";
import { LoadingState, ErrorState } from "../../components/PageState/PageState";
import type { CleaningConfiguration } from "../../domain/configuration";
import type {
  ApartmentType,
  CleaningType,
  CleaningOrderQuote,
  CreateCleaningOrderRequest,
  ServiceArea,
} from "../../domain/order";
import type { RentalCleaningContext } from "../../domain/rental";
import { calculateDisplayedPrice, formatPrice } from "../../domain/pricing";
import { usePlatform } from "../../platform/PlatformProvider";
import { addDaysToInputValue, formatDate, todayAsInputValue } from "../../utils/format";
import { getBrandName } from "../../brand/productBrand";
import { useAuthentication } from "../../api/AuthApiProvider";
import { AuthenticationRequiredState } from "../../components/CustomerAccessGate/CustomerAccessGate";

interface FormState {
  area?: ServiceArea;
  apartmentType?: ApartmentType;
  duplex: boolean;
  cleaningType?: CleaningType;
  requestedDate: string;
  address: string;
  phone: string;
  comment: string;
  referralCode: string;
}

type FormField =
  | "area"
  | "apartmentType"
  | "cleaningType"
  | "requestedDate"
  | "address"
  | "phone"
  | "referralCode";

type SubmitError = "notificationAccess" | "createOrder" | "rentalBenefit" | null;

const initialForm: FormState = {
  duplex: false,
  requestedDate: "",
  address: "",
  phone: "",
  comment: "",
  referralCode: "",
};

export function CreateOrderPage() {
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const customerApi = useCustomerApi();
  const rentalApi = useRentalApi();
  const platform = usePlatform();
  const authentication = useAuthentication();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [configuration, setConfiguration] = useState<CleaningConfiguration | null>(null);
  const [configurationError, setConfigurationError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [form, setForm] = useState<FormState>(initialForm);
  const [errors, setErrors] = useState<Partial<Record<FormField, string>>>({});
  const [serviceInfo, setServiceInfo] = useState<CleaningType | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<SubmitError>(null);
  const [quote, setQuote] = useState<CleaningOrderQuote | null>(null);
  const [isQuoteLoading, setIsQuoteLoading] = useState(false);
  const [quoteReferralError, setQuoteReferralError] = useState(false);
  const [rentalContext, setRentalContext] = useState<RentalCleaningContext | null>(null);
  const [rentalPromoCode, setRentalPromoCode] = useState("");
  const [rentalBenefitError, setRentalBenefitError] = useState(false);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    const referralCode = searchParams.get("ref")?.trim();
    if (referralCode && !searchParams.get("rentalBooking")) {
      setForm((current) => ({
        ...current,
        referralCode: referralCode.slice(0, 32).toUpperCase(),
      }));
    }
  }, [searchParams]);

  useEffect(() => {
    if (!authentication.current.authenticated) {
      setRentalContext(null);
      setRentalPromoCode("");
      setForm((current) => current.address || current.phone
        ? { ...current, address: "", phone: "" }
        : current);
      return;
    }
    const rawBookingId = searchParams.get("rentalBooking");
    const bookingId = rawBookingId === null ? Number.NaN : Number(rawBookingId);
    if (!Number.isSafeInteger(bookingId) || bookingId <= 0) {
      setRentalContext(null);
      setRentalPromoCode("");
      return;
    }
    let active = true;
    rentalApi.getCleaningContext(bookingId)
      .then((context) => {
        if (!active) return;
        setRentalContext(context);
        setForm((current) => ({
          ...current,
          address: current.address.trim() ? current.address : context.address,
          phone: current.phone.trim() ? current.phone : context.phone,
          referralCode: "",
        }));
        const requestedPromo = searchParams.get("promo")?.trim().toUpperCase();
        setRentalPromoCode(
          requestedPromo && context.promoCode === requestedPromo
            ? context.promoCode
            : "",
        );
      })
      .catch(() => {
        if (active) {
          setRentalContext(null);
          setRentalPromoCode("");
        }
      });
    return () => {
      active = false;
    };
  }, [authentication.current.authenticated, rentalApi, searchParams]);

  useEffect(() => {
    if (!authentication.current.authenticated) {
      setForm((current) => current.phone ? { ...current, phone: "" } : current);
      return;
    }
    let active = true;

    const fillPhone = (phone?: string | null): boolean => {
      if (!active || !phone) return false;
      setForm((current) => current.phone.trim()
        ? current
        : { ...current, phone });
      return true;
    };

    const loadPhone = async () => {
      try {
        const profile = await customerApi.getCurrentProfile();
        if (fillPhone(profile.phone)) return;

        const shared = await platform.requestPhoneNumber();
        if (!active || !shared) return;

        for (let attempt = 0; attempt < 10 && active; attempt += 1) {
          await new Promise((resolve) => window.setTimeout(resolve, 500));
          const refreshedProfile = await customerApi.getCurrentProfile();
          if (fillPhone(refreshedProfile.phone)) return;
        }
      } catch {
        // Phone prefill is optional and must not block manual order creation.
      }
    };

    void loadPhone();
    return () => {
      active = false;
    };
  }, [authentication.current.authenticated, customerApi, platform]);

  useEffect(() => {
    let active = true;
    setConfigurationError(false);
    api.getConfiguration()
      .then((value) => {
        if (active) setConfiguration(value);
      })
      .catch(() => {
        if (active) setConfigurationError(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey]);

  const displayedBasePrice = useMemo(() => {
    if (!configuration || !form.apartmentType || !form.cleaningType) {
      return null;
    }
    return calculateDisplayedPrice(
      configuration,
      form.apartmentType,
      form.cleaningType,
      form.duplex,
    );
  }, [configuration, form.apartmentType, form.cleaningType, form.duplex]);

  useEffect(() => {
    if (!authentication.current.authenticated) {
      setQuote(null);
      setIsQuoteLoading(false);
      setQuoteReferralError(false);
      return;
    }
    if (!form.apartmentType || !form.cleaningType) {
      setQuote(null);
      setIsQuoteLoading(false);
      setQuoteReferralError(false);
      return;
    }
    if (rentalPromoCode && !form.requestedDate) {
      setQuote(null);
      setIsQuoteLoading(false);
      setQuoteReferralError(false);
      setRentalBenefitError(false);
      return;
    }

    let active = true;
    setQuote(null);
    setIsQuoteLoading(true);
    setQuoteReferralError(false);
    setRentalBenefitError(false);
    const timeout = window.setTimeout(() => {
      api.quoteOrder({
        apartmentType: form.apartmentType!,
        cleaningType: form.cleaningType!,
        duplex: form.duplex,
        referralCode: rentalPromoCode ? undefined : form.referralCode.trim() || undefined,
        requestedDate: form.requestedDate || undefined,
        rentalCleaningPromoCode: rentalPromoCode || undefined,
      })
        .then((value) => {
          if (active) {
            setQuote(value);
            setErrors((current) => ({ ...current, referralCode: undefined }));
          }
        })
        .catch((error) => {
          if (!active) return;
          setQuote(null);
          if (
            error instanceof CleaningApiError &&
            (error.code === "referral_not_applicable" || error.fieldErrors.referralCode)
          ) {
            setQuoteReferralError(true);
            setErrors((current) => ({
              ...current,
              referralCode: t("create.validation.referralCode"),
            }));
          } else if (
            error instanceof CleaningApiError
            && error.code === "rental_cleaning_benefit_not_applicable"
          ) {
            setRentalBenefitError(true);
          }
        })
        .finally(() => {
          if (active) setIsQuoteLoading(false);
        });
    }, 300);

    return () => {
      active = false;
      window.clearTimeout(timeout);
    };
  }, [
    api,
    authentication.current.authenticated,
    form.apartmentType,
    form.cleaningType,
    form.duplex,
    form.referralCode,
    form.requestedDate,
    rentalPromoCode,
    t,
  ]);

  const price = quote?.finalCustomerPrice ?? displayedBasePrice;

  const updateForm = <K extends keyof FormState>(key: K, value: FormState[K]) => {
    setForm((current) => ({ ...current, [key]: value }));
    if (key in errors) {
      setErrors((current) => ({ ...current, [key]: undefined }));
    }
  };

  const validate = (): Partial<Record<FormField, string>> => {
    const nextErrors: Partial<Record<FormField, string>> = {};
    if (!form.area) nextErrors.area = t("create.validation.area");
    if (!form.apartmentType) nextErrors.apartmentType = t("create.validation.apartmentType");
    if (!form.cleaningType) nextErrors.cleaningType = t("create.validation.cleaningType");
    if (!form.requestedDate) {
      nextErrors.requestedDate = t("create.validation.requestedDate");
    } else if (form.requestedDate < todayAsInputValue()) {
      nextErrors.requestedDate = t("create.validation.pastDate");
    }
    if (!form.address.trim()) nextErrors.address = t("create.validation.address");
    if (!/^\+\s*\d/.test(form.phone.trim())) {
      nextErrors.phone = t("create.validation.phone");
    }
    if (form.referralCode.trim() && quoteReferralError) {
      nextErrors.referralCode = t("create.validation.referralCode");
    }
    return nextErrors;
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!authentication.current.authenticated) return;
    const nextErrors = validate();
    setErrors(nextErrors);
    setSubmitError(null);

    if (Object.keys(nextErrors).length > 0) {
      document.querySelector(".field-error")?.parentElement?.scrollIntoView({
        behavior: "smooth",
        block: "center",
      });
      return;
    }

    if (!form.area || !form.apartmentType || !form.cleaningType) {
      return;
    }

    const request: CreateCleaningOrderRequest = {
      area: form.area,
      apartmentType: form.apartmentType,
      duplex: form.duplex,
      cleaningType: form.cleaningType,
      requestedDate: form.requestedDate,
      address: form.address.trim(),
      phone: form.phone.trim(),
      comment: form.comment.trim() || undefined,
      referralCode: rentalPromoCode ? undefined : form.referralCode.trim() || undefined,
      rentalCleaningPromoCode: rentalPromoCode || undefined,
    };

    try {
      setIsSubmitting(true);
      const allowed = await platform.ensureNotificationAccess();
      if (!allowed) {
        setSubmitError("notificationAccess");
        return;
      }

      const order = await api.createOrder(request);
      navigate(`/cleaning/orders/${order.id}/created`);
    } catch (error) {
      if (
        error instanceof CleaningApiError &&
        (error.code === "invalid_phone_number" || error.fieldErrors.phone)
      ) {
        setErrors((current) => ({
          ...current,
          phone: t("create.validation.phone"),
        }));
        document.getElementById("phone")?.scrollIntoView({
          behavior: "smooth",
          block: "center",
        });
      } else if (
        error instanceof CleaningApiError &&
        (error.code === "booking_date_unavailable" || error.fieldErrors.requestedDate)
      ) {
        setErrors((current) => ({
          ...current,
          requestedDate: t("create.validation.pastDate"),
        }));
        document.getElementById("requested-date")?.scrollIntoView({
          behavior: "smooth",
          block: "center",
        });
      } else if (
        error instanceof CleaningApiError &&
        (error.code === "referral_not_applicable" || error.fieldErrors.referralCode)
      ) {
        setErrors((current) => ({
          ...current,
          referralCode: t("create.validation.referralCode"),
        }));
        document.getElementById("referral-code")?.scrollIntoView({
          behavior: "smooth",
          block: "center",
        });
      } else if (
        error instanceof CleaningApiError
        && error.code === "rental_cleaning_benefit_not_applicable"
      ) {
        setRentalBenefitError(true);
        setSubmitError("rentalBenefit");
      } else {
        setSubmitError("createOrder");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!configuration && !configurationError) {
    return <LoadingState />;
  }

  if (configurationError || !configuration) {
    return (
      <ErrorState
        message={t("orders.loadError")}
        onRetry={() => setReloadKey((value) => value + 1)}
      />
    );
  }

  const today = todayAsInputValue();
  const lastBookingDate = addDaysToInputValue(today, configuration.bookingDaysAhead);

  return (
    <div className="page page--create">
      <section className="hero">
        <div className="hero__copy">
          <span className="eyebrow">{t("create.eyebrow")}</span>
          <h1>{t("create.title")}</h1>
          <p>{t("create.subtitle")}</p>
        </div>
        <div className="hero__art" aria-hidden="true">
          <span className="broom-icon hero__broom-icon" />
        </div>
      </section>

      {rentalContext ? (
        <section className="rental-cleaning-context">
          <span><Icon name="building" size={21} /></span>
          <div>
            <strong>{t("create.rentalContext.title", { id: rentalContext.rentalBookingId })}</strong>
            <p>{t("create.rentalContext.prefilled")}</p>
            {rentalPromoCode ? (
              <small>{t("create.rentalContext.benefit", {
                code: rentalPromoCode,
                from: formatDate(rentalContext.earliestBenefitCleaningDate, locale),
                to: formatDate(rentalContext.checkOutDate, locale),
              })}</small>
            ) : null}
          </div>
        </section>
      ) : null}

      <form className="booking-form" noValidate onSubmit={handleSubmit}>
        <section className="form-section">
          <div className="section-heading">
            <span className="section-heading__number">{t("create.area.step")}</span>
            <div>
              <h2 id="cleaning-area-heading">{t("create.area.title")}</h2>
              <p>{t("create.area.hint")}</p>
            </div>
          </div>
          <div className="choice-grid choice-grid--areas" role="group" aria-labelledby="cleaning-area-heading">
            {configuration.areas.map((area) => (
              <button
                key={area}
                className={`choice-card${form.area === area ? " is-selected" : ""}`}
                type="button"
                aria-pressed={form.area === area}
                onClick={() => updateForm("area", area)}
              >
                <span className="choice-card__pin"><Icon name="location" size={20} /></span>
                <span>{t(`areas.${area}`)}</span>
              </button>
            ))}
          </div>
          {errors.area ? <p className="field-error">{errors.area}</p> : null}
        </section>

        <section className="form-section">
          <div className="section-heading">
            <span className="section-heading__number">{t("create.apartment.step")}</span>
            <div><h2 id="cleaning-apartment-heading">{t("create.apartment.title")}</h2></div>
          </div>
          <div className="choice-grid choice-grid--apartments" role="group" aria-labelledby="cleaning-apartment-heading">
            {configuration.apartmentTypes.map(({ type }) => (
              <button
                key={type}
                className={`choice-card choice-card--compact${form.apartmentType === type ? " is-selected" : ""}`}
                type="button"
                aria-pressed={form.apartmentType === type}
                onClick={() => updateForm("apartmentType", type)}
              >
                {t(`apartments.${type}`)}
              </button>
            ))}
          </div>
          {errors.apartmentType ? <p className="field-error">{errors.apartmentType}</p> : null}

          <div className="toggle-row">
            <div>
              <strong>{t("create.apartment.duplexTitle")}</strong>
              <p>{t("create.apartment.duplexHint")}</p>
            </div>
            <button
              className={`switch${form.duplex ? " is-on" : ""}`}
              type="button"
              role="switch"
              aria-checked={form.duplex}
              onClick={() => updateForm("duplex", !form.duplex)}
            >
              <span />
            </button>
          </div>
        </section>

        <section className="form-section">
          <div className="section-heading">
            <span className="section-heading__number">{t("create.cleaning.step")}</span>
            <div><h2 id="cleaning-type-heading">{t("create.cleaning.title")}</h2></div>
          </div>
          <div className="cleaning-options" role="group" aria-labelledby="cleaning-type-heading">
            {(["REGULAR", "DEEP"] as CleaningType[]).map((type) => (
              <article
                key={type}
                className={`cleaning-card${form.cleaningType === type ? " is-selected" : ""}`}
              >
                <button
                  className="cleaning-card__select"
                  type="button"
                  aria-pressed={form.cleaningType === type}
                  onClick={() => updateForm("cleaningType", type)}
                >
                  <span className="cleaning-card__radio" aria-hidden="true" />
                  <span>
                    <strong>{t(`cleaning.${type}.title`)}</strong>
                    <small>{t(`cleaning.${type}.description`)}</small>
                  </span>
                  {form.apartmentType ? (
                    <b>
                      {formatPrice(
                        calculateDisplayedPrice(configuration, form.apartmentType, type, form.duplex),
                        configuration.currency,
                        locale,
                      )}
                    </b>
                  ) : null}
                </button>
                <button
                  className="text-button"
                  type="button"
                  onClick={() => setServiceInfo(type)}
                >
                  {t("create.cleaning.included")} <Icon name="arrow-right" size={14} />
                </button>
              </article>
            ))}
          </div>
          {errors.cleaningType ? <p className="field-error">{errors.cleaningType}</p> : null}
        </section>

        <section className="form-section">
          <div className="section-heading">
            <span className="section-heading__number">{t("create.details.step")}</span>
            <div><h2>{t("create.details.title")}</h2></div>
          </div>

          <div className="form-grid form-grid--cleaning-details">
            <div className="field field--date">
              <label htmlFor="requested-date">{t("create.details.date")}</label>
              <input
                id="requested-date"
                type="date"
                min={today}
                max={lastBookingDate}
                value={form.requestedDate}
                onChange={(event) => updateForm("requestedDate", event.target.value)}
                aria-invalid={Boolean(errors.requestedDate)}
                aria-describedby={`requested-date-hint${errors.requestedDate ? " requested-date-error" : ""}`}
              />
              <small id="requested-date-hint">{t("create.details.dateHint")}</small>
              {errors.requestedDate ? <p className="field-error" id="requested-date-error">{errors.requestedDate}</p> : null}
            </div>

            <div className="field field--address">
              <label htmlFor="address">{t("create.details.address")}</label>
              <input
                id="address"
                autoComplete="street-address"
                placeholder={t("create.details.addressPlaceholder")}
                value={form.address}
                onChange={(event) => updateForm("address", event.target.value)}
                aria-invalid={Boolean(errors.address)}
                aria-describedby={errors.address ? "address-error" : undefined}
              />
              {errors.address ? <p className="field-error" id="address-error">{errors.address}</p> : null}
            </div>

            <div className="field field--phone">
              <label htmlFor="phone">{t("create.details.phone")}</label>
              <input
                id="phone"
                type="tel"
                inputMode="tel"
                autoComplete="tel"
                maxLength={40}
                placeholder={t("create.details.phonePlaceholder")}
                value={form.phone}
                onChange={(event) => updateForm("phone", event.target.value)}
                aria-describedby={`phone-hint${errors.phone ? " phone-error" : ""}`}
                aria-invalid={Boolean(errors.phone)}
              />
              <small id="phone-hint">{t("create.details.phoneHint")}</small>
              {errors.phone ? <p className="field-error" id="phone-error">{errors.phone}</p> : null}
            </div>

            <div className="field field--comment">
              <label htmlFor="comment">
                {t("create.details.comment")} <span>{t("common.optional")}</span>
              </label>
              <textarea
                id="comment"
                rows={3}
                placeholder={t("create.details.commentPlaceholder")}
                value={form.comment}
                onChange={(event) => updateForm("comment", event.target.value)}
              />
            </div>

            {!rentalPromoCode ? <div className="field field--referral">
              <label htmlFor="referral-code">
                {t("create.details.referralCode")} <span>{t("common.optional")}</span>
              </label>
              <input
                id="referral-code"
                autoComplete="off"
                maxLength={32}
                placeholder={t("create.details.referralCodePlaceholder")}
                value={form.referralCode}
                onChange={(event) => updateForm("referralCode", event.target.value.toUpperCase())}
                aria-invalid={Boolean(errors.referralCode)}
                aria-describedby={`referral-code-hint${errors.referralCode ? " referral-code-error" : ""}`}
              />
              <small id="referral-code-hint">{t("create.details.referralCodeHint")}</small>
              {errors.referralCode ? <p className="field-error" id="referral-code-error">{errors.referralCode}</p> : null}
            </div> : null}
          </div>
        </section>

        <section className="price-summary">
          <div className="price-summary__top">
            <div>
              <span>{t("create.summary.title")}</span>
              {form.apartmentType && form.cleaningType ? (
                <p>
                  {t(`apartments.${form.apartmentType}`)} · {t(`cleaning.${form.cleaningType}.title`)}
                  {form.duplex ? ` · ${t("create.apartment.duplexTitle")}` : ""}
                </p>
              ) : <p>{t("create.summary.pending")}</p>}
            </div>
            {price !== null ? (
              <div className="price-summary__price">
                {quote && quote.customerDiscount > 0 ? (
                  <span className="price-summary__base-price">
                    {formatPrice(quote.basePrice, quote.currency, locale)}
                  </span>
                ) : null}
                <strong>{formatPrice(price, configuration.currency, locale)}</strong>
                <small>
                  {quote && quote.customerDiscount > 0
                    ? t("create.summary.discount", {
                        amount: formatPrice(quote.customerDiscount, quote.currency, locale),
                      })
                    : t("create.summary.fixed")}
                </small>
              </div>
            ) : null}
          </div>
          <div className="payment-note">
            <span><Icon name="wallet" size={17} /></span>
            <p>{t("create.summary.directPayment", { brand: getBrandName("cleaning") })}</p>
          </div>
          {rentalBenefitError ? (
            <p className="form-alert" role="alert">{t("create.rentalContext.notApplicable")}</p>
          ) : null}
          {submitError && submitError !== "rentalBenefit" ? (
            <p className="form-alert" role="alert">
              {t(
                submitError === "notificationAccess"
                  ? "create.notificationAccessRequired"
                  : "create.error",
              )}
            </p>
          ) : null}
          {authentication.current.authenticated ? (
            <button
              className="button button--primary button--full button--large"
              type="submit"
              disabled={isSubmitting || isQuoteLoading || price === null || quoteReferralError || rentalBenefitError}
            >
              {isSubmitting
                ? t("create.submitting")
                : price === null
                  ? t("create.summary.pending")
                  : t("create.submit", {
                      price: formatPrice(price, configuration.currency, locale),
                    })}
            </button>
          ) : <AuthenticationRequiredState compact />}
        </section>
      </form>

      {serviceInfo ? (
        <ServiceInfoDialog cleaningType={serviceInfo} onClose={() => setServiceInfo(null)} />
      ) : null}
    </div>
  );
}
