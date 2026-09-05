import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useTransferApi } from "../../api/TransferApiProvider";
import { usePlatformCatalogApi } from "../../api/PlatformCatalogApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { TransferBookingStatus } from "../../components/TransferBookingStatus/TransferBookingStatus";
import type { TransferBooking } from "../../domain/transfer";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";
import { TransactionCarePanel } from "../../components/TransactionCarePanel/TransactionCarePanel";

export function TransferBookingDetailsPage() {
  const { id } = useParams();
  const { t, i18n } = useTranslation();
  const api = useTransferApi();
  const catalogApi = usePlatformCatalogApi();
  const [booking, setBooking] = useState<TransferBooking | null>(null);
  const [failed, setFailed] = useState(false);
  const [cancelling, setCancelling] = useState(false);
  const [repeatAvailable, setRepeatAvailable] = useState(false);
  const bookingId = Number(id);
  const russian = i18n.resolvedLanguage?.startsWith("ru") ?? true;
  const locale = russian ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    if (!Number.isInteger(bookingId) || bookingId <= 0) {
      setFailed(true);
      return;
    }
    api.getBooking(bookingId)
      .then((result) => {
        if (active) setBooking(result);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api, bookingId]);

  useEffect(() => {
    let active = true;
    setRepeatAvailable(false);
    if (booking?.status !== "COMPLETED") return () => { active = false; };
    catalogApi.getServices()
      .then((services) => {
        if (!active || !services.some((service) => service.service === "TRANSFER")) return;
        setRepeatAvailable(true);
        void api.recordRepeatShown(booking.id).catch(() => undefined);
      })
      .catch(() => undefined);
    return () => { active = false; };
  }, [api, booking, catalogApi]);

  async function cancel() {
    if (!booking) return;
    setCancelling(true);
    try {
      setBooking(await api.cancelBooking(booking.id));
    } catch {
      setFailed(true);
    } finally {
      setCancelling(false);
    }
  }

  if (failed) return <ErrorState message={t("transfer.booking.loadError")} />;
  if (!booking) return <LoadingState />;
  const canCancel = booking.status === "REQUESTED" || booking.status === "CONFIRMED";

  return (
    <div className="page page--transfer-booking">
      <header className="page-header transfer-booking-header">
        <span className="eyebrow"><BrandName service="transfer" /> · #{booking.id}</span>
        <h1>{t("transfer.booking.title")}</h1>
        <TransferBookingStatus status={booking.status} />
      </header>
      <section className="transfer-booking-detail">
        <Detail label={t("transfer.booking.direction")} value={t(`transfer.direction.${booking.direction}`)} />
        <Detail label={t("transfer.booking.airport")} value={`${russian ? booking.airportNameRu : booking.airportNameEn} · ${booking.airportCode}`} />
        <Detail label={t("transfer.booking.dateTime")} value={`${formatDate(booking.pickupDate, locale)} · ${booking.pickupTime.slice(0, 5)}`} />
        <Detail label={t("transfer.booking.vehicle")} value={russian ? booking.vehicleNameRu : booking.vehicleNameEn} />
        <Detail label={t("transfer.booking.address")} value={booking.address} wide />
        <Detail label={t("transfer.booking.passengers")} value={String(booking.passengerCount)} />
        <Detail label={t("transfer.booking.luggage")} value={String(booking.luggageCount)} />
        {booking.flightNumber ? <Detail label={t("transfer.booking.flight")} value={`${booking.flightNumber} · ${booking.scheduledArrivalTime?.slice(0, 5) ?? "—"}`} /> : null}
        <Detail label={t("transfer.booking.phone")} value={booking.phone} />
        {booking.discountAmount > 0 ? (
          <>
            <Detail label={t("transfer.booking.basePrice")} value={formatPrice(booking.basePriceAmount, booking.priceCurrency, locale)} />
            <Detail label={t("transfer.booking.discount")} value={`−${formatPrice(booking.discountAmount, booking.priceCurrency, locale)}`} />
            <Detail label={t("transfer.booking.payable")} value={formatPrice(booking.priceAmount, booking.priceCurrency, locale)} />
          </>
        ) : (
          <Detail label={t("transfer.booking.price")} value={formatPrice(booking.priceAmount, booking.priceCurrency, locale)} />
        )}
        {booking.driverName ? <Detail label={t("transfer.booking.driver")} value={booking.driverName} /> : null}
        {booking.comment ? <Detail label={t("transfer.booking.comment")} value={booking.comment} wide /> : null}
        {booking.statusReason ? <Detail label={t("transfer.booking.reason")} value={booking.statusReason} wide /> : null}
      </section>
      <p className="transfer-booking-note">{t("transfer.booking.requestNote")}</p>
      <TransactionCarePanel service="TRANSFER" sourceEntityId={booking.id} />
      <div className="page-actions">
        <Link className="button button--secondary" to="/transfer/bookings">{t("common.back")}</Link>
        {repeatAvailable ? (
          <Link className="button button--primary" to={`/transfer?repeatFrom=${booking.id}`}>
            {t("transfer.booking.repeat")}
          </Link>
        ) : null}
        {canCancel ? <button className="button button--danger" disabled={cancelling} type="button" onClick={() => void cancel()}>{t("transfer.booking.cancel")}</button> : null}
      </div>
    </div>
  );
}

function Detail({ label, value, wide = false }: { label: string; value: string; wide?: boolean }) {
  return <div className={wide ? "is-wide" : ""}><small>{label}</small><strong>{value}</strong></div>;
}
