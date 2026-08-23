import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ApiError } from "../../api/ApiError";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalCalendar } from "../../components/RentalCalendar/RentalCalendar";
import { RentalGallery } from "../../components/RentalGallery/RentalGallery";
import type {
  RentalAvailability,
  RentalBookingQuote,
  RentalConfiguration,
  RentalProperty,
  RentalTermType,
} from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { usePlatform } from "../../platform/PlatformProvider";
import { addDaysToInputValue, addMonthsToInputValue, formatDate, todayAsInputValue } from "../../utils/format";
import { rentalLanguage, rentalPropertyDescription, rentalPropertyTitle } from "../../utils/rental";
import { BrandName } from "../../components/BrandName/BrandName";

function bookingErrorMessage(error: unknown, t: (key: string, options?: Record<string, unknown>) => string): string {
  if (!(error instanceof ApiError)) return t("rental.booking.errors.generic");
  const knownCodes = new Set([
    "rental_min_stay_not_met",
    "rental_max_stay_exceeded",
    "rental_booking_horizon_exceeded",
    "dates_not_available",
    "rental_active_booking_limit_exceeded",
    "invalid_rental_booking",
    "invalid_phone_number",
  ]);
  return knownCodes.has(error.code ?? "")
    ? t(`rental.booking.errors.${error.code}`)
    : t("rental.booking.errors.generic");
}

