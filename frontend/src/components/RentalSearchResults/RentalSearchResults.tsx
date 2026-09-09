import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import type { RentalSearchRequest, RentalSearchResponse } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { rentalPropertyDescription, rentalPropertyTitle } from "../../utils/rental";
import { rentalSearchDetailQuery } from "../../utils/rentalSearch";
import { Icon } from "../Icon/Icon";
import { RentalPricePresentation } from "../RentalPricePresentation/RentalPricePresentation";

export function RentalSearchResults({ response, request, language, locale }: {
  response: RentalSearchResponse;
  request: RentalSearchRequest;
  language: "ru" | "en";
  locale: string;
}) {
  const { t } = useTranslation();
  const query = rentalSearchDetailQuery(request);
  if (response.properties.length === 0) {
    return (
      <section className="empty-state rental-search-empty">
        <div className="empty-state__art"><Icon name="calendar-plus" size={48} /></div>
        <h2>{t("rental.search.emptyTitle")}</h2>
        <p>{t("rental.search.emptyText")}</p>
      </section>
    );
  }
  return (
    <div className="rental-property-list" aria-live="polite">
      {response.properties.map((property) => (
        <Link
          className="rental-property-card"
          key={property.id}
          to={`/rent/properties/${property.slug}?${query}`}
          state={{ rentalSearchExecutionId: response.searchExecutionId }}
        >
          <div className="rental-property-card__image">
            {property.coverUrl ? <img src={property.coverUrl} alt="" /> : <Icon name="building" size={42} />}
            <span>{property.area}</span>
          </div>
          <div className="rental-property-card__body">
            <div className="rental-property-card__copy">
              <h2>{rentalPropertyTitle(property, language)}</h2>
              <p className="rental-property-card__facts">
                {t("rental.search.bedroomsSummary", { count: property.bedrooms })} · {t("rental.search.guestCapacity", { count: property.maxGuests })} · {property.areaSqm} m²
              </p>
              {rentalPropertyDescription(property, language) ? (
                <p className="rental-property-card__description">{rentalPropertyDescription(property, language)}</p>
              ) : null}
            </div>
            <div className="rental-property-card__price">
              {property.price ? (
                <RentalPricePresentation price={property.price} locale={locale} compact />
              ) : (
                <><strong>{formatPrice(property.baseDailyPrice, property.currency, locale)}</strong><span>{t("rental.common.perDay")}</span></>
              )}
              <Icon name="arrow-right" size={19} />
            </div>
          </div>
        </Link>
      ))}
    </div>
  );
}
