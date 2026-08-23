import { useEffect, useMemo, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ApiError } from "../../api/ApiError";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalCalendar } from "../../components/RentalCalendar/RentalCalendar";
import type {
  RentalAvailability,
  RentalBookingQuote,
  RentalConfiguration,
  RentalProperty,
} from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { usePlatform } from "../../platform/PlatformProvider";
import { addDaysToInputValue, addMonthsToInputValue, formatDate, todayAsInputValue } from "../../utils/format";
import { rentalLanguage, rentalPropertyDescription, rentalPropertyTitle } from "../../utils/rental";

function bookingErrorMessage(error: unknown, t: (key: string, options?: Record<string, unknown>) => string): string {
  if (!(error instanceof ApiError)) return t("rental.booking.errors.generic");
  const knownCodes = new Set([
    "rental_min_stay_not_met",
    "rental_max_stay_exceeded",
    "rental_booking_horizon_exceeded",
    "dates_not_available",
    "rental_active_booking_limit_exceeded",
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
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
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
        if (searchParams.get("scenario")?.toUpperCase() === "RENT_LONG_TERM") {
          const previewCheckIn = addDaysToInputValue(fromDate, 80);
          setCheckInDate(previewCheckIn);
          setCheckOutDate(addDaysToInputValue(previewCheckIn, 35));
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
    if (!property || !checkInDate || !checkOutDate) {
      setQuote(null);
      return;
    }
    let active = true;
    setQuoteLoading(true);
    setQuote(null);
    setSubmitError(null);
    api.quoteBooking({ propertyId: property.id, checkInDate, checkOutDate })
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
  }, [api, checkInDate, checkOutDate, property, t]);

  const sortedMedia = useMemo(
    () => [...(property?.media ?? [])].sort((first, second) => first.sortOrder - second.sortOrder),
    [property],
  );

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
      const booking = await api.createBooking({
        propertyId: property.id,
        checkInDate,
        checkOutDate,
        guests,
        phone: phone.trim(),
        comment: comment.trim() || undefined,
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

      <div className="rental-gallery" aria-label={t("rental.property.galleryLabel")}>
        {sortedMedia.map((media, index) => (
          <figure key={media.id}>
            <img src={media.url} alt={t("rental.property.photoAlt", { index: index + 1 })} />
            {media.cover ? <span>{t("rental.property.cover")}</span> : null}
          </figure>
        ))}
      </div>

      <header className="rental-property-heading">
        <span className="eyebrow">go-rent · {property.area}</span>
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
            <div><span className="eyebrow">02</span><h2>{t("rental.booking.datesTitle")}</h2></div>
            <strong className="rental-daily-price">
              {formatPrice(property.baseDailyPrice!, property.currency!, locale)}
              <small>{t("rental.common.perDay")}</small>
            </strong>
          </div>
          <RentalCalendar
            configuration={configuration}
            unavailableRanges={availability.unavailableRanges}
            checkInDate={checkInDate}
            checkOutDate={checkOutDate}
            onChange={(checkIn, checkOut) => {
              setCheckInDate(checkIn);
              setCheckOutDate(checkOut);
              setCalendarError(null);
              setSubmitError(null);
            }}
            onValidationError={setCalendarError}
          />
          <div className="rental-date-selection">
            <div><span>{t("rental.booking.checkIn")}</span><strong>{checkInDate ? formatDate(checkInDate, locale) : "—"}</strong></div>
            <Icon name="arrow-right" size={18} />
            <div><span>{t("rental.booking.checkOut")}</span><strong>{checkOutDate ? formatDate(checkOutDate, locale) : "—"}</strong></div>
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
                <span>{t("rental.booking.stay", { count: quote.durationDays })}</span>
                <strong>{formatPrice(quote.baseAmount, quote.currency, locale)}</strong>
              </div>
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
          ) : <p>{t("rental.booking.selectDates")}</p>}
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