export function RentalPropertyPage() {
  const { slug } = useParams();
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const customerApi = useCustomerApi();
  const platform = usePlatform();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [property, setProperty] = useState<RentalProperty | null>(null);
  const [configuration, setConfiguration] = useState<RentalConfiguration | null>(null);
  const [availability, setAvailability] = useState<RentalAvailability | null>(null);
  const [loadError, setLoadError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [termType, setTermType] = useState<RentalTermType>("DATE_RANGE");
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
  const [months, setMonths] = useState(1);
  const [calendarError, setCalendarError] = useState<string | null>(null);
  const [quote, setQuote] = useState<RentalBookingQuote | null>(null);
  const [quoteLoading, setQuoteLoading] = useState(false);
  const [guests, setGuests] = useState(1);
  const [phone, setPhone] = useState("");
  const [comment, setComment] = useState("");
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setLoadError(false);
    Promise.all([api.getProperty(slug ?? ""), api.getConfiguration()])
      .then(async ([loadedProperty, loadedConfiguration]) => {
        const fromDate = todayAsInputValue();
        const horizon = addMonthsToInputValue(
          fromDate,
          loadedConfiguration.bookingStartMonthsAhead,
        );
        const toDate = addDaysToInputValue(horizon, loadedConfiguration.maxStayDays);
        const loadedAvailability = await api.getAvailability(loadedProperty.id, fromDate, toDate);
        if (!active) return;
        setProperty(loadedProperty);
        setConfiguration(loadedConfiguration);
        setAvailability(loadedAvailability);
        const scenario = searchParams.get("scenario")?.toUpperCase();
        if (scenario === "RENT_MONTHLY" || scenario === "RENT_MONTHLY_UNAVAILABLE") {
          const previewCheckIn = addDaysToInputValue(
            fromDate,
            scenario === "RENT_MONTHLY_UNAVAILABLE" ? 40 : 80,
          );
          setTermType("MONTHLY");
          setCheckInDate(previewCheckIn);
          setCheckOutDate("");
          setMonths(scenario === "RENT_MONTHLY_UNAVAILABLE" ? 2 : 3);
        } else if (scenario === "RENT_DATE_RANGE") {
          const previewCheckIn = addDaysToInputValue(fromDate, 45);
          setTermType("DATE_RANGE");
          setCheckInDate(previewCheckIn);
          setCheckOutDate(addDaysToInputValue(previewCheckIn, 10));
        }
      })
      .catch(() => {
        if (active) setLoadError(true);
      });
    return () => {
      active = false;
    };
  }, [api, slug, reloadKey, searchParams]);

  useEffect(() => {
    let active = true;
    customerApi.getCurrentProfile()
      .then((profile) => {
        if (active && profile.phone) setPhone((current) => current || profile.phone || "");
      })
      .catch(() => undefined);
    return () => {
      active = false;
    };
  }, [customerApi]);

  useEffect(() => {
    const hasCompleteTerm = termType === "DATE_RANGE"
      ? Boolean(checkInDate && checkOutDate)
      : Boolean(checkInDate && months > 0);
    if (!property || !hasCompleteTerm) {
      setQuote(null);
      return;
    }
    let active = true;
    setQuoteLoading(true);
    setQuote(null);
    setSubmitError(null);
    const request = termType === "DATE_RANGE"
      ? {
          propertyId: property.id,
          termType,
          checkInDate,
          checkOutDate,
        } as const
      : {
          propertyId: property.id,
          termType,
          checkInDate,
          months,
        } as const;
    api.quoteBooking(request)
      .then((value) => {
        if (active) setQuote(value);
      })
      .catch((error) => {
        if (active) setCalendarError(bookingErrorMessage(error, t));
      })
      .finally(() => {
        if (active) setQuoteLoading(false);
      });
    return () => {
      active = false;
    };
  }, [api, checkInDate, checkOutDate, months, property, t, termType]);

  const expectedCheckOutDate = termType === "MONTHLY" && checkInDate
    ? addMonthsToInputValue(checkInDate, months)
    : checkOutDate;

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!property || !quote || !configuration) return;
    setSubmitError(null);
    if (guests < 1 || guests > (property.maxGuests ?? 0)) {
      setSubmitError(t("rental.booking.errors.guests"));
      return;
    }
    if (!/^\+\s*\d/.test(phone.trim())) {
      setSubmitError(t("rental.booking.errors.invalid_phone_number"));
      return;
    }
    try {
      setSubmitting(true);
      if (!await platform.ensureNotificationAccess()) {
        setSubmitError(t("rental.booking.errors.notificationAccess"));
        return;
      }
      const contactDetails = {
        guests,
        phone: phone.trim(),
        comment: comment.trim() || undefined,
      };
      const booking = termType === "DATE_RANGE"
        ? await api.createBooking({
            propertyId: property.id,
            termType,
            checkInDate,
            checkOutDate,
            ...contactDetails,
          })
        : await api.createBooking({
            propertyId: property.id,
            termType,
            checkInDate,
            months,
            ...contactDetails,
          });
      navigate(`/rent/bookings/${booking.id}`, { state: { justCreated: true } });
    } catch (error) {
      setSubmitError(bookingErrorMessage(error, t));
    } finally {
      setSubmitting(false);
    }
  };

  if (loadError || !slug) {
    return <ErrorState message={t("rental.property.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  }
  if (!property || !configuration || !availability) return <LoadingState />;

  return (
    <div className="page page--rental-property">
      <Link className="back-link" to="/rent"><Icon name="arrow-left" size={17} />{t("common.back")}</Link>

      <RentalGallery
        media={property.media}
        propertyTitle={rentalPropertyTitle(property, language)}
      />

      <header className="rental-property-heading">
        <span className="eyebrow"><BrandName service="rental" /> · {property.area}</span>
        <h1>{rentalPropertyTitle(property, language)}</h1>
        <p>{rentalPropertyDescription(property, language)}</p>
      </header>

      <section className="rental-facts">
        <div><Icon name="bed" size={21} /><strong>{property.bedrooms}</strong><span>{t("rental.property.bedrooms")}</span></div>
        <div><Icon name="user" size={21} /><strong>{property.maxGuests}</strong><span>{t("rental.property.guests")}</span></div>
        <div><Icon name="home" size={21} /><strong>{property.areaSqm} м²</strong><span>{t("rental.property.area")}</span></div>
      </section>

      <section className="rental-section">
        <div className="rental-section__heading">
          <div><span className="eyebrow">01</span><h2>{t("rental.property.detailsTitle")}</h2></div>
        </div>
        <dl className="rental-detail-grid">
          <div><dt>{t("rental.property.beds")}</dt><dd>{property.beds}</dd></div>
          <div><dt>{t("rental.property.bathrooms")}</dt><dd>{property.bathrooms}</dd></div>
          <div><dt>{t("rental.property.floor")}</dt><dd>{property.floor}</dd></div>
          <div><dt>{t("rental.property.address")}</dt><dd>{property.address}</dd></div>
        </dl>
        <div className="rental-amenities">
          {property.amenities.map((amenity) => (
            <span key={amenity}><Icon name="check" size={14} />{t(`rental.amenities.${amenity}`)}</span>
          ))}
        </div>
      </section>

      <form className="rental-booking-form" onSubmit={(event) => void submit(event)}>
        <section className="rental-section">
          <div className="rental-section__heading">
            <div><span className="eyebrow">02</span><h2>{t("rental.booking.termTitle")}</h2></div>
            <strong className="rental-daily-price">
              {quote?.termType === "MONTHLY" && quote.monthlyPrice !== null
                ? formatPrice(quote.monthlyPrice, quote.currency, locale)
                : formatPrice(property.baseDailyPrice!, property.currency!, locale)}
              <small>{quote?.termType === "MONTHLY" ? t("rental.common.perMonth") : t("rental.common.perDay")}</small>
            </strong>
          </div>
          <div className="rental-term-selector" role="radiogroup" aria-label={t("rental.booking.termTitle")}>
            <button
              className={termType === "DATE_RANGE" ? "is-selected" : ""}
              type="button"
              role="radio"
              aria-checked={termType === "DATE_RANGE"}
              onClick={() => {
                setTermType("DATE_RANGE");
                setCheckInDate("");
                setCheckOutDate("");
                setCalendarError(null);
              }}
            >
              <Icon name="calendar-plus" size={22} />
              <strong>{t("rental.booking.dateRange")}</strong>
              <span>{t("rental.booking.dateRangeHint", { min: configuration.minStayDays, max: configuration.longTermMinDays - 1 })}</span>
            </button>
            <button
              className={termType === "MONTHLY" ? "is-selected" : ""}
              type="button"
              role="radio"
              aria-checked={termType === "MONTHLY"}
              onClick={() => {
                setTermType("MONTHLY");
                setCheckInDate("");
                setCheckOutDate("");
                setCalendarError(null);
              }}
            >
              <Icon name="home" size={22} />
              <strong>{t("rental.booking.monthly")}</strong>
              <span>{t("rental.booking.monthlyHint")}</span>
            </button>
          </div>
          <h3 className="rental-booking-subtitle">
            {termType === "DATE_RANGE" ? t("rental.booking.datesTitle") : t("rental.booking.startDateTitle")}
          </h3>
          <RentalCalendar
            configuration={configuration}
            unavailableRanges={availability.unavailableRanges}
            checkInDate={checkInDate}
            checkOutDate={checkOutDate}
            selectionMode={termType === "MONTHLY" ? "START" : "RANGE"}
            onChange={(checkIn, checkOut) => {
              setCheckInDate(checkIn);
              setCheckOutDate(checkOut);
              setCalendarError(null);
              setSubmitError(null);
            }}
            onValidationError={setCalendarError}
          />
          {termType === "MONTHLY" ? (
            <div className="rental-months-control">
              <span>{t("rental.booking.monthsLabel")}</span>
              <div>
                <button type="button" aria-label={t("rental.booking.decreaseMonths")} disabled={months <= 1} onClick={() => setMonths((value) => Math.max(1, value - 1))}>−</button>
                <strong>{t("rental.booking.months", { count: months })}</strong>
                <button type="button" aria-label={t("rental.booking.increaseMonths")} onClick={() => setMonths((value) => value + 1)}>+</button>
              </div>
            </div>
          ) : null}
          <div className="rental-date-selection">
            <div><span>{t("rental.booking.checkIn")}</span><strong>{checkInDate ? formatDate(checkInDate, locale) : "—"}</strong></div>
            <Icon name="arrow-right" size={18} />
            <div><span>{termType === "MONTHLY" ? t("rental.booking.expectedCheckOut") : t("rental.booking.checkOut")}</span><strong>{expectedCheckOutDate ? formatDate(expectedCheckOutDate, locale) : "—"}</strong></div>
          </div>
          {calendarError ? <p className="form-alert" role="alert">{calendarError}</p> : null}
        </section>

        <section className="rental-section">
          <div className="rental-section__heading">
            <div><span className="eyebrow">03</span><h2>{t("rental.booking.contactsTitle")}</h2></div>
          </div>
          <div className="field">
            <label htmlFor="rental-guests">{t("rental.booking.guests")}</label>
            <input
              id="rental-guests"
              type="number"
              min="1"
              max={property.maxGuests ?? 1}
              value={guests}
              onChange={(event) => setGuests(Number(event.target.value))}
            />
          </div>
          <div className="field">
            <label htmlFor="rental-phone">{t("rental.booking.phone")}</label>
            <input
              id="rental-phone"
              type="tel"
              autoComplete="tel"
              maxLength={40}
              placeholder="+90 5xx xxx xx xx"
              value={phone}
              onChange={(event) => setPhone(event.target.value)}
            />
          </div>
          <div className="field">
            <label htmlFor="rental-comment">{t("rental.booking.comment")} <span>{t("common.optional")}</span></label>
            <textarea
              id="rental-comment"
              rows={3}
              maxLength={1000}
              value={comment}
              placeholder={t("rental.booking.commentPlaceholder")}
              onChange={(event) => setComment(event.target.value)}
            />
          </div>
        </section>

        <section className="rental-quote-card">
          {quoteLoading ? <span>{t("rental.booking.quoteLoading")}</span> : null}
          {quote ? (
            <>
              <div className="rental-quote-card__line">
                <span>{quote.termType === "MONTHLY"
                  ? t("rental.booking.months", { count: quote.rentalMonths })
                  : t("rental.booking.stay", { count: quote.durationDays })}</span>
                <strong>{formatPrice(quote.baseAmount, quote.currency, locale)}</strong>
              </div>
              {quote.termType === "MONTHLY" && quote.monthlyPrice !== null ? (
                <div className="rental-quote-card__line">
                  <span>{t("rental.booking.monthlyPrice")}</span>
                  <strong>{formatPrice(quote.monthlyPrice, quote.currency, locale)} {t("rental.common.perMonth")}</strong>
                </div>
              ) : null}
              {quote.longTermDiscountApplied ? (
                <div className="rental-quote-card__discount">
                  <span>{t("rental.booking.longTermDiscount", { percent: Math.round(quote.discountRate * 100) })}</span>
                  <strong>−{formatPrice(quote.discountAmount, quote.currency, locale)}</strong>
                </div>
              ) : null}
              <div className="rental-quote-card__total">
                <span>{t("rental.booking.total")}</span>
                <strong>{formatPrice(quote.totalPrice, quote.currency, locale)}</strong>
              </div>
            </>
          ) : <p>{termType === "MONTHLY" ? t("rental.booking.selectMonthlyStart") : t("rental.booking.selectDates")}</p>}
          {submitError ? <p className="form-alert" role="alert">{submitError}</p> : null}
          <button
            className="button button--primary button--full button--large"
            type="submit"
            disabled={!quote || quoteLoading || submitting}
          >
            {submitting ? t("rental.booking.submitting") : t("rental.booking.confirm")}
          </button>
          <small className="rental-quote-card__note">{t("rental.booking.noPayment")}</small>
        </section>
      </form>
    </div>
  );
}
