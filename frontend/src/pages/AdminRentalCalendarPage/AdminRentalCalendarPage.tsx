import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useRentalApi } from "../../api/RentalApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type { RentalOccupancy, RentalOccupancyType, RentalProperty, UpsertRentalOccupancyRequest } from "../../domain/rental";
import { addDaysToInputValue, formatDate, todayAsInputValue } from "../../utils/format";

const manualTypes: Exclude<RentalOccupancyType, "BOOKING">[] = ["OWNER_BLOCK", "EXTERNAL_BOOKING", "MAINTENANCE"];

function toInputDate(date: Date): string {
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
  return local.toISOString().slice(0, 10);
}

function calendarDates(month: Date): string[] {
  const first = new Date(month.getFullYear(), month.getMonth(), 1, 12);
  const offset = (first.getDay() + 6) % 7;
  first.setDate(first.getDate() - offset);
  return Array.from({ length: 42 }, (_, index) => {
    const date = new Date(first);
    date.setDate(first.getDate() + index);
    return toInputDate(date);
  });
}

export function AdminRentalCalendarPage() {
  const { id } = useParams();
  const propertyId = Number(id);
  const { t, i18n } = useTranslation();
  const api = useRentalApi();
  const navigate = useNavigate();
  const [property, setProperty] = useState<RentalProperty | null>(null);
  const [occupancies, setOccupancies] = useState<RentalOccupancy[] | null>(null);
  const [month, setMonth] = useState(() => new Date(new Date().getFullYear(), new Date().getMonth(), 1, 12));
  const [editingId, setEditingId] = useState<number | null>(null);
  const [startDate, setStartDate] = useState(todayAsInputValue());
  const [endDate, setEndDate] = useState(addDaysToInputValue(todayAsInputValue(), 7));
  const [type, setType] = useState<Exclude<RentalOccupancyType, "BOOKING">>("OWNER_BLOCK");
  const [note, setNote] = useState("");
  const [error, setError] = useState(false);
  const [actionError, setActionError] = useState(false);
  const [pending, setPending] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const dates = useMemo(() => calendarDates(month), [month]);
  const fromDate = dates[0]!;
  const toDate = addDaysToInputValue(dates[dates.length - 1]!, 1);
  const language = i18n.resolvedLanguage === "ru" ? "ru" : "en";
  const locale = language === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setError(false);
    Promise.all([
      api.getAdminProperty(propertyId),
      api.getAdminOccupancies(propertyId, fromDate, toDate),
    ]).then(([propertyValue, occupancyValues]) => {
      if (!active) return;
      setProperty(propertyValue);
      setOccupancies(occupancyValues);
    }).catch(() => {
      if (active) setError(true);
    });
    return () => { active = false; };
  }, [api, fromDate, propertyId, reloadKey, toDate]);

  const reloadOccupancies = async () => setOccupancies(await api.getAdminOccupancies(propertyId, fromDate, toDate));
  const resetForm = () => {
    setEditingId(null);
    setStartDate(todayAsInputValue());
    setEndDate(addDaysToInputValue(todayAsInputValue(), 7));
    setType("OWNER_BLOCK");
    setNote("");
  };
  const edit = (occupancy: RentalOccupancy) => {
    if (occupancy.type === "BOOKING") {
      if (occupancy.bookingId) navigate(`/admin/rent/bookings/${occupancy.bookingId}`);
      return;
    }
    setEditingId(occupancy.id);
    setStartDate(occupancy.startDate);
    setEndDate(occupancy.endDate);
    setType(occupancy.type);
    setNote(occupancy.note ?? "");
    window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
  };
  const save = async () => {
    if (startDate >= endDate) {
      setActionError(true);
      return;
    }
    const request: UpsertRentalOccupancyRequest = { startDate, endDate, type, note: note.trim() || null };
    try {
      setPending(true);
      setActionError(false);
      if (editingId) await api.updateAdminOccupancy(propertyId, editingId, request);
      else await api.createAdminOccupancy(propertyId, request);
      await reloadOccupancies();
      resetForm();
    } catch {
      setActionError(true);
    } finally {
      setPending(false);
    }
  };
  const remove = async () => {
    if (!editingId || !window.confirm(t("adminRental.calendar.deleteConfirm"))) return;
    try {
      setPending(true);
      setActionError(false);
      await api.deleteAdminOccupancy(propertyId, editingId);
      await reloadOccupancies();
      resetForm();
    } catch {
      setActionError(true);
    } finally {
      setPending(false);
    }
  };
  const moveMonth = (offset: number) => setMonth((current) => new Date(current.getFullYear(), current.getMonth() + offset, 1, 12));

  if (error || !Number.isFinite(propertyId)) return <ErrorState message={t("adminRental.calendar.loadError")} onRetry={() => setReloadKey((key) => key + 1)} />;
  if (!property || !occupancies) return <LoadingState />;
  const monthLabel = new Intl.DateTimeFormat(locale, { month: "long", year: "numeric" }).format(month);
  const weekdays = Array.from({ length: 7 }, (_, index) => new Intl.DateTimeFormat(locale, { weekday: "short" }).format(new Date(2026, 7, 24 + index)));

  return (
    <div className="page page--admin-rental">
      <Link className="back-link" to="/admin/rent/properties"><Icon name="arrow-left" size={17} />{t("common.back")}</Link>
      <header className="admin-rental-header">
        <div><span className="eyebrow">go-rent / #{property.id}</span><h1>{t("adminRental.calendar.title")}</h1><p>{property.titleRu || property.titleEn || t("adminRental.properties.untitled")}</p></div>
        <Link className="button button--secondary" to={`/admin/rent/properties/${property.id}`}>{t("adminRental.properties.edit")}</Link>
      </header>

      <section className="admin-rental-panel">
        <div className="admin-rental-calendar-toolbar">
          <button type="button" aria-label={t("rental.calendar.previousMonth")} onClick={() => moveMonth(-1)}><Icon name="arrow-left" size={18} /></button>
          <strong>{monthLabel}</strong>
          <button type="button" aria-label={t("rental.calendar.nextMonth")} onClick={() => moveMonth(1)}><Icon name="arrow-right" size={18} /></button>
        </div>
        <div className="admin-rental-calendar-weekdays">{weekdays.map((day) => <span key={day}>{day}</span>)}</div>
        <div className="admin-rental-calendar-grid">
          {dates.map((date) => {
            const dateMonth = Number(date.slice(5, 7)) - 1;
            const dayOccupancies = occupancies.filter((item) => item.startDate <= date && item.endDate > date);
            const occupancy = dayOccupancies[0];
            return (
              <button
                className={`${dateMonth !== month.getMonth() ? "is-outside " : ""}${occupancy ? `has-${occupancy.type.toLowerCase()}` : ""}`}
                key={date}
                type="button"
                title={occupancy ? t(`adminRental.occupancy.${occupancy.type}`) : t("rental.calendar.free")}
                onClick={() => occupancy && edit(occupancy)}
              >
                <span>{Number(date.slice(8, 10))}</span>
                {occupancy ? <small>{t(`adminRental.occupancyShort.${occupancy.type}`)}</small> : null}
              </button>
            );
          })}
        </div>
        <div className="admin-rental-calendar-legend">
          {(["BOOKING", ...manualTypes] as RentalOccupancyType[]).map((item) => <span key={item}><i className={`has-${item.toLowerCase()}`} />{t(`adminRental.occupancy.${item}`)}</span>)}
        </div>
      </section>

      <section className="admin-rental-panel">
        <div className="admin-rental-section-heading"><div><h2>{t(editingId ? "adminRental.calendar.editTitle" : "adminRental.calendar.createTitle")}</h2><p>{t("adminRental.calendar.formText")}</p></div>{editingId ? <button className="admin-rental-text-button" type="button" onClick={resetForm}>{t("adminRental.calendar.newBlock")}</button> : null}</div>
        <div className="admin-rental-form-grid">
          <div className="field"><label><span>{t("adminRental.calendar.startDate")}</span><input type="date" value={startDate} onChange={(event) => setStartDate(event.target.value)} /></label></div>
          <div className="field"><label><span>{t("adminRental.calendar.endDate")}</span><input type="date" min={addDaysToInputValue(startDate, 1)} value={endDate} onChange={(event) => setEndDate(event.target.value)} /></label></div>
          <div className="field"><label><span>{t("adminRental.calendar.type")}</span><select value={type} onChange={(event) => setType(event.target.value as Exclude<RentalOccupancyType, "BOOKING">)}>{manualTypes.map((item) => <option key={item} value={item}>{t(`adminRental.occupancy.${item}`)}</option>)}</select></label></div>
          <div className="field"><label><span>{t("adminRental.calendar.note")}</span><input maxLength={1000} value={note} onChange={(event) => setNote(event.target.value)} /></label></div>
        </div>
        {editingId ? <p className="admin-rental-range-caption">{formatDate(startDate, locale)} — {formatDate(endDate, locale)}</p> : null}
        {actionError ? <p className="form-alert" role="alert">{t("adminRental.calendar.actionError")}</p> : null}
        <div className="admin-rental-actions">
          <button className="button button--primary" type="button" disabled={pending} onClick={() => void save()}>{t(editingId ? "adminRental.calendar.update" : "adminRental.calendar.create")}</button>
          {editingId ? <button className="button button--danger" type="button" disabled={pending} onClick={() => void remove()}>{t("adminRental.calendar.delete")}</button> : null}
        </div>
      </section>
    </div>
  );
}
