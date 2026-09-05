import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useTransferApi } from "../../api/TransferApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { TransferBookingStatus } from "../../components/TransferBookingStatus/TransferBookingStatus";
import type { AdminTransferDriver, TransferBooking } from "../../domain/transfer";
import { formatPrice } from "../../domain/pricing";

export function AdminTransferBookingPage() {
  const { id } = useParams();
  const { t, i18n } = useTranslation();
  const api = useTransferApi();
  const [booking, setBooking] = useState<TransferBooking | null>(null);
  const [drivers, setDrivers] = useState<AdminTransferDriver[]>([]);
  const [driverId, setDriverId] = useState(0);
  const [reason, setReason] = useState("");
  const [failed, setFailed] = useState(false);
  const [pending, setPending] = useState(false);
  const bookingId = Number(id);
  const russian = i18n.resolvedLanguage?.startsWith("ru") ?? true;
  const locale = russian ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    Promise.all([api.getAdminBooking(bookingId), api.getAdminDrivers()])
      .then(([result, driverList]) => {
        if (!active) return;
        setBooking(result);
        const enabled = driverList.filter((driver) => driver.enabled);
        setDrivers(enabled);
        setDriverId(enabled[0]?.id ?? 0);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api, bookingId]);

  async function action(operation: () => Promise<TransferBooking>) {
    setPending(true);
    setFailed(false);
    try {
      setBooking(await operation());
    } catch {
      setFailed(true);
    } finally {
      setPending(false);
    }
  }

  if (!booking && failed) return <ErrorState message={t("adminTransfer.booking.loadError")} />;
  if (!booking) return <LoadingState />;

  return (
    <div className="page page--admin-transfer-booking">
      <header className="page-header"><span className="eyebrow"><BrandName service="transfer" /> / #{booking.id}</span><h1>{t("adminTransfer.booking.title")}</h1><TransferBookingStatus status={booking.status} /></header>
      {failed ? <p className="form-error">{t("adminTransfer.booking.actionError")}</p> : null}
      <section className="transfer-booking-detail">
        <Detail label={t("adminTransfer.booking.customer")} value={booking.customerName} />
        <Detail label={t("transfer.booking.phone")} value={booking.phone} />
        <Detail label={t("transfer.booking.direction")} value={t(`transfer.direction.${booking.direction}`)} />
        <Detail label={t("transfer.booking.airport")} value={`${russian ? booking.airportNameRu : booking.airportNameEn} · ${booking.airportCode}`} />
        <Detail label={t("transfer.booking.dateTime")} value={`${booking.pickupDate} · ${booking.pickupTime.slice(0, 5)}`} />
        <Detail label={t("transfer.booking.vehicle")} value={russian ? booking.vehicleNameRu : booking.vehicleNameEn} />
        <Detail label={t("transfer.booking.address")} value={booking.address} wide />
        <Detail label={t("transfer.booking.passengers")} value={String(booking.passengerCount)} />
        <Detail label={t("transfer.booking.luggage")} value={String(booking.luggageCount)} />
        {booking.flightNumber ? <Detail label={t("transfer.booking.flight")} value={`${booking.flightNumber} · ${booking.scheduledArrivalTime?.slice(0, 5)}`} /> : null}
        {booking.discountAmount > 0 ? (
          <>
            <Detail label={t("transfer.booking.basePrice")} value={formatPrice(booking.basePriceAmount, booking.priceCurrency, locale)} />
            <Detail label={t("transfer.booking.discount")} value={`−${formatPrice(booking.discountAmount, booking.priceCurrency, locale)}`} />
            <Detail label={t("transfer.booking.payable")} value={formatPrice(booking.priceAmount, booking.priceCurrency, locale)} />
          </>
        ) : (
          <Detail label={t("transfer.booking.price")} value={formatPrice(booking.priceAmount, booking.priceCurrency, locale)} />
        )}
        <Detail label={t("transfer.booking.driver")} value={booking.driverName ?? t("common.notProvided")} />
        {booking.comment ? <Detail label={t("transfer.booking.comment")} value={booking.comment} wide /> : null}
      </section>
      <section className="admin-transfer-actions">
        {booking.status === "REQUESTED" ? (
          <div className="admin-transfer-assign"><label className="field"><span>{t("adminTransfer.booking.driver")}</span><select value={driverId} onChange={(event) => setDriverId(Number(event.target.value))}>{drivers.map((driver) => <option key={driver.id} value={driver.id}>{driver.name} · {driver.phone}</option>)}</select></label><button className="button button--primary" type="button" disabled={!driverId || pending} onClick={() => void action(() => api.assignAdminBooking(booking.id, driverId))}>{t("adminTransfer.booking.assign")}</button></div>
        ) : null}
        {(booking.status === "REQUESTED" || booking.status === "CONFIRMED") ? <label className="field"><span>{t("adminTransfer.booking.reason")}</span><textarea value={reason} onChange={(event) => setReason(event.target.value)} /></label> : null}
        <div className="page-actions">
          <Link className="button button--secondary" to="/admin/transfer/bookings">{t("common.back")}</Link>
          {booking.status === "REQUESTED" ? <button className="button button--danger" disabled={pending} type="button" onClick={() => void action(() => api.rejectAdminBooking(booking.id, reason))}>{t("adminTransfer.booking.reject")}</button> : null}
          {(booking.status === "REQUESTED" || booking.status === "CONFIRMED") ? <button className="button button--danger" disabled={pending} type="button" onClick={() => void action(() => api.cancelAdminBooking(booking.id, reason))}>{t("adminTransfer.booking.cancel")}</button> : null}
          {booking.status === "CONFIRMED" ? <button className="button button--primary" disabled={pending} type="button" onClick={() => void action(() => api.completeAdminBooking(booking.id))}>{t("adminTransfer.booking.complete")}</button> : null}
        </div>
      </section>
    </div>
  );
}

function Detail({ label, value, wide = false }: { label: string; value: string; wide?: boolean }) {
  return <div className={wide ? "is-wide" : ""}><small>{label}</small><strong>{value}</strong></div>;
}
