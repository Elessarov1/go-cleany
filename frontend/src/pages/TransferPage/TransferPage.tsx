import { type FormEvent, useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useTransferApi } from "../../api/TransferApiProvider";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { useRentalApi } from "../../api/RentalApiProvider";
import { ApiError } from "../../api/ApiError";
import { BrandName } from "../../components/BrandName/BrandName";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type {
  TransferConfiguration,
  TransferDirection,
  TransferRepeatPrefill,
  TransferQuote,
  TransferVehicleType,
} from "../../domain/transfer";
import type { RentalTransferContextType, RentalTransferPrefill } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";

function timeSlots(step: number): string[] {
  return Array.from({ length: 1440 / step }, (_, index) => {
    const minutes = index * step;
    return `${String(Math.floor(minutes / 60)).padStart(2, "0")}:${String(minutes % 60).padStart(2, "0")}`;
  });
}

export function TransferPage() {
  const { t, i18n } = useTranslation();
  const api = useTransferApi();
  const customerApi = useCustomerApi();
  const rentalApi = useRentalApi();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [configuration, setConfiguration] = useState<TransferConfiguration | null>(null);
  const [loadError, setLoadError] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<"generic" | "benefit" | null>(null);
  const [direction, setDirection] = useState<TransferDirection>("TO_AIRPORT");
  const [airportId, setAirportId] = useState(0);
  const [vehicleTypeId, setVehicleTypeId] = useState(0);
  const [pickupDate, setPickupDate] = useState("");
  const [pickupTime, setPickupTime] = useState("12:00");
  const [address, setAddress] = useState("");
  const [passengerCount, setPassengerCount] = useState(1);
  const [luggageCount, setLuggageCount] = useState(0);
  const [flightNumber, setFlightNumber] = useState("");
  const [scheduledArrivalTime, setScheduledArrivalTime] = useState("");
  const [phone, setPhone] = useState("");
  const [comment, setComment] = useState("");
  const [repeatContext, setRepeatContext] = useState<TransferRepeatPrefill | null>(null);
  const [repeatContextError, setRepeatContextError] = useState(false);
  const [rentalContext, setRentalContext] = useState<RentalTransferPrefill | null>(null);
  const [rentalContextError, setRentalContextError] = useState(false);
  const [rentalContextLoading, setRentalContextLoading] = useState(false);
  const [rentalContextReloadKey, setRentalContextReloadKey] = useState(0);
  const [quote, setQuote] = useState<TransferQuote | null>(null);
  const [quoteLoading, setQuoteLoading] = useState(false);
  const [quoteError, setQuoteError] = useState<"generic" | "benefit" | null>(null);
  const [quoteReloadKey, setQuoteReloadKey] = useState(0);
  const russian = i18n.resolvedLanguage?.startsWith("ru") ?? true;
  const rawRepeatSourceId = searchParams.get("repeatFrom");
  const parsedRepeatSourceId = rawRepeatSourceId === null ? null : Number(rawRepeatSourceId);
  const repeatSourceId = parsedRepeatSourceId !== null
    && Number.isSafeInteger(parsedRepeatSourceId)
    && parsedRepeatSourceId > 0
    ? parsedRepeatSourceId
    : null;
  const rawRentalBookingId = searchParams.get("rentalBooking");
  const parsedRentalBookingId = rawRentalBookingId === null ? null : Number(rawRentalBookingId);
  const rentalBookingId = parsedRentalBookingId !== null
    && Number.isSafeInteger(parsedRentalBookingId)
    && parsedRentalBookingId > 0
    ? parsedRentalBookingId
    : null;
  const rawRentalContext = searchParams.get("rentalContext");
  const rentalContextType = rawRentalContext === "ARRIVAL" || rawRentalContext === "CHECKOUT"
    ? rawRentalContext as RentalTransferContextType
    : null;

  useEffect(() => {
    let active = true;
    setLoadError(false);
    Promise.all([api.getConfiguration(), customerApi.getCurrentProfile()])
      .then(([result, profile]) => {
        if (!active) return;
        setConfiguration(result);
        setAirportId(result.airports[0]?.id ?? 0);
        setPickupDate(result.earliestBookingDate);
        setPhone(profile.phone ?? "");
      })
      .catch(() => {
        if (active) setLoadError(true);
      });
    return () => {
      active = false;
    };
  }, [api, customerApi]);

  useEffect(() => {
    setRepeatContext(null);
    setRepeatContextError(false);
    if (rawRepeatSourceId === null || !configuration || rawRentalBookingId !== null) return;
    if (repeatSourceId === null) {
      setRepeatContextError(true);
      return;
    }
    let active = true;
    api.getRepeatPrefill(repeatSourceId)
      .then((context) => {
        if (!active) return;
        const configuredAirportId = context.airportId !== null
          && configuration.airports.some((airport) => airport.id === context.airportId)
          && configuration.prices.some((price) => price.airportId === context.airportId
            && price.direction === context.direction)
          ? context.airportId
          : configuration.airports.find((airport) => configuration.prices.some((price) => (
            price.airportId === airport.id && price.direction === context.direction
          )))?.id ?? 0;
        const configuredVehicles = configuration.vehicleTypes.filter((candidate) => (
          configuration.prices.some((price) => price.airportId === configuredAirportId
            && price.vehicleTypeId === candidate.id
            && price.direction === context.direction)
        ));
        const configuredVehicle = configuredVehicles.find((candidate) => candidate.id === context.vehicleTypeId)
          ?? configuredVehicles[0];
        setRepeatContext(context);
        setDirection(context.direction);
        setAirportId(configuredAirportId);
        setVehicleTypeId(configuredVehicle?.id ?? 0);
        setAddress(context.address);
        setPassengerCount(Math.min(context.passengerCount, configuredVehicle?.maxPassengers ?? 1));
        setLuggageCount(Math.min(context.luggageCount, configuredVehicle?.maxLuggage ?? 0));
        setPickupDate("");
        setPickupTime("");
        setFlightNumber("");
        setScheduledArrivalTime("");
        setComment("");
      })
      .catch(() => {
        if (active) setRepeatContextError(true);
      });
    return () => { active = false; };
  }, [api, configuration, rawRentalBookingId, rawRepeatSourceId, repeatSourceId]);

  useEffect(() => {
    setRentalContext(null);
    setRentalContextError(false);
    setRentalContextLoading(false);
    if (rawRentalBookingId === null || !configuration) return;
    if (rentalBookingId === null || rentalContextType === null || rawRepeatSourceId !== null) {
      setRentalContextError(true);
      return;
    }
    let active = true;
    setRentalContextLoading(true);
    rentalApi.getTransferPrefill(rentalBookingId, rentalContextType)
      .then((context) => {
        if (!active) return;
        const configuredAirportId = configuration.airports.find((airport) => (
          configuration.prices.some((price) => price.airportId === airport.id
            && price.direction === context.direction)
        ))?.id ?? 0;
        const configuredVehicle = configuration.vehicleTypes.find((vehicle) => (
          configuration.prices.some((price) => price.airportId === configuredAirportId
            && price.vehicleTypeId === vehicle.id
            && price.direction === context.direction)
        ));
        setRentalContext(context);
        setDirection(context.direction);
        setAirportId(configuredAirportId);
        setVehicleTypeId(configuredVehicle?.id ?? 0);
        setPickupDate(context.suggestedDate);
        setPickupTime("");
        setAddress(context.address);
        setPassengerCount(1);
        setLuggageCount(0);
        setFlightNumber("");
        setScheduledArrivalTime("");
        setComment("");
      })
      .catch(() => {
        if (active) setRentalContextError(true);
      })
      .finally(() => {
        if (active) setRentalContextLoading(false);
      });
    return () => { active = false; };
  }, [
    configuration,
    rawRentalBookingId,
    rawRepeatSourceId,
    rentalApi,
    rentalBookingId,
    rentalContextReloadKey,
    rentalContextType,
  ]);

  const availableVehicles = useMemo(() => {
    if (!configuration || !airportId) return [];
    const ids = new Set(configuration.prices
      .filter((price) => price.airportId === airportId && price.direction === direction)
      .map((price) => price.vehicleTypeId));
    return configuration.vehicleTypes.filter((vehicle) => ids.has(vehicle.id));
  }, [airportId, configuration, direction]);

  useEffect(() => {
    if (!availableVehicles.some((vehicle) => vehicle.id === vehicleTypeId)) {
      setVehicleTypeId(availableVehicles[0]?.id ?? 0);
    }
  }, [availableVehicles, vehicleTypeId]);

  const vehicle = configuration?.vehicleTypes.find((item) => item.id === vehicleTypeId);
  const price = configuration?.prices.find((item) => item.airportId === airportId
    && item.vehicleTypeId === vehicleTypeId && item.direction === direction);

  useEffect(() => {
    setQuote(null);
    setQuoteError(null);
    setSubmitError(null);
    if (!price || !airportId || !vehicleTypeId || rentalContextLoading) return;
    let active = true;
    setQuoteLoading(true);
    api.quote({
      direction,
      airportId,
      vehicleTypeId,
      rentalSource: rentalContext ? {
        bookingId: rentalContext.rentalBookingId,
        context: rentalContext.context,
      } : undefined,
      benefit: rentalContext?.benefit?.type,
    })
      .then((result) => {
        if (active) setQuote(result);
      })
      .catch((error) => {
        if (!active) return;
        setQuoteError(error instanceof ApiError && error.code === "rental_transfer_benefit_unavailable"
          ? "benefit"
          : "generic");
      })
      .finally(() => {
        if (active) setQuoteLoading(false);
      });
    return () => { active = false; };
  }, [api, airportId, direction, price, quoteReloadKey, rentalContext, rentalContextLoading, vehicleTypeId]);

  useEffect(() => {
    if (!vehicle) return;
    setPassengerCount((value) => Math.min(value, vehicle.maxPassengers));
    setLuggageCount((value) => Math.min(value, vehicle.maxLuggage));
  }, [vehicle]);

  async function submit(event: FormEvent) {
    event.preventDefault();
    if (!quote || !vehicle) return;
    setSubmitting(true);
    setSubmitError(null);
    try {
      const booking = await api.createBooking({
        direction,
        airportId,
        vehicleTypeId,
        pickupDate,
        pickupTime,
        address,
        passengerCount,
        luggageCount,
        flightNumber: direction === "FROM_AIRPORT" ? flightNumber : null,
        scheduledArrivalTime: direction === "FROM_AIRPORT" ? scheduledArrivalTime : null,
        phone,
        comment: comment || null,
        repeatFromBookingId: repeatContext?.sourceBookingId,
        rentalSource: rentalContext ? {
          bookingId: rentalContext.rentalBookingId,
          context: rentalContext.context,
        } : undefined,
        benefit: rentalContext?.benefit?.type,
      });
      navigate(`/transfer/bookings/${booking.id}`);
    } catch (error) {
      if (error instanceof ApiError && error.code === "rental_transfer_benefit_unavailable") {
        setSubmitError("benefit");
        setQuote(null);
      } else {
        setSubmitError("generic");
      }
    } finally {
      setSubmitting(false);
    }
  }

  if (loadError) return <ErrorState message={t("transfer.loadError")} />;
  if (!configuration) return <LoadingState />;

  return (
    <div className="page page--transfer">
      <header className="transfer-hero">
        <span className="eyebrow"><BrandName service="transfer" /></span>
        <h1>{t("transfer.title")}</h1>
        <p>{t("transfer.subtitle")}</p>
      </header>

      {repeatContext ? (
        <section className="repeat-context-card transfer-repeat-context">
          <span><Icon name="car" size={22} /></span>
          <div>
            <strong>{t("transfer.repeatContext.title", { id: repeatContext.sourceBookingId })}</strong>
            <p>{t("transfer.repeatContext.text")}</p>
          </div>
        </section>
      ) : null}
      {repeatContextError ? <p className="form-alert">{t("transfer.repeatContext.error")}</p> : null}
      {rentalContext ? (
        <section className="repeat-context-card transfer-rental-context">
          <span><Icon name="home" size={22} /></span>
          <div>
            <strong>{t("transfer.rentalContext.title", { id: rentalContext.rentalBookingId })}</strong>
            <p>{t(`transfer.rentalContext.${rentalContext.context}`)}</p>
            {rentalContext.benefit ? (
              <small>{t("transfer.rentalContext.benefit", {
                percent: Math.round(rentalContext.benefit.discountRate * 100),
              })}</small>
            ) : null}
          </div>
        </section>
      ) : null}
      {rentalContextError ? <p className="form-alert">{t("transfer.rentalContext.error")}</p> : null}

      {configuration.prices.length === 0 ? (
        <section className="empty-state transfer-unavailable">
          <h2>{t("transfer.unavailableTitle")}</h2>
          <p>{t("transfer.unavailableText")}</p>
        </section>
      ) : (
        <form className="transfer-form" onSubmit={(event) => void submit(event)}>
          <section className="transfer-panel">
            <div className="transfer-panel__heading"><span>01</span><h2>{t("transfer.route.title")}</h2></div>
            <div className="transfer-direction" role="group" aria-label={t("transfer.route.direction")}>
              {(["TO_AIRPORT", "FROM_AIRPORT"] as const).map((value) => (
                <button
                  className={direction === value ? "is-selected" : ""}
                  key={value}
                  type="button"
                  onClick={() => {
                    if (rentalContext && rentalContext.direction !== value) setRentalContext(null);
                    setDirection(value);
                  }}
                >
                  {t(`transfer.direction.${value}`)}
                </button>
              ))}
            </div>
            <label className="field">
              <span>{t("transfer.route.airport")}</span>
              <select value={airportId} onChange={(event) => setAirportId(Number(event.target.value))} required>
                {configuration.airports.map((airport) => (
                  <option key={airport.id} value={airport.id}>
                    {russian ? airport.nameRu : airport.nameEn} · {airport.code}
                  </option>
                ))}
              </select>
            </label>
          </section>

          <section className="transfer-panel">
            <div className="transfer-panel__heading"><span>02</span><h2>{t("transfer.vehicle.title")}</h2></div>
            <div className="transfer-vehicles">
              {availableVehicles.map((item) => (
                <VehicleCard
                  key={item.id}
                  vehicle={item}
                  selected={item.id === vehicleTypeId}
                  price={configuration.prices.find((candidate) => candidate.airportId === airportId
                    && candidate.vehicleTypeId === item.id && candidate.direction === direction)?.amount}
                  currency={configuration.prices.find((candidate) => candidate.airportId === airportId
                    && candidate.vehicleTypeId === item.id && candidate.direction === direction)?.currency}
                  russian={russian}
                  onSelect={() => setVehicleTypeId(item.id)}
                />
              ))}
            </div>
            {availableVehicles.length === 0 ? <p className="form-error">{t("transfer.vehicle.unavailable")}</p> : null}
          </section>

          <section className="transfer-panel">
            <div className="transfer-panel__heading"><span>03</span><h2>{t("transfer.details.title")}</h2></div>
            <div className="transfer-form-grid">
              <label className="field transfer-form-grid__date">
                <span>{t("transfer.details.date")}</span>
                <input type="date" min={configuration.earliestBookingDate} max={configuration.latestBookingDate} value={pickupDate} onChange={(event) => setPickupDate(event.target.value)} required />
              </label>
              <label className="field">
                <span>{t("transfer.details.time")}</span>
                <select value={pickupTime} onChange={(event) => setPickupTime(event.target.value)} required>
                  <option value="" disabled>{t("transfer.details.selectTime")}</option>
                  {timeSlots(configuration.timeSlotMinutes).map((value) => <option key={value}>{value}</option>)}
                </select>
              </label>
              <label className="field transfer-form-grid__wide">
                <span>{t(direction === "TO_AIRPORT" ? "transfer.details.pickupAddress" : "transfer.details.destinationAddress")}</span>
                <input value={address} maxLength={1000} onChange={(event) => setAddress(event.target.value)} placeholder={t("transfer.details.addressPlaceholder")} required />
              </label>
              <label className="field">
                <span>{t("transfer.details.passengers")}</span>
                <input type="number" min={1} max={vehicle?.maxPassengers ?? 1} value={passengerCount} onChange={(event) => setPassengerCount(Number(event.target.value))} required />
              </label>
              <label className="field">
                <span>{t("transfer.details.luggage")}</span>
                <input type="number" min={0} max={vehicle?.maxLuggage ?? 0} value={luggageCount} onChange={(event) => setLuggageCount(Number(event.target.value))} required />
              </label>
              {direction === "FROM_AIRPORT" ? (
                <>
                  <label className="field">
                    <span>{t("transfer.details.flight")}</span>
                    <input value={flightNumber} maxLength={64} onChange={(event) => setFlightNumber(event.target.value)} placeholder="TK 123" required />
                  </label>
                  <label className="field">
                    <span>{t("transfer.details.arrivalTime")}</span>
                    <input type="time" value={scheduledArrivalTime} onChange={(event) => setScheduledArrivalTime(event.target.value)} required />
                  </label>
                </>
              ) : null}
              <label className="field transfer-form-grid__wide">
                <span>{t("transfer.details.phone")}</span>
                <input type="tel" value={phone} maxLength={40} onChange={(event) => setPhone(event.target.value)} placeholder="+90 5xx xxx xx xx" required />
              </label>
              <label className="field transfer-form-grid__wide">
                <span>{t("transfer.details.comment")} <small>{t("common.optional")}</small></span>
                <textarea value={comment} maxLength={1000} onChange={(event) => setComment(event.target.value)} />
              </label>
            </div>
          </section>

          <section className="transfer-summary">
            {quote?.appliedBenefit ? (
              <div className="transfer-summary__benefit">
                <div><span>{t("transfer.summary.basePrice")}</span><strong>{formatPrice(quote.baseAmount, quote.currency, russian ? "ru-RU" : "en-GB")}</strong></div>
                <div><span>{t("transfer.summary.discount", { percent: Math.round((rentalContext?.benefit?.discountRate ?? 0) * 100) })}</span><strong>−{formatPrice(quote.discountAmount, quote.currency, russian ? "ru-RU" : "en-GB")}</strong></div>
              </div>
            ) : null}
            <div><span>{t(quote?.appliedBenefit ? "transfer.summary.payable" : "transfer.summary.fixedPrice")}</span><strong>{quote ? formatPrice(quote.payableAmount, quote.currency, russian ? "ru-RU" : "en-GB") : "—"}</strong></div>
            <p>{t("transfer.summary.requested")}</p>
            {quoteLoading ? <p className="transfer-summary__quote-state">{t("transfer.summary.calculating")}</p> : null}
            {quoteError === "generic" ? (
              <p className="form-error">
                {t("transfer.summary.quoteError")}{" "}
                <button type="button" className="inline-action" onClick={() => setQuoteReloadKey((key) => key + 1)}>{t("common.retry")}</button>
              </p>
            ) : null}
            {quoteError === "benefit" ? (
              <p className="form-error">
                {t("transfer.benefitUnavailable")}{" "}
                <button
                  type="button"
                  className="inline-action"
                  onClick={() => setRentalContextReloadKey((key) => key + 1)}
                >
                  {t("transfer.refreshQuote")}
                </button>
              </p>
            ) : null}
            {submitError === "benefit" ? (
              <p className="form-error">
                {t("transfer.benefitUnavailable")}{" "}
                <button
                  type="button"
                  className="inline-action"
                  onClick={() => {
                    setSubmitError(null);
                    setRentalContextReloadKey((key) => key + 1);
                  }}
                >
                  {t("transfer.refreshQuote")}
                </button>
              </p>
            ) : null}
            {submitError === "generic" ? <p className="form-error">{t("transfer.submitError")}</p> : null}
            <button className="button button--primary button--large button--full" disabled={!quote || quoteLoading || rentalContextLoading || submitting} type="submit">
              {submitting ? t("transfer.submitting") : t("transfer.submit")}
            </button>
          </section>
        </form>
      )}
    </div>
  );
}

interface VehicleCardProps {
  vehicle: TransferVehicleType;
  selected: boolean;
  price?: number;
  currency?: string;
  russian: boolean;
  onSelect(): void;
}

function VehicleCard({ vehicle, selected, price, currency, russian, onSelect }: VehicleCardProps) {
  const { t } = useTranslation();
  return (
    <button className={`transfer-vehicle${selected ? " is-selected" : ""}`} type="button" onClick={onSelect}>
      <span><strong>{russian ? vehicle.nameRu : vehicle.nameEn}</strong><small>{vehicle.code}</small></span>
      <span className="transfer-vehicle__capacity">
        {t("transfer.vehicle.capacity", { passengers: vehicle.maxPassengers, luggage: vehicle.maxLuggage })}
      </span>
      <b>{price && currency ? formatPrice(price, currency, russian ? "ru-RU" : "en-GB") : "—"}</b>
    </button>
  );
}
