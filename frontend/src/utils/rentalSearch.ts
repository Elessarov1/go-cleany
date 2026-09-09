import type {
  RentalConfiguration,
  RentalSearchRequest,
  RentalTermType,
} from "../domain/rental";
import {
  addDaysToInputValue,
  addMonthsToInputValue,
  inclusiveDaysBetween,
} from "./format";

export interface RentalSearchDraft {
  termType: RentalTermType;
  checkInDate: string;
  checkOutDate: string;
  months: string;
  guests: string;
}

export type RentalSearchField = "checkInDate" | "checkOutDate" | "months" | "guests";

export interface ParsedRentalSearch {
  draft: RentalSearchDraft;
  applied: RentalSearchRequest | null;
  errors: Partial<Record<RentalSearchField, string>>;
  initial: boolean;
}

const ISO_DATE = /^\d{4}-\d{2}-\d{2}$/;

export function emptyRentalSearchDraft(): RentalSearchDraft {
  return {
    termType: "DATE_RANGE",
    checkInDate: "",
    checkOutDate: "",
    months: "1",
    guests: "1",
  };
}

export function parseRentalSearchQuery(
  params: URLSearchParams,
  configuration: RentalConfiguration,
): ParsedRentalSearch {
  const rawTerm = params.get("termType")?.toUpperCase();
  const termType: RentalTermType = rawTerm === "MONTHLY" ? "MONTHLY" : "DATE_RANGE";
  const draft: RentalSearchDraft = {
    termType,
    checkInDate: params.get("checkInDate") ?? "",
    checkOutDate: params.get("checkOutDate") ?? "",
    months: params.get("months") ?? "1",
    guests: params.get("guests") ?? "1",
  };
  const recognized = ["termType", "checkInDate", "checkOutDate", "months", "guests", "view"]
    .some((name) => params.has(name));

  if (params.get("view") === "all") {
    const hasConditions = ["termType", "checkInDate", "checkOutDate", "months", "guests"]
      .some((name) => params.has(name));
    return {
      draft,
      applied: hasConditions ? null : { view: "all" },
      errors: hasConditions ? { checkInDate: "invalidCombination" } : {},
      initial: false,
    };
  }
  if (!recognized) return { draft, applied: null, errors: {}, initial: true };

  const errors = validateRentalSearchDraft(draft, configuration);
  const incompatible = termType === "DATE_RANGE" ? params.has("months") : params.has("checkOutDate");
  if (rawTerm !== termType || incompatible) errors.checkInDate = "invalidCombination";
  if (Object.keys(errors).length > 0) return { draft, applied: null, errors, initial: false };
  return { draft, applied: rentalSearchRequestFromDraft(draft), errors, initial: false };
}

export function validateRentalSearchDraft(
  draft: RentalSearchDraft,
  configuration: RentalConfiguration,
): Partial<Record<RentalSearchField, string>> {
  const errors: Partial<Record<RentalSearchField, string>> = {};
  if (!ISO_DATE.test(draft.checkInDate)) {
    errors.checkInDate = "required";
  } else if (
    draft.checkInDate < configuration.today
    || draft.checkInDate > configuration.latestCheckInDate
  ) {
    errors.checkInDate = "outsideHorizon";
  }

  const guests = Number(draft.guests);
  if (!Number.isInteger(guests) || guests < 1 || guests > 100) errors.guests = "guests";

  if (draft.termType === "DATE_RANGE") {
    if (!ISO_DATE.test(draft.checkOutDate)) {
      errors.checkOutDate = "required";
    } else if (!errors.checkInDate) {
      const days = inclusiveDaysBetween(draft.checkInDate, draft.checkOutDate);
      if (days < configuration.minStayDays) errors.checkOutDate = "minimum";
      if (days > configuration.longTermMinDays - 1) errors.checkOutDate = "maximum";
    }
  } else {
    const months = Number(draft.months);
    if (!Number.isInteger(months) || months < 1) {
      errors.months = "months";
    } else if (!errors.checkInDate) {
      const checkOut = addDaysToInputValue(addMonthsToInputValue(draft.checkInDate, months), -1);
      if (inclusiveDaysBetween(draft.checkInDate, checkOut) > configuration.maxStayDays) {
        errors.months = "maximum";
      }
    }
  }
  return errors;
}

export function rentalSearchRequestFromDraft(draft: RentalSearchDraft): RentalSearchRequest {
  const guests = Number(draft.guests);
  return draft.termType === "DATE_RANGE"
    ? {
        termType: "DATE_RANGE",
        checkInDate: draft.checkInDate,
        checkOutDate: draft.checkOutDate,
        guests,
      }
    : {
        termType: "MONTHLY",
        checkInDate: draft.checkInDate,
        months: Number(draft.months),
        guests,
      };
}

export function serializeRentalSearch(request: RentalSearchRequest): URLSearchParams {
  if (!("termType" in request) || !request.termType) return new URLSearchParams({ view: "all" });
  const params = new URLSearchParams({
    termType: request.termType,
    checkInDate: request.checkInDate,
    guests: String(request.guests),
  });
  if (request.termType === "DATE_RANGE") params.set("checkOutDate", request.checkOutDate);
  else params.set("months", String(request.months));
  return params;
}

export function rentalSearchKey(request: RentalSearchRequest): string {
  return serializeRentalSearch(request).toString();
}

export function rentalSearchDetailQuery(request: RentalSearchRequest): string {
  return serializeRentalSearch(request).toString();
}

const EXECUTION_STORAGE_PREFIX = "loco.rental-search.";

export function rememberRentalSearchExecution(request: RentalSearchRequest, executionId: string): void {
  try {
    sessionStorage.setItem(`${EXECUTION_STORAGE_PREFIX}${rentalSearchKey(request)}`, executionId);
  } catch {
    // Analytics context is optional and must never break the funnel.
  }
}

export function recalledRentalSearchExecution(request: RentalSearchRequest): string | null {
  try {
    return sessionStorage.getItem(`${EXECUTION_STORAGE_PREFIX}${rentalSearchKey(request)}`);
  } catch {
    return null;
  }
}
