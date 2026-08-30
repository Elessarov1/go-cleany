import { type FormEvent, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useTransferApi } from "../../api/TransferApiProvider";
import { useCustomerApi } from "../../api/CustomerApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type {
  TransferConfiguration,
  TransferDirection,
  TransferVehicleType,
} from "../../domain/transfer";
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
  const navigate = useNavigate();
  const [configuration, setConfiguration] = useState<TransferConfiguration | null>(null);
  const [loadError, setLoadError] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState(false);
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
  const russian = i18n.resolvedLanguage?.startsWith("ru") ?? true;

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
    if (!vehicle) return;
    setPassengerCount((value) => Math.min(value, vehicle.maxPassengers));
    setLuggageCount((value) => Math.min(value, vehicle.maxLuggage));
  }, [vehicle]);

  async function submit(event: FormEvent) {
    event.preventDefault();
    if (!price || !vehicle) return;
    setSubmitting(true);
    setSubmitError(false);
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
      });
      navigate(`/transfer/bookings/${booking.id}`);
    } catch {
      setSubmitError(true);
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
                  onClick={() => setDirection(value)}
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
            <div><span>{t("transfer.summary.fixedPrice")}</span><strong>{price ? formatPrice(price.amount, price.currency, russian ? "ru-RU" : "en-GB") : "—"}</strong></div>
            <p>{t("transfer.summary.requested")}</p>
            {submitError ? <p className="form-error">{t("transfer.submitError")}</p> : null}
            <button className="button button--primary button--large button--full" disabled={!price || submitting} type="submit">
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
