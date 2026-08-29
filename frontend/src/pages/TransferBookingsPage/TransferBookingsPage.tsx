import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useTransferApi } from "../../api/TransferApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { Icon } from "../../components/Icon/Icon";
import { TransferBookingStatus } from "../../components/TransferBookingStatus/TransferBookingStatus";
import type { TransferBooking } from "../../domain/transfer";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";

export function TransferBookingsPage() {
  const { t, i18n } = useTranslation();
  const api = useTransferApi();
  const [bookings, setBookings] = useState<TransferBooking[] | null>(null);
  const [failed, setFailed] = useState(false);
  const russian = i18n.resolvedLanguage?.startsWith("ru") ?? true;
  const locale = russian ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    api.getBookings()
      .then((result) => {
        if (active) setBookings(result);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api]);

  if (failed) return <ErrorState message={t("transfer.bookings.loadError")} />;
  if (!bookings) return <LoadingState />;

  return (
    <div className="page page--transfer-bookings">
      <header className="page-header">
        <span className="eyebrow"><BrandName service="transfer" /></span>
        <h1>{t("transfer.bookings.title")}</h1>
        <p>{t("transfer.bookings.subtitle")}</p>
      </header>
      {bookings.length === 0 ? (
        <section className="empty-state">
          <h2>{t("transfer.bookings.emptyTitle")}</h2>
          <p>{t("transfer.bookings.emptyText")}</p>
          <Link className="button button--primary" to="/transfer">{t("transfer.bookings.emptyAction")}</Link>
        </section>
      ) : (
        <div className="transfer-booking-list">
          {bookings.map((booking) => (
            <Link className="transfer-booking-card" key={booking.id} to={`/transfer/bookings/${booking.id}`}>
              <div className="transfer-booking-card__top"><span>#{booking.id}</span><TransferBookingStatus status={booking.status} /></div>
              <h2>{russian ? booking.airportNameRu : booking.airportNameEn}</h2>
              <p>{t(`transfer.direction.${booking.direction}`)} · {russian ? booking.vehicleNameRu : booking.vehicleNameEn}</p>
              <div className="transfer-booking-card__footer">
                <strong>{formatDate(booking.pickupDate, locale)} · {booking.pickupTime.slice(0, 5)}</strong>
                <b>{formatPrice(booking.priceAmount, booking.priceCurrency, locale)}</b>
                <Icon name="arrow-right" size={18} />
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
