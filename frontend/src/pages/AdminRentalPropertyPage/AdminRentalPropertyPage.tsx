import { useEffect, useState, type ChangeEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { AdminRentalMediaImage } from "../../components/AdminRentalMediaImage/AdminRentalMediaImage";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { RentalAmenity, RentalProperty, UpdateRentalPropertyRequest } from "../../domain/rental";

const amenities: RentalAmenity[] = [
  "WIFI", "AIR_CONDITIONING", "WASHING_MACHINE", "DISHWASHER", "BALCONY", "SEA_VIEW",
  "POOL", "PARKING", "ELEVATOR", "WORKSPACE", "TV", "KITCHEN",
];

type NullableTextKey = "slug" | "titleRu" | "titleEn" | "descriptionRu" | "descriptionEn" | "area" | "address" | "currency";
type NullableNumberKey = "bedrooms" | "beds" | "bathrooms" | "maxGuests" | "areaSqm" | "floor" | "baseDailyPrice";

function toUpdateRequest(property: RentalProperty): UpdateRentalPropertyRequest {
  return {
    slug: property.slug, titleRu: property.titleRu, titleEn: property.titleEn,
    descriptionRu: property.descriptionRu, descriptionEn: property.descriptionEn,
    area: property.area, address: property.address, bedrooms: property.bedrooms,
    beds: property.beds, bathrooms: property.bathrooms, maxGuests: property.maxGuests,
    areaSqm: property.areaSqm, floor: property.floor, baseDailyPrice: property.baseDailyPrice,
    currency: property.currency?.toUpperCase() ?? null, amenities: property.amenities,
  };
}

export function AdminRentalPropertyPage() {
  const { id } = useParams();
  const propertyId = Number(id);
  const { t } = useTranslation();
  const api = useRentalApi();
  const [property, setProperty] = useState<RentalProperty | null>(null);
  const [error, setError] = useState(false);
  const [actionError, setActionError] = useState(false);
  const [pending, setPending] = useState(false);
  const [saved, setSaved] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let active = true;
    setError(false);
    api.getAdminProperty(propertyId).then((value) => {
      if (active) setProperty(value);
    }).catch(() => {
      if (active) setError(true);
    });
    return () => { active = false; };
  }, [api, propertyId, reloadKey]);

  const changeText = (key: NullableTextKey, value: string) => {
    setSaved(false);
    setProperty((current) => current ? { ...current, [key]: value.trimStart() || null } : current);
  };
  const changeNumber = (key: NullableNumberKey, value: string) => {
    setSaved(false);
    setProperty((current) => current ? { ...current, [key]: value === "" ? null : Number(value) } : current);
  };
  const toggleAmenity = (amenity: RentalAmenity) => {
    setSaved(false);
    setProperty((current) => current ? {
      ...current,
      amenities: current.amenities.includes(amenity)
        ? current.amenities.filter((item) => item !== amenity)
        : [...current.amenities, amenity],
    } : current);
  };

  const runPropertyAction = async (action: () => Promise<RentalProperty>) => {
    try {
      setPending(true);
      setActionError(false);
      setProperty(await action());
      setSaved(true);
    } catch {
      setActionError(true);
    } finally {
      setPending(false);
    }
  };

  const save = () => property && runPropertyAction(() => api.updateAdminProperty(propertyId, toUpdateRequest(property)));
  const publish = () => property && runPropertyAction(async () => {
    await api.updateAdminProperty(propertyId, toUpdateRequest(property));
    return api.publishAdminProperty(propertyId);
  });
  const upload = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = "";
    if (file && property) void runPropertyAction(() => api.addAdminPropertyMedia(propertyId, file, property.media.length === 0));
  };
  const moveMedia = (mediaId: number, direction: -1 | 1) => {
    if (!property) return;
    const ids = property.media.map((item) => item.id);
    const index = ids.indexOf(mediaId);
    const target = index + direction;
    if (index < 0 || target < 0 || target >= ids.length) return;
    [ids[index], ids[target]] = [ids[target]!, ids[index]!];
    void runPropertyAction(() => api.reorderAdminPropertyMedia(propertyId, ids));
  };

  if (error || !Number.isFinite(propertyId)) return <ErrorState message={t("adminRental.editor.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  if (!property) return <LoadingState />;

  const textField = (key: NullableTextKey, textarea = false) => (
    <div className="field">
      <label><span>{t(`adminRental.editor.fields.${key}`)}</span>
        {textarea
          ? <textarea value={property[key] ?? ""} onChange={(event) => changeText(key, event.target.value)} />
          : <input value={property[key] ?? ""} onChange={(event) => changeText(key, event.target.value)} />}
      </label>
    </div>
  );
  const numberField = (key: NullableNumberKey, step = "1") => (
    <div className="field">
      <label><span>{t(`adminRental.editor.fields.${key}`)}</span>
        <input type="number" step={step} value={property[key] ?? ""} onChange={(event) => changeNumber(key, event.target.value)} />
      </label>
    </div>
  );

  return (
    <div className="page page--admin-rental">
      <Link className="back-link" to="/admin/rent/properties"><Icon name="arrow-left" size={17} />{t("common.back")}</Link>
      <header className="admin-rental-header admin-rental-header--editor">
        <div>
          <span className="eyebrow">go-rent / #{property.id}</span>
          <h1>{property.titleRu || property.titleEn || t("adminRental.properties.untitled")}</h1>
          <p><span className={`admin-rental-status admin-rental-status--${property.status.toLowerCase()}`}>{t(`adminRental.propertyStatus.${property.status}`)}</span></p>
        </div>
        <Link className="button button--secondary" to={`/admin/rent/properties/${property.id}/calendar`}>{t("adminRental.properties.calendar")}</Link>
      </header>

      <section className="admin-rental-panel">
        <div className="admin-rental-section-heading"><div><h2>{t("adminRental.editor.mainTitle")}</h2><p>{t("adminRental.editor.mainText")}</p></div></div>
        <div className="admin-rental-form-grid">
          {textField("slug")}{textField("area")}{textField("titleRu")}{textField("titleEn")}
          <div className="admin-rental-form-grid__wide">{textField("address")}</div>
          <div className="admin-rental-form-grid__wide">{textField("descriptionRu", true)}</div>
          <div className="admin-rental-form-grid__wide">{textField("descriptionEn", true)}</div>
          {numberField("bedrooms")}{numberField("beds")}{numberField("bathrooms")}
          {numberField("maxGuests")}{numberField("areaSqm", "0.01")}{numberField("floor")}
          {numberField("baseDailyPrice", "0.01")}{textField("currency")}
        </div>
        <fieldset className="admin-rental-amenities">
          <legend>{t("adminRental.editor.amenities")}</legend>
          {amenities.map((amenity) => (
            <label key={amenity}><input type="checkbox" checked={property.amenities.includes(amenity)} onChange={() => toggleAmenity(amenity)} />{t(`rental.amenities.${amenity}`)}</label>
          ))}
        </fieldset>
        {actionError ? <p className="form-alert" role="alert">{t("adminRental.editor.actionError")}</p> : null}
        {saved ? <p className="admin-rental-success">{t("adminRental.editor.saved")}</p> : null}
        <div className="admin-rental-actions">
          <button className="button button--primary" type="button" disabled={pending} onClick={() => void save()}>{t("adminRental.editor.save")}</button>
          {property.status !== "PUBLISHED" ? <button className="button button--secondary" type="button" disabled={pending} onClick={() => void publish()}>{t("adminRental.editor.publish")}</button> : null}
          {property.status !== "ARCHIVED" ? <button className="button button--danger" type="button" disabled={pending} onClick={() => void runPropertyAction(() => api.archiveAdminProperty(propertyId))}>{t("adminRental.editor.archive")}</button> : null}
        </div>
      </section>

      <section className="admin-rental-panel">
        <div className="admin-rental-section-heading"><div><h2>{t("adminRental.media.title")}</h2><p>{t("adminRental.media.subtitle")}</p></div><label className="button button--secondary admin-rental-upload"><Icon name="camera" size={18} />{t("adminRental.media.upload")}<input type="file" accept="image/jpeg,image/png,image/webp" disabled={pending} onChange={upload} /></label></div>
        {property.media.length === 0 ? <p className="admin-orders__empty">{t("adminRental.media.empty")}</p> : (
          <div className="admin-rental-media-grid">
            {property.media.map((media, index) => (
              <article className="admin-rental-media-card" key={media.id}>
                <AdminRentalMediaImage propertyId={propertyId} mediaId={media.id} alt={t("adminRental.media.photoAlt", { index: index + 1 })} />
                {media.cover ? <span>{t("adminRental.media.cover")}</span> : null}
                <div>
                  <button type="button" disabled={pending || index === 0} onClick={() => moveMedia(media.id, -1)}>↑</button>
                  <button type="button" disabled={pending || index === property.media.length - 1} onClick={() => moveMedia(media.id, 1)}>↓</button>
                  {!media.cover ? <button type="button" disabled={pending} onClick={() => void runPropertyAction(() => api.setAdminPropertyMediaCover(propertyId, media.id))}>{t("adminRental.media.makeCover")}</button> : null}
                  <button className="is-danger" type="button" disabled={pending} onClick={() => void runPropertyAction(() => api.removeAdminPropertyMedia(propertyId, media.id))}>{t("adminRental.media.remove")}</button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
