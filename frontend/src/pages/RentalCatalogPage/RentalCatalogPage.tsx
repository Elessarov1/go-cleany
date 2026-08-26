import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { RentalConfiguration, RentalProperty } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { rentalCoverUrl, rentalLanguage, rentalPropertyDescription, rentalPropertyTitle } from "../../utils/rental";
import { BrandName } from "../../components/BrandName/BrandName";

export function RentalCatalogPage() {
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const [properties, setProperties] = useState<RentalProperty[] | null>(null);
  const [configuration, setConfiguration] = useState<RentalConfiguration | null>(null);
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setError(false);
    Promise.all([api.getProperties(), api.getConfiguration()])
      .then(([propertyList, rentalConfiguration]) => {
        if (!active) return;
        setProperties(propertyList);
        setConfiguration(rentalConfiguration);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey]);

  if (error) {
    return <ErrorState message={t("rental.catalog.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  }
  if (!properties || !configuration) return <LoadingState />;

  return (
    <div className="page page--rental-catalog">
      <header className="rental-hero">
        <div>
          <span className="eyebrow"><BrandName service="rental" /> · Alanya</span>
          <h1>{t("rental.catalog.title")}</h1>
          <p>{t("rental.catalog.subtitle")}</p>
        </div>
        <span className="rental-hero__mark" aria-hidden="true"><Icon name="building" size={42} /></span>
      </header>

      <div className="rental-policy-strip">
        <span><Icon name="moon" size={17} />{t("rental.catalog.minStay", { count: configuration.minStayDays })}</span>
        <span>{t("rental.catalog.longTerm", {
          count: configuration.longTermMinDays,
          percent: Math.round(configuration.longTermDiscountRate * 100),
        })}</span>
      </div>

      {properties.length === 0 ? (
        <section className="empty-state">
          <div className="empty-state__art"><Icon name="building" size={48} /></div>
          <h2>{t("rental.catalog.emptyTitle")}</h2>
          <p>{t("rental.catalog.emptyText")}</p>
        </section>
      ) : (
        <div className="rental-property-list">
          {properties.map((property) => {
            const coverUrl = rentalCoverUrl(property);
            const description = rentalPropertyDescription(property, language);
            return (
              <Link
                className="rental-property-card"
                key={property.id}
                to={`/rent/properties/${property.slug}`}
              >
                <div className="rental-property-card__image">
                  {coverUrl ? <img src={coverUrl} alt="" /> : <Icon name="building" size={42} />}
                  <span>{property.area}</span>
                </div>
                <div className="rental-property-card__body">
                  <div className="rental-property-card__copy">
                    <h2>{rentalPropertyTitle(property, language)}</h2>
                    <p className="rental-property-card__facts">
                      {t("rental.property.capacity", {
                        bedrooms: property.bedrooms,
                        guests: property.maxGuests,
                      })} · {property.areaSqm} м²
                    </p>
                    {description ? (
                      <p className="rental-property-card__description">
                        {description}
                      </p>
                    ) : null}
                  </div>
                  <div className="rental-property-card__price">
                    <strong>{formatPrice(property.baseDailyPrice!, property.currency!, locale)}</strong>
                    <span>{t("rental.common.perDay")}</span>
                    <Icon name="arrow-right" size={19} />
                  </div>
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}
