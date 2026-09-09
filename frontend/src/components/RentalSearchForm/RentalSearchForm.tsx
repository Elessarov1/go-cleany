import { type FormEvent } from "react";
import { useTranslation } from "react-i18next";
import type { RentalConfiguration, RentalTermType } from "../../domain/rental";
import type {
  RentalSearchDraft,
  RentalSearchField,
} from "../../utils/rentalSearch";
import { Icon } from "../Icon/Icon";

export function RentalSearchForm({
  configuration,
  draft,
  errors,
  searching,
  onChange,
  onSubmit,
  onBrowseAll,
}: {
  configuration: RentalConfiguration;
  draft: RentalSearchDraft;
  errors: Partial<Record<RentalSearchField, string>>;
  searching: boolean;
  onChange: (draft: RentalSearchDraft) => void;
  onSubmit: () => void;
  onBrowseAll: () => void;
}) {
  const { t } = useTranslation();

  const selectMode = (termType: RentalTermType) => {
    onChange({
      ...draft,
      termType,
      checkOutDate: termType === "DATE_RANGE" ? draft.checkOutDate : "",
      months: termType === "MONTHLY" ? draft.months || "1" : "1",
    });
  };
  const fieldError = (field: RentalSearchField) => errors[field]
    ? t(`rental.search.errors.${errors[field]}`)
    : null;
  const submit = (event: FormEvent) => {
    event.preventDefault();
    onSubmit();
  };

  return (
    <form className="rental-search-form" onSubmit={submit} noValidate>
      <div className="rental-search-form__modes" role="radiogroup" aria-label={t("rental.search.termLabel")}>
        {(["DATE_RANGE", "MONTHLY"] as const).map((termType) => (
          <button
            key={termType}
            type="button"
            role="radio"
            aria-checked={draft.termType === termType}
            className={draft.termType === termType ? "is-selected" : ""}
            onClick={() => selectMode(termType)}
          >
            {t(`rental.search.mode.${termType}`)}
          </button>
        ))}
      </div>
      <div className="rental-search-form__fields">
        <div className="field">
          <label htmlFor="rental-search-check-in">{t("rental.booking.checkIn")}</label>
          <input
            id="rental-search-check-in"
            type="date"
            min={configuration.today}
            max={configuration.latestCheckInDate}
            value={draft.checkInDate}
            aria-invalid={Boolean(errors.checkInDate)}
            onChange={(event) => onChange({ ...draft, checkInDate: event.target.value })}
          />
          {fieldError("checkInDate") ? <small className="field-error">{fieldError("checkInDate")}</small> : null}
        </div>
        {draft.termType === "DATE_RANGE" ? (
          <div className="field">
            <label htmlFor="rental-search-check-out">{t("rental.booking.checkOut")}</label>
            <input
              id="rental-search-check-out"
              type="date"
              min={draft.checkInDate || configuration.today}
              value={draft.checkOutDate}
              aria-invalid={Boolean(errors.checkOutDate)}
              onChange={(event) => onChange({ ...draft, checkOutDate: event.target.value })}
            />
            {fieldError("checkOutDate") ? <small className="field-error">{fieldError("checkOutDate")}</small> : null}
          </div>
        ) : (
          <div className="field">
            <label htmlFor="rental-search-months">{t("rental.booking.monthsLabel")}</label>
            <input
              id="rental-search-months"
              type="number"
              min="1"
              value={draft.months}
              aria-invalid={Boolean(errors.months)}
              onChange={(event) => onChange({ ...draft, months: event.target.value })}
            />
            {fieldError("months") ? <small className="field-error">{fieldError("months")}</small> : null}
          </div>
        )}
        <div className="field">
          <label htmlFor="rental-search-guests">{t("rental.booking.guests")}</label>
          <input
            id="rental-search-guests"
            type="number"
            min="1"
            max="100"
            value={draft.guests}
            aria-invalid={Boolean(errors.guests)}
            onChange={(event) => onChange({ ...draft, guests: event.target.value })}
          />
          {fieldError("guests") ? <small className="field-error">{fieldError("guests")}</small> : null}
        </div>
      </div>
      <button className="button button--primary button--full button--large" type="submit" disabled={searching}>
        <Icon name="calendar-plus" size={18} />
        {searching ? t("rental.search.searching") : t("rental.search.submit")}
      </button>
      <button className="text-button rental-search-form__browse" type="button" onClick={onBrowseAll}>
        {t("rental.search.browseAll")}
      </button>
    </form>
  );
}
