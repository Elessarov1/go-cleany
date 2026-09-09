import { useEffect, useRef, useState, type FormEvent } from "react";
import { Link, useLocation, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ApiError } from "../../api/ApiError";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalCalendar } from "../../components/RentalCalendar/RentalCalendar";
import { RentalGallery } from "../../components/RentalGallery/RentalGallery";
import { RentalPricePresentation } from "../../components/RentalPricePresentation/RentalPricePresentation";
import type {
  RentalAvailability,
  RentalQuote,
  RentalConfiguration,
  RentalProperty,
  RentalTermCriteria,
  RentalTermType,
} from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import {
  addDaysToInputValue,
  addMonthsToInputValue,
  formatDate,
  inclusiveDaysBetween,
} from "../../utils/format";
import { rentalLanguage, rentalPropertyDescription, rentalPropertyTitle } from "../../utils/rental";
import { BrandName } from "../../components/BrandName/BrandName";
import { useAuthentication } from "../../api/AuthApiProvider";
import { AuthenticationRequiredState } from "../../components/CustomerAccessGate/CustomerAccessGate";
import {
  parseRentalSearchQuery,
  recalledRentalSearchExecution,
  rentalSearchKey,
  rentalSearchRequestFromDraft,
  serializeRentalSearch,
} from "../../utils/rentalSearch";

