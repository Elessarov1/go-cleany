import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalBookingStatus } from "../../components/RentalBookingStatus/RentalBookingStatus";
import type { RentalBooking } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";
import { rentalLanguage, rentalPropertyTitle } from "../../utils/rental";

export function RentalBookingsPage() {
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const [bookings, setBookings] = useState<RentalBooking[] | null>(null);
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setError(false);
    api.getBookings()
      .then((items) => {
        if (active) setBookings(items);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey]);

  if (error) {
    return <ErrorState message={t("rental.bookings.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  }
  if (!bookings) return <LoadingState />;

  return (
    <div className="page page--rental-bookings">
      <header className="page-header">
        <span className="eyebrow">go-rent</span>
        <h1>{t("rental.bookings.title")}</h1>
        <p>{t("rental.bookings.subtitle")}</p>
      </header>

      {bookings.length === 0 ? (
        <section className="empty-state">
          <div className="empty-state__art"><Icon name="calendar-plus" size={44} /></div>
          <h2>{t("rental.bookings.emptyTitle")}</h2>
          <p>{t("rental.bookings.emptyText")}</p>
          <Link className="button button--primary" to="/rent">{t("rental.bookings.emptyAction")}</Link>
        </section>
      ) : (
        <div className="rental-booking-list">
          {bookings.map((booking) => (
            <Link className="rental-booking-card" key={booking.id} to={`/rent/bookings/${booking.id}`}>
              <div className="rental-booking-card__top">
                <span>#{booking.id}</span>
                <RentalBookingStatus status={booking.status} />
              </div>
              <h2>{rentalPropertyTitle(booking.property, language)}</h2>
              <p>{booking.property.area}</p>
              <div className="rental-booking-card__dates">
                <div><small>{t("rental.booking.checkIn")}</small><strong>{formatDate(booking.checkInDate, locale)}</strong></div>
                <Icon name="arrow-right" size={18} />
                <div><small>{t("rental.booking.checkOut")}</small><strong>{formatDate(booking.checkOutDate, locale)}</strong></div>
              </div>
              <div className="rental-booking-card__footer">
                <strong>{formatPrice(booking.totalPrice, booking.currency, locale)}</strong>
                <span>{t("rental.bookings.open")}<Icon name="arrow-right" size={15} /></span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
