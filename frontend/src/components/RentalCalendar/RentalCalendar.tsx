import { useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import type { RentalAvailabilityRange, RentalConfiguration } from "../../domain/rental";
import { addDaysToInputValue, addMonthsToInputValue, daysBetween, todayAsInputValue } from "../../utils/format";
import { Icon } from "../Icon/Icon";

interface RentalCalendarProps {
  configuration: RentalConfiguration;
  unavailableRanges: RentalAvailabilityRange[];
  checkInDate: string;
  checkOutDate: string;
  onChange: (checkInDate: string, checkOutDate: string) => void;
  onValidationError: (message: string | null) => void;
}

function startOfMonth(value: string): Date {
  const date = new Date(`${value.slice(0, 7)}-01T12:00:00`);
  date.setHours(12, 0, 0, 0);
  return date;
}

function inputValue(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function monthKey(date: Date): string {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}`;
}

function overlaps(
  range: RentalAvailabilityRange,
  startDate: string,
  endDate: string,
): boolean {
  return range.startDate < endDate && range.endDate > startDate;
}

export function RentalCalendar({
  configuration,
  unavailableRanges,
  checkInDate,
  checkOutDate,
  onChange,
  onValidationError,
}: RentalCalendarProps) {
  const { t, i18n } = useTranslation();
  const today = todayAsInputValue();
  const [visibleMonth, setVisibleMonth] = useState(() => startOfMonth(today));
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";
  const maxCheckIn = addMonthsToInputValue(today, configuration.bookingStartMonthsAhead);
  const maxCalendarDate = addDaysToInputValue(maxCheckIn, configuration.maxStayDays);
  const calendarDays = useMemo(() => {
    const first = new Date(visibleMonth);
    const mondayOffset = (first.getDay() + 6) % 7;
    first.setDate(first.getDate() - mondayOffset);
    return Array.from({ length: 42 }, (_, index) => {
      const date = new Date(first);
      date.setDate(first.getDate() + index);
      return date;
    });
  }, [visibleMonth]);

  const monthLabel = new Intl.DateTimeFormat(locale, {
    month: "long",
    year: "numeric",
  }).format(visibleMonth);
  const weekdayLabels = Array.from({ length: 7 }, (_, index) => {
    const monday = new Date(2026, 0, 5 + index, 12);
    return new Intl.DateTimeFormat(locale, { weekday: "short" }).format(monday);
  });

  const isUnavailableDay = (value: string): boolean => unavailableRanges.some(
    (range) => range.startDate <= value && value < range.endDate,
  );

  const selectDate = (value: string) => {
    onValidationError(null);
    const selectingStart = !checkInDate || Boolean(checkOutDate);
    if (selectingStart) {
      if (value < today || value > maxCheckIn || isUnavailableDay(value)) return;
      onChange(value, "");
      return;
    }

    if (value <= checkInDate) {
      if (value >= today && value <= maxCheckIn && !isUnavailableDay(value)) {
        onChange(value, "");
      }
      return;
    }

    const duration = daysBetween(checkInDate, value);
    if (duration < configuration.minStayDays) {
      onValidationError(t("rental.calendar.minimum", { count: configuration.minStayDays }));
      return;
    }
    if (duration > configuration.maxStayDays) {
      onValidationError(t("rental.calendar.maximum", { count: configuration.maxStayDays }));
      return;
    }
    if (unavailableRanges.some((range) => overlaps(range, checkInDate, value))) {
      onValidationError(t("rental.calendar.crossesUnavailable"));
      return;
    }
    onChange(checkInDate, value);
  };

  const moveMonth = (offset: number) => {
    setVisibleMonth((current) => {
      const next = new Date(current);
      next.setMonth(next.getMonth() + offset);
      return next;
    });
  };

  const earliestMonth = monthKey(startOfMonth(today));
  const latestMonth = monthKey(startOfMonth(maxCalendarDate));

  return (
    <div className="rental-calendar">
      <div className="rental-calendar__toolbar">
        <button
          type="button"
          aria-label={t("rental.calendar.previousMonth")}
          disabled={monthKey(visibleMonth) <= earliestMonth}
          onClick={() => moveMonth(-1)}
        >
          <Icon name="arrow-left" size={18} />
        </button>
        <strong>{monthLabel}</strong>
        <button
          type="button"
          aria-label={t("rental.calendar.nextMonth")}
          disabled={monthKey(visibleMonth) >= latestMonth}
          onClick={() => moveMonth(1)}
        >
          <Icon name="arrow-right" size={18} />
        </button>
      </div>

      <div className="rental-calendar__weekdays" aria-hidden="true">
        {weekdayLabels.map((label) => <span key={label}>{label}</span>)}
      </div>
      <div className="rental-calendar__grid">
        {calendarDays.map((date) => {
          const value = inputValue(date);
          const outsideMonth = date.getMonth() !== visibleMonth.getMonth();
          const unavailable = isUnavailableDay(value);
          const inSelection = Boolean(checkInDate && checkOutDate)
            && checkInDate <= value && value < checkOutDate;
          const selected = value === checkInDate || value === checkOutDate;
          const selectingCheckout = Boolean(checkInDate && !checkOutDate && value > checkInDate);
          const checkoutOverlaps = selectingCheckout && unavailableRanges.some(
            (range) => overlaps(range, checkInDate, value),
          );
          const disabled = outsideMonth
            || value < today
            || (!checkInDate || checkOutDate ? value > maxCheckIn || unavailable : false)
            || (selectingCheckout && (
              daysBetween(checkInDate, value) > configuration.maxStayDays || checkoutOverlaps
            ));
          const classNames = [
            unavailable ? "is-unavailable" : "",
            inSelection ? "is-in-range" : "",
            selected ? "is-selected" : "",
            value === today ? "is-today" : "",
          ].filter(Boolean).join(" ");
          return (
            <button
              key={value}
              className={classNames}
              type="button"
              disabled={disabled}
              aria-label={new Intl.DateTimeFormat(locale, { dateStyle: "long" }).format(date)}
              onClick={() => selectDate(value)}
            >
              {date.getDate()}
            </button>
          );
        })}
      </div>
      <div className="rental-calendar__legend">
        <span><i className="is-free" />{t("rental.calendar.free")}</span>
        <span><i className="is-unavailable" />{t("rental.calendar.unavailable")}</span>
        <span><i className="is-selected" />{t("rental.calendar.selected")}</span>
      </div>
    </div>
  );
}