function bookingErrorMessage(
  error: unknown,
  t: (key: string, options?: Record<string, unknown>) => string,
  termType?: RentalTermType,
): string {
  if (!(error instanceof ApiError)) return t("rental.booking.errors.generic");
  if (error.code === "dates_not_available" && termType === "MONTHLY") {
    return t("rental.booking.errors.monthly_dates_not_available");
  }
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
  const authentication = useAuthentication();
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
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
  const [quote, setQuote] = useState<RentalQuote | null>(null);
  const [quoteLoading, setQuoteLoading] = useState(false);
  const [guests, setGuests] = useState(1);
  const [phone, setPhone] = useState("");
  const [comment, setComment] = useState("");
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [priceChanged, setPriceChanged] = useState(false);
  const [searchExecutionId, setSearchExecutionId] = useState<string | null>(() => {
    const state = location.state as { rentalSearchExecutionId?: string } | null;
    return state?.rentalSearchExecutionId ?? null;
  });
  const executionSourceKey = useRef<string | null>(null);
  const executionSourceWasBrowseAll = useRef(false);
  const [openedExecutionId, setOpenedExecutionId] = useState<string | null>(null);
  const query = searchParams.toString();
  const queryRef = useRef(query);
  queryRef.current = query;
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setLoadError(false);
    Promise.all([api.getProperty(slug ?? ""), api.getConfiguration()])
      .then(([loadedProperty, loadedConfiguration]) => {
        if (!active) return;
        setProperty(loadedProperty);
        setConfiguration(loadedConfiguration);
        const fromDate = loadedConfiguration.today;
        const toDate = addDaysToInputValue(
          loadedConfiguration.latestCheckInDate,
          loadedConfiguration.maxStayDays - 1,
        );
        void api.getAvailability(loadedProperty.id, fromDate, toDate)
          .then((value) => { if (active) setAvailability(value); })
          .catch(() => undefined);
      })
      .catch(() => {
        if (active) setLoadError(true);
      });
    return () => {
      active = false;
    };
  }, [api, slug, reloadKey]);

  useEffect(() => {
    if (!configuration) return;
    const parsed = parseRentalSearchQuery(new URLSearchParams(query), configuration);
    if (!parsed.applied) return;
    const applied = parsed.applied;
    const recalled = recalledRentalSearchExecution(applied);
    const appliedKey = rentalSearchKey(applied);
    const appliedIsBrowseAll = !("termType" in applied);
    setSearchExecutionId((current) => {
      if (recalled) {
        executionSourceKey.current = appliedKey;
        executionSourceWasBrowseAll.current = appliedIsBrowseAll;
        return recalled;
      }
      if (!current) return null;
      if (executionSourceKey.current === null) {
        executionSourceKey.current = appliedKey;
        executionSourceWasBrowseAll.current = appliedIsBrowseAll;
        return current;
      }
      if (executionSourceWasBrowseAll.current || executionSourceKey.current === appliedKey) {
        return current;
      }
      return null;
    });
    if (!("termType" in applied) || !applied.termType) return;
    setTermType(applied.termType);
    setCheckInDate(applied.checkInDate);
    setCheckOutDate(applied.termType === "DATE_RANGE" ? applied.checkOutDate : "");
    setMonths(applied.termType === "MONTHLY" ? applied.months : 1);
    setGuests(applied.guests);
  }, [configuration, query]);

  useEffect(() => {
    if (!property || !searchExecutionId || openedExecutionId === searchExecutionId) return;
    setOpenedExecutionId(searchExecutionId);
    void api.recordPropertyOpened(searchExecutionId).catch(() => undefined);
  }, [api, openedExecutionId, property, searchExecutionId]);

  useEffect(() => {
    if (!authentication.current.authenticated) {
      setPhone("");
      return;
    }
    let active = true;
    customerApi.getCurrentProfile()
      .then((profile) => {
        if (active && profile.phone) setPhone((current) => current || profile.phone || "");
      })
      .catch(() => undefined);
    return () => {
      active = false;
    };
  }, [authentication.current.authenticated, customerApi]);

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
    const request: RentalTermCriteria = termType === "DATE_RANGE"
      ? {
          termType,
          checkInDate,
          checkOutDate,
          guests,
        } as const
      : {
          termType,
          checkInDate,
          months,
          guests,
        } as const;
    api.quotePublic(property.id, request)
      .then((value) => {
        if (active) {
          setQuote(value);
          setPriceChanged(false);
          setCalendarError(null);
          const normalized = serializeRentalSearch(request).toString();
          if (normalized !== queryRef.current) {
            setSearchParams(serializeRentalSearch(request), { replace: true });
          }
        }
      })
      .catch((error) => {
        if (active) setCalendarError(bookingErrorMessage(error, t, termType));
      })
      .finally(() => {
        if (active) setQuoteLoading(false);
      });
    return () => {
      active = false;
    };
  }, [api, checkInDate, checkOutDate, guests, months, property, setSearchParams, t, termType]);

  const expectedCheckOutDate = termType === "MONTHLY" && checkInDate
    ? addDaysToInputValue(addMonthsToInputValue(checkInDate, months), -1)
    : checkOutDate;
  const selectedDays = termType === "DATE_RANGE" && checkInDate && checkOutDate
    ? inclusiveDaysBetween(checkInDate, checkOutDate)
    : null;
  const selectTermType = (nextTermType: RentalTermType) => {
    if (termType === nextTermType) return;
    setTermType(nextTermType);
    if (nextTermType === "MONTHLY") setCheckOutDate("");
    setMonths(1);
    setQuote(null);
    setCalendarError(null);
    setSubmitError(null);
  };

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!authentication.current.authenticated) return;
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
      const rentalRequest: RentalTermCriteria = termType === "DATE_RANGE"
        ? { termType, checkInDate, checkOutDate, guests }
        : { termType, checkInDate, months, guests };
      const contactDetails = {
        phone: phone.trim(),
        comment: comment.trim() || undefined,
        expectedTotalPrice: quote.price.totalPrice,
        expectedCurrency: quote.price.currency,
        searchExecutionId: searchExecutionId ?? undefined,
      };
      const booking = await api.createBooking({
        propertyId: property.id,
        ...rentalRequest,
        ...contactDetails,
      });
      navigate(`/rent/bookings/${booking.id}`, { state: { justCreated: true } });
    } catch (error) {
      if (error instanceof ApiError && error.code === "rental_price_changed" && property) {
        const request: RentalTermCriteria = termType === "DATE_RANGE"
          ? { termType, checkInDate, checkOutDate, guests }
          : { termType, checkInDate, months, guests };
        try {
          const refreshedQuote = await api.quotePublic(property.id, request);
          setQuote(refreshedQuote);
          setPriceChanged(true);
          setSubmitError(t("rental.booking.errors.rental_price_changed"));
        } catch (quoteError) {
          setSubmitError(bookingErrorMessage(quoteError, t, termType));
        }
      } else {
        setSubmitError(bookingErrorMessage(error, t, termType));
      }
    } finally {
      setSubmitting(false);
    }
  };

  if (loadError || !slug) {
    return <ErrorState message={t("rental.property.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  }
  if (!property || !configuration) return <LoadingState />;

  return (
    <div className="page page--rental-property">
      <Link className="back-link" to={`/rent${query ? `?${query}` : ""}`}><Icon name="arrow-left" size={17} />{t("rental.search.backToResults")}</Link>

      <RentalGallery
        media={property.media}
        propertyTitle={rentalPropertyTitle(property, language)}
      />

      <div className="rental-property-layout">
        <div className="rental-property-main">
          <header className="rental-property-heading">
            <span className="eyebrow"><BrandName service="rental" /> · {property.area}</span>
            <h1>{rentalPropertyTitle(property, language)}</h1>
            <p>{rentalPropertyDescription(property, language)}</p>
          </header>

          <section className="rental-facts" aria-label={t("rental.property.detailsTitle")}>
            <div><Icon name="bed" size={21} /><strong>{property.bedrooms}</strong><span>{t("rental.property.bedrooms")}</span></div>
            <div><Icon name="user" size={21} /><strong>{property.maxGuests}</strong><span>{t("rental.property.guests")}</span></div>
            <div><Icon name="home" size={21} /><strong>{property.areaSqm} м²</strong><span>{t("rental.property.area")}</span></div>
          </section>

          <section className="rental-section rental-property-details">
            <div className="rental-section__heading">
              <div><span className="eyebrow">01</span><h2>{t("rental.property.detailsTitle")}</h2></div>
            </div>
            <dl className="rental-detail-grid">
              <div><dt>{t("rental.property.beds")}</dt><dd>{property.beds}</dd></div>
              <div><dt>{t("rental.property.bathrooms")}</dt><dd>{property.bathrooms}</dd></div>
              <div><dt>{t("rental.property.floor")}</dt><dd>{property.floor}</dd></div>
              {property.apartmentNumber ? <div><dt>{t("rental.property.apartmentNumber")}</dt><dd>{property.apartmentNumber}</dd></div> : null}
              <div><dt>{t("rental.property.address")}</dt><dd>{property.address}</dd></div>
            </dl>
            <div className="rental-amenities">
              {property.amenities.map((amenity) => (
                <span key={amenity}><Icon name="check" size={14} />{t(`rental.amenities.${amenity}`)}</span>
              ))}
            </div>
          </section>
        </div>

        <form className="rental-booking-form" onSubmit={(event) => void submit(event)}>
        <section className="rental-section">
          <div className="rental-section__heading">
            <div><span className="eyebrow">02</span><h2>{t("rental.booking.termTitle")}</h2></div>
            <strong className="rental-daily-price">
              {quote?.criteria.termType === "MONTHLY" && quote.price.monthlyPrice !== null
                ? formatPrice(quote.price.monthlyPrice, quote.price.currency, locale)
                : formatPrice(property.baseDailyPrice!, property.currency!, locale)}
              <small>{quote?.criteria.termType === "MONTHLY" ? t("rental.common.perMonth") : t("rental.common.perDay")}</small>
            </strong>
          </div>
          <div className="rental-term-selector" role="radiogroup" aria-label={t("rental.booking.termTitle")}>
            <button
              className={termType === "DATE_RANGE" ? "is-selected" : ""}
              type="button"
              role="radio"
              aria-checked={termType === "DATE_RANGE"}
              onClick={() => selectTermType("DATE_RANGE")}
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
              onClick={() => selectTermType("MONTHLY")}
            >
              <Icon name="home" size={22} />
              <strong>{t("rental.booking.monthly")}</strong>
              <span>{t("rental.booking.monthlyHint")}</span>
            </button>
          </div>
          {termType === "DATE_RANGE" ? (
            <>
              <div className="rental-date-guidance" aria-live="polite">
                {!checkInDate ? (
                  <><span>1</span><div><strong>{t("rental.booking.chooseCheckIn")}</strong><p>{t("rental.booking.chooseCheckInHint")}</p></div></>
                ) : !checkOutDate ? (
                  <><Icon name="check" size={18} /><div><strong>{t("rental.booking.selectedCheckIn", { date: formatDate(checkInDate, locale) })}</strong><p>{t("rental.booking.chooseCheckOut")}</p></div></>
                ) : (
                  <><Icon name="check" size={18} /><div><strong>{t("rental.booking.rangeSelected")}</strong><p>{t("rental.booking.selectedDays", { count: selectedDays })}</p></div></>
                )}
              </div>
              <RentalCalendar
                configuration={configuration}
                unavailableRanges={availability?.unavailableRanges ?? []}
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
              {checkInDate ? (
                <button
                  className="text-button rental-date-reset"
                  type="button"
                  onClick={() => {
                    setCheckInDate("");
                    setCheckOutDate("");
                    setCalendarError(null);
                    setSubmitError(null);
                  }}
                >
                  {t("rental.booking.changeDates")}
                </button>
              ) : null}
            </>
          ) : (
            <div className="rental-monthly-selection">
              <div className="field">
                <label htmlFor="rental-monthly-start">{t("rental.booking.desiredStartDate")}</label>
                <input
                  id="rental-monthly-start"
                  type="date"
                  min={configuration.today}
                  max={configuration.latestCheckInDate}
                  value={checkInDate}
                  onChange={(event) => {
                    setCheckInDate(event.target.value);
                    setCheckOutDate("");
                    setCalendarError(null);
                    setSubmitError(null);
                  }}
                />
                <small>{t("rental.booking.monthlyStartHint")}</small>
              </div>
              <div className="rental-months-control">
                <span>{t("rental.booking.monthsLabel")}</span>
                <div>
                  <button
                    type="button"
                    aria-label={t("rental.booking.decreaseMonths")}
                    disabled={months <= 1}
                    onClick={() => {
                      setMonths((value) => Math.max(1, value - 1));
                      setCalendarError(null);
                      setSubmitError(null);
                    }}
                  >−</button>
                  <strong>{t("rental.booking.months", { count: months })}</strong>
                  <button
                    type="button"
                    aria-label={t("rental.booking.increaseMonths")}
                    onClick={() => {
                      setMonths((value) => value + 1);
                      setCalendarError(null);
                      setSubmitError(null);
                    }}
                  >+</button>
                </div>
              </div>
              <div className="rental-monthly-end" aria-live="polite">
                <Icon name="calendar-plus" size={20} />
                <div>
                  <span>{t("rental.booking.expectedCheckOut")}</span>
                  <strong>{expectedCheckOutDate ? formatDate(expectedCheckOutDate, locale) : "—"}</strong>
                  <small>{t("rental.booking.expectedCheckOutHint")}</small>
                </div>
              </div>
            </div>
          )}
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
            <RentalPricePresentation price={quote.price} locale={locale} />
          ) : <p>{termType === "MONTHLY" ? t("rental.booking.selectMonthlyStart") : t("rental.booking.selectDates")}</p>}
          {submitError ? <p className="form-alert" role="alert">{submitError}</p> : null}
          {authentication.current.authenticated ? (
            <>
              <button
                className="button button--primary button--full button--large"
                type="submit"
                disabled={!quote || quoteLoading || submitting}
              >
                {submitting
                  ? t("rental.booking.submitting")
                  : priceChanged
                    ? t("rental.booking.confirmNewPrice")
                    : t("rental.booking.confirm")}
              </button>
              <small className="rental-quote-card__note">{t("rental.booking.noPayment")}</small>
            </>
          ) : <AuthenticationRequiredState compact />}
        </section>
        </form>
      </div>
    </div>
  );
}
