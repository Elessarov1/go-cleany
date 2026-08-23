import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalBookingStatus } from "../../components/RentalBookingStatus/RentalBookingStatus";
import type { RentalBooking } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { formatDate, todayAsInputValue } from "../../utils/format";
import { rentalLanguage, rentalPropertyTitle } from "../../utils/rental";

interface BookingLocationState {
  justCreated?: boolean;
}

export function RentalBookingDetailsPage() {
  const { id } = useParams();
  const bookingId = Number(id);
  const location = useLocation();
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const [booking, setBooking] = useState<RentalBooking | null>(null);
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [cancelling, setCancelling] = useState(false);
  const [cancelError, setCancelError] = useState(false);
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";
  const justCreated = (location.state as BookingLocationState | null)?.justCreated === true;

  useEffect(() => {
    let active = true;
    setError(false);
    api.getBooking(bookingId)
      .then((value) => {
        if (active) setBooking(value);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, bookingId, reloadKey]);

  const cancel = async () => {
    if (!window.confirm(t("rental.bookingDetails.cancelConfirm"))) return;
    try {
      setCancelling(true);
      setCancelError(false);
      setBooking(await api.cancelBooking(bookingId));
    } catch {
      setCancelError(true);
    } finally {
      setCancelling(false);
    }
  };

  if (error || !Number.isFinite(bookingId)) {
    return <ErrorState message={t("rental.bookingDetails.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  }
  if (!booking) return <LoadingState />;
  const canCancel = booking.status === "CONFIRMED" && booking.checkInDate > todayAsInputValue();

  return (
    <div className="page page--rental-booking-details">
      <Link className="back-link" to="/rent/bookings"><Icon name="arrow-left" size={17} />{t("common.back")}</Link>

      {justCreated ? (
        <section className="rental-created-banner">
          <span><Icon name="check" size={25} /></span>
          <div><strong>{t("rental.bookingDetails.createdTitle")}</strong><p>{t("rental.bookingDetails.createdText")}</p></div>
        </section>
      ) : null}

      <header className="page-header page-header--compact">
        <span className="eyebrow">{t("rental.bookingDetails.eyebrow", { id: booking.id })}</span>
        <h1>{rentalPropertyTitle(booking.property, language)}</h1>
        <p>{booking.property.area}</p>
      </header>

      <section className="rental-booking-summary">
        <div className="rental-booking-summary__status">
          <span>{t("rental.bookingDetails.status")}</span>
          <RentalBookingStatus status={booking.status} />
        </div>
        <div className="rental-booking-summary__dates">
          <div><span>{t("rental.booking.checkIn")}</span><strong>{formatDate(booking.checkInDate, locale)}</strong></div>
          <Icon name="arrow-right" size={19} />
          <div><span>{booking.termType === "MONTHLY" ? t("rental.booking.expectedCheckOut") : t("rental.booking.checkOut")}</span><strong>{formatDate(booking.checkOutDate, locale)}</strong></div>
        </div>
        <dl className="detail-list">
          <div><dt>{t("rental.bookingDetails.duration")}</dt><dd>{booking.termType === "MONTHLY" ? t("rental.booking.months", { count: booking.rentalMonths }) : t("rental.booking.stay", { count: booking.durationDays })}</dd></div>
          <div><dt>{t("rental.bookingDetails.guests")}</dt><dd>{booking.guests}</dd></div>
          <div><dt>{t("rental.bookingDetails.phone")}</dt><dd>{booking.phone}</dd></div>
          <div><dt>{t("rental.bookingDetails.comment")}</dt><dd>{booking.comment || t("common.notProvided")}</dd></div>
          {booking.cancellationReason ? (
            <div><dt>{t("rental.bookingDetails.cancellationReason")}</dt><dd>{booking.cancellationReason}</dd></div>
          ) : null}
        </dl>
      </section>

      <section className="rental-price-snapshot">
        <div>
          <span>{booking.termType === "MONTHLY" ? t("rental.bookingDetails.monthlyPrice") : t("rental.bookingDetails.dailyPrice")}</span>
          <strong>{formatPrice(booking.termType === "MONTHLY" ? booking.monthlyPriceSnapshot! : booking.baseDailyPriceSnapshot, booking.currency, locale)}</strong>
        </div>
        {booking.discountAmount > 0 ? (
          <div><span>{t("rental.bookingDetails.discount")}</span><strong>−{formatPrice(booking.discountAmount, booking.currency, locale)}</strong></div>
        ) : null}
        <div className="rental-price-snapshot__total"><span>{t("rental.booking.total")}</span><strong>{formatPrice(booking.totalPrice, booking.currency, locale)}</strong></div>
      </section>

      {cancelError ? <p className="form-alert" role="alert">{t("rental.bookingDetails.cancelError")}</p> : null}
      {canCancel ? (
        <button className="button button--danger button--full" type="button" disabled={cancelling} onClick={() => void cancel()}>
          {cancelling ? t("rental.bookingDetails.cancelling") : t("rental.bookingDetails.cancel")}
        </button>
      ) : null}
    </div>
  );
}
