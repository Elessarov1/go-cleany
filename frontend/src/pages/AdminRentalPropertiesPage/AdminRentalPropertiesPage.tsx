import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { AdminRentalMediaImage } from "../../components/AdminRentalMediaImage/AdminRentalMediaImage";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { RentalProperty } from "../../domain/rental";
import { formatPrice } from "../../domain/pricing";
import { rentalLanguage, rentalPropertyTitle } from "../../utils/rental";
import { BrandName } from "../../components/BrandName/BrandName";
import { ConfirmationDialog } from "../../components/ConfirmationDialog/ConfirmationDialog";
import { RentalAdminNotificationPreference } from "../../components/RentalAdminNotificationPreference/RentalAdminNotificationPreference";

interface PropertyAction {
  type: "DELETE" | "UNPUBLISH";
  property: RentalProperty;
}

export function AdminRentalPropertiesPage() {
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const navigate = useNavigate();
  const [properties, setProperties] = useState<RentalProperty[] | null>(null);
  const [error, setError] = useState(false);
  const [creating, setCreating] = useState(false);
  const [propertyAction, setPropertyAction] = useState<PropertyAction | null>(null);
  const [actionPending, setActionPending] = useState(false);
  const [actionError, setActionError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const language = rentalLanguage(i18n.resolvedLanguage);
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setError(false);
    api.getAdminProperties().then((items) => {
      if (active) setProperties(items);
    }).catch(() => {
      if (active) setError(true);
    });
    return () => { active = false; };
  }, [api, reloadKey]);

  const createProperty = async () => {
    try {
      setCreating(true);
      const property = await api.createAdminProperty();
      navigate(`/admin/rent/properties/${property.id}`);
    } catch {
      setError(true);
    } finally {
      setCreating(false);
    }
  };

  const confirmPropertyAction = async () => {
    if (!propertyAction) return;
    try {
      setActionPending(true);
      setActionError(false);
      if (propertyAction.type === "UNPUBLISH") {
        const updated = await api.unpublishAdminProperty(propertyAction.property.id);
        setProperties((current) => current?.map((item) => item.id === updated.id ? updated : item) ?? null);
      } else {
        await api.deleteAdminProperty(propertyAction.property.id);
        setProperties((current) => current?.filter((item) => item.id !== propertyAction.property.id) ?? null);
      }
      setPropertyAction(null);
    } catch {
      setActionError(true);
      setPropertyAction(null);
    } finally {
      setActionPending(false);
    }
  };

  if (error) return <ErrorState message={t("adminRental.properties.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  if (!properties) return <LoadingState />;

  return (
    <div className="page page--admin-rental">
      <header className="admin-rental-header">
        <div><span className="eyebrow"><BrandName service="rental" /> / admin</span><h1>{t("adminRental.properties.title")}</h1><p>{t("adminRental.properties.subtitle")}</p></div>
        <button className="button button--primary" type="button" disabled={creating} onClick={() => void createProperty()}>
          {creating ? t("adminRental.properties.creating") : t("adminRental.properties.create")}
        </button>
      </header>
      <div className="admin-rental-toolbar">
        <Link className="admin-rental-toolbar__link is-active" to="/admin/rent/properties"><Icon name="building" size={18} />{t("adminRental.nav.properties")}</Link>
        <Link className="admin-rental-toolbar__link" to="/admin/rent/bookings"><Icon name="clipboard" size={18} />{t("adminRental.nav.bookings")}</Link>
      </div>
      <RentalAdminNotificationPreference />
      {actionError ? <p className="form-alert" role="alert">{t("adminRental.properties.actionError")}</p> : null}
      {properties.length === 0 ? <p className="admin-orders__empty">{t("adminRental.properties.empty")}</p> : (
        <div className="admin-rental-property-grid">
          {properties.map((property) => {
            const cover = property.media.find((item) => item.cover) ?? property.media[0];
            return (
              <article className="admin-rental-property-card" key={property.id}>
                <div className="admin-rental-property-card__image">
                  <AdminRentalMediaImage propertyId={property.id} mediaId={cover?.id} alt="" />
                  <span className={`admin-rental-status admin-rental-status--${property.status.toLowerCase()}`}>{t(`adminRental.propertyStatus.${property.status}`)}</span>
                </div>
                <div className="admin-rental-property-card__body">
                  <small>#{property.id} · {property.area || t("common.notProvided")}</small>
                  <h2>{rentalPropertyTitle(property, language) || t("adminRental.properties.untitled")}</h2>
                  <strong>{property.baseDailyPrice && property.currency ? formatPrice(property.baseDailyPrice, property.currency, locale) : "—"}</strong>
                  <div>
                    <Link className="button button--secondary" to={`/admin/rent/properties/${property.id}`}>{t("adminRental.properties.edit")}</Link>
                    {property.status === "DRAFT" ? (
                      <button className="button button--danger" type="button" onClick={() => setPropertyAction({ type: "DELETE", property })}>
                        {t("adminRental.properties.delete")}
                      </button>
                    ) : null}
                    {property.status === "PUBLISHED" ? (
                      <button className="button button--secondary" type="button" onClick={() => setPropertyAction({ type: "UNPUBLISH", property })}>
                        {t("adminRental.properties.unpublish")}
                      </button>
                    ) : null}
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      )}
      {propertyAction ? (
        <ConfirmationDialog
          title={t(`adminRental.properties.confirm.${propertyAction.type}.title`)}
          description={t(`adminRental.properties.confirm.${propertyAction.type}.description`)}
          confirmLabel={t(`adminRental.properties.confirm.${propertyAction.type}.action`)}
          pending={actionPending}
          destructive={propertyAction.type === "DELETE"}
          onCancel={() => setPropertyAction(null)}
          onConfirm={() => void confirmPropertyAction()}
        />
      ) : null}
    </div>
  );
}
