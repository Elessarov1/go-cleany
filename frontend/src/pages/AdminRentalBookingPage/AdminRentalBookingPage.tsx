import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalBookingStatus } from "../../components/RentalBookingStatus/RentalBookingStatus";
import type { AdminRentalBooking } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { formatDate, todayAsInputValue } from "../../utils/format";
import { rentalLanguage, rentalPropertyTitle } from "../../utils/rental";

export function AdminRentalBookingPage() {
  const { id } = useParams();
  const bookingId = Number(id);
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const [item, setItem] = useState<AdminRentalBooking | null>(null);
  const [reason, setReason] = useState("");
  const [keepDates, setKeepDates] = useState(false);
  const [error, setError] = useState(false);
  const [actionError, setActionError] = useState(false);
  const [pending, setPending] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setError(false);
    api.getAdminBooking(bookingId).then((value) => {
      if (active) setItem(value);
    }).catch(() => {
      if (active) setError(true);
    });
    return () => { active = false; };
  }, [api, bookingId, reloadKey]);

  const cancel = async () => {
    if (!window.confirm(t("adminRental.booking.cancelConfirm"))) return;
    try {
      setPending(true);
      setActionError(false);
      setItem(await api.cancelAdminBooking(bookingId, { reason: reason.trim() || null, keepDatesUnavailable: keepDates }));
    } catch {
      setActionError(true);
    } finally {
      setPending(false);
    }
  };
  const complete = async () => {
    if (!window.confirm(t("adminRental.booking.completeConfirm"))) return;
    try {
      setPending(true);
      setActionError(false);
      setItem(await api.completeAdminBooking(bookingId));
    } catch {
      setActionError(true);
    } finally {
      setPending(false);
    }
  };

  if (error || !Number.isFinite(bookingId)) return <ErrorState message={t("adminRental.booking.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  if (!item) return <LoadingState />;
  const booking = item.booking;

  return (
    <div className="page page--admin-rental">
      <Link className="back-link" to="/admin/rent/bookings"><Icon name="arrow-left" size={17} />{t("common.back")}</Link>
      <header className="admin-rental-header"><div><span className="eyebrow">go-rent / #{booking.id}</span><h1>{rentalPropertyTitle(booking.property, language)}</h1><p>{booking.property.area}</p></div><RentalBookingStatus status={booking.status} /></header>
      <div className="admin-rental-booking-details-grid">
        <section className="admin-rental-panel">
          <div className="admin-rental-section-heading"><div><h2>{t("adminRental.booking.stayTitle")}</h2></div></div>
          <dl className="detail-list">
            <div><dt>{t("rental.booking.checkIn")}</dt><dd>{formatDate(booking.checkInDate, locale)}</dd></div>
            <div><dt>{t("rental.booking.checkOut")}</dt><dd>{formatDate(booking.checkOutDate, locale)}</dd></div>
            <div><dt>{t("rental.bookingDetails.duration")}</dt><dd>{t("rental.booking.stay", { count: booking.durationDays })}</dd></div>
            <div><dt>{t("rental.bookingDetails.guests")}</dt><dd>{booking.guests}</dd></div>
            <div><dt>{t("rental.bookingDetails.comment")}</dt><dd>{booking.comment || t("common.notProvided")}</dd></div>
            {booking.cancellationReason ? <div><dt>{t("rental.bookingDetails.cancellationReason")}</dt><dd>{booking.cancellationReason}</dd></div> : null}
          </dl>
        </section>
        <section className="admin-rental-panel">
          <div className="admin-rental-section-heading"><div><h2>{t("adminRental.booking.customerTitle")}</h2></div></div>
          <dl className="detail-list">
            <div><dt>{t("adminRental.booking.customerName")}</dt><dd>{booking.customerName}</dd></div>
            <div><dt>{t("rental.bookingDetails.phone")}</dt><dd>{booking.phone}</dd></div>
            <div><dt>{t("adminRental.booking.customerId")}</dt><dd>{item.customerId}</dd></div>
            <div><dt>{t("adminRental.booking.communicationIdentityId")}</dt><dd>{item.communicationIdentityId}</dd></div>
          </dl>
        </section>
        <section className="admin-rental-panel">
          <div className="admin-rental-section-heading"><div><h2>{t("adminRental.booking.priceTitle")}</h2></div></div>
          <dl className="detail-list">
            <div><dt>{t("rental.bookingDetails.dailyPrice")}</dt><dd>{formatPrice(booking.baseDailyPriceSnapshot, booking.currency, locale)}</dd></div>
            <div><dt>{t("rental.bookingDetails.discount")}</dt><dd>{formatPrice(booking.discountAmount, booking.currency, locale)}</dd></div>
            <div><dt>{t("rental.booking.total")}</dt><dd><strong>{formatPrice(booking.totalPrice, booking.currency, locale)}</strong></dd></div>
            <div><dt>{t("adminRental.booking.createdAt")}</dt><dd>{new Intl.DateTimeFormat(locale, { dateStyle: "medium", timeStyle: "short" }).format(new Date(booking.createdAt))}</dd></div>
          </dl>
        </section>
      </div>
      {booking.status === "CONFIRMED" ? (
        <section className="admin-rental-panel admin-rental-booking-actions">
          <div className="admin-rental-section-heading"><div><h2>{t("adminRental.booking.actionsTitle")}</h2><p>{t("adminRental.booking.actionsText")}</p></div></div>
          <div className="field"><label><span>{t("adminRental.booking.reason")}</span><textarea maxLength={1000} value={reason} onChange={(event) => setReason(event.target.value)} /></label></div>
          <label className="admin-rental-checkbox"><input type="checkbox" checked={keepDates} onChange={(event) => setKeepDates(event.target.checked)} /><span>{t("adminRental.booking.keepDates")}</span></label>
          {actionError ? <p className="form-alert" role="alert">{t("adminRental.booking.actionError")}</p> : null}
          <div className="admin-rental-actions">
            <button className="button button--danger" type="button" disabled={pending} onClick={() => void cancel()}>{t("adminRental.booking.cancel")}</button>
            {booking.checkOutDate <= todayAsInputValue() ? <button className="button button--secondary" type="button" disabled={pending} onClick={() => void complete()}>{t("adminRental.booking.complete")}</button> : null}
          </div>
        </section>
      ) : null}
    </div>
  );
}
