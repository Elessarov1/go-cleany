import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useTransferApi } from "../../api/TransferApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { TransferBookingStatus } from "../../components/TransferBookingStatus/TransferBookingStatus";
import type { TransferBooking, TransferBookingStatus as Status } from "../../domain/transfer";

const STATUSES: Status[] = ["REQUESTED", "CONFIRMED", "COMPLETED", "CANCELLED", "REJECTED"];

export function AdminTransferBookingsPage() {
  const { t, i18n } = useTranslation();
  const api = useTransferApi();
  const [bookings, setBookings] = useState<TransferBooking[] | null>(null);
  const [status, setStatus] = useState<Status | "">("");
  const [date, setDate] = useState("");
  const [failed, setFailed] = useState(false);
  const russian = i18n.resolvedLanguage?.startsWith("ru") ?? true;

  useEffect(() => {
    let active = true;
    setFailed(false);
    api.getAdminBookings({ status: status || undefined, date: date || undefined })
      .then((result) => {
        if (active) setBookings(result);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api, date, status]);

  if (failed) return <ErrorState message={t("adminTransfer.bookings.loadError")} />;
  if (!bookings) return <LoadingState />;

  return (
    <div className="page page--admin-transfer">
      <header className="page-header admin-rental-header">
        <div><span className="eyebrow"><BrandName service="transfer" /> / admin</span><h1>{t("adminTransfer.bookings.title")}</h1><p>{t("adminTransfer.bookings.subtitle")}</p></div>
        <Link className="button button--secondary" to="/admin/transfer/configuration">{t("adminTransfer.configuration.open")}</Link>
      </header>
      <div className="admin-transfer-filters">
        <label className="field"><span>{t("adminTransfer.bookings.status")}</span><select value={status} onChange={(event) => setStatus(event.target.value as Status | "")}><option value="">{t("adminTransfer.bookings.all")}</option>{STATUSES.map((value) => <option key={value} value={value}>{t(`transfer.status.${value}`)}</option>)}</select></label>
        <label className="field"><span>{t("adminTransfer.bookings.date")}</span><input type="date" value={date} onChange={(event) => setDate(event.target.value)} /></label>
      </div>
      {bookings.length === 0 ? <section className="empty-state"><h2>{t("adminTransfer.bookings.empty")}</h2></section> : (
        <div className="admin-transfer-booking-list">
          {bookings.map((booking) => (
            <Link key={booking.id} to={`/admin/transfer/bookings/${booking.id}`} className="admin-transfer-booking-row">
              <span><b>#{booking.id}</b><small>{booking.pickupDate} · {booking.pickupTime.slice(0, 5)}</small></span>
              <span><strong>{russian ? booking.airportNameRu : booking.airportNameEn}</strong><small>{t(`transfer.direction.${booking.direction}`)} · {russian ? booking.vehicleNameRu : booking.vehicleNameEn}</small></span>
              <span><small>{booking.customerName}</small><small>{booking.phone}</small></span>
              <TransferBookingStatus status={booking.status} />
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
