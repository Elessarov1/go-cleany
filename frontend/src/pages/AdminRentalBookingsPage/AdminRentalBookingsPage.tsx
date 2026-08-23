import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { RentalBookingStatus as RentalBookingStatusBadge } from "../../components/RentalBookingStatus/RentalBookingStatus";
import type { AdminRentalBooking, RentalBookingStatus, RentalBookingTimeFilter, RentalProperty } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";
import { rentalLanguage, rentalPropertyTitle } from "../../utils/rental";

type StatusFilter = RentalBookingStatus | "ALL";

export function AdminRentalBookingsPage() {
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const [bookings, setBookings] = useState<AdminRentalBooking[] | null>(null);
  const [properties, setProperties] = useState<RentalProperty[]>([]);
  const [status, setStatus] = useState<StatusFilter>("ALL");
  const [propertyId, setPropertyId] = useState("ALL");
  const [time, setTime] = useState<RentalBookingTimeFilter>("FUTURE");
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setError(false);
    Promise.all([
      api.getAdminBookings({
        status: status === "ALL" ? undefined : status,
        propertyId: propertyId === "ALL" ? undefined : Number(propertyId),
        time,
      }),
      api.getAdminProperties(),
    ]).then(([bookingValues, propertyValues]) => {
      if (!active) return;
      setBookings(bookingValues);
      setProperties(propertyValues);
    }).catch(() => {
      if (active) setError(true);
    });
    return () => { active = false; };
  }, [api, propertyId, reloadKey, status, time]);

  if (error) return <ErrorState message={t("adminRental.bookings.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  if (!bookings) return <LoadingState />;

  return (
    <div className="page page--admin-rental">
      <header className="admin-rental-header"><div><span className="eyebrow">go-rent / admin</span><h1>{t("adminRental.bookings.title")}</h1><p>{t("adminRental.bookings.subtitle")}</p></div></header>
      <div className="admin-rental-toolbar">
        <Link className="admin-rental-toolbar__link" to="/admin/rent/properties"><Icon name="building" size={18} />{t("adminRental.nav.properties")}</Link>
        <Link className="admin-rental-toolbar__link is-active" to="/admin/rent/bookings"><Icon name="clipboard" size={18} />{t("adminRental.nav.bookings")}</Link>
      </div>
      <section className="admin-rental-panel admin-rental-filters">
        <div className="field"><label><span>{t("adminRental.bookings.statusFilter")}</span><select value={status} onChange={(event) => setStatus(event.target.value as StatusFilter)}><option value="ALL">{t("adminRental.filters.all")}</option>{(["CONFIRMED", "CANCELLED_BY_CUSTOMER", "CANCELLED_BY_ADMIN", "COMPLETED"] as RentalBookingStatus[]).map((item) => <option key={item} value={item}>{t(`rental.status.${item}`)}</option>)}</select></label></div>
        <div className="field"><label><span>{t("adminRental.bookings.propertyFilter")}</span><select value={propertyId} onChange={(event) => setPropertyId(event.target.value)}><option value="ALL">{t("adminRental.filters.allProperties")}</option>{properties.map((property) => <option key={property.id} value={property.id}>{rentalPropertyTitle(property, language) || `#${property.id}`}</option>)}</select></label></div>
        <div className="field"><label><span>{t("adminRental.bookings.timeFilter")}</span><select value={time} onChange={(event) => setTime(event.target.value as RentalBookingTimeFilter)}>{(["FUTURE", "PAST", "ALL"] as RentalBookingTimeFilter[]).map((item) => <option key={item} value={item}>{t(`adminRental.time.${item}`)}</option>)}</select></label></div>
      </section>
      {bookings.length === 0 ? <p className="admin-orders__empty">{t("adminRental.bookings.empty")}</p> : (
        <div className="admin-rental-booking-table">
          {bookings.map(({ booking }) => (
            <Link className="admin-rental-booking-row" key={booking.id} to={`/admin/rent/bookings/${booking.id}`}>
              <div><small>#{booking.id}</small><strong>{booking.customerName}</strong><span>{rentalPropertyTitle(booking.property, language)} · {booking.property.area}</span></div>
              <div className="admin-rental-booking-row__dates"><span>{formatDate(booking.checkInDate, locale)}</span><Icon name="arrow-right" size={15} /><span>{formatDate(booking.checkOutDate, locale)}</span></div>
              <div><RentalBookingStatusBadge status={booking.status} /><b>{formatPrice(booking.totalPrice, booking.currency, locale)}</b></div>
              <Icon name="arrow-right" size={18} />
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
