import { describe, expect, it } from "vitest";
import type { RentalConfiguration } from "../domain/rental";
import {
  emptyRentalSearchDraft,
  parseRentalSearchQuery,
  rentalSearchRequestFromDraft,
  serializeRentalSearch,
  validateRentalSearchDraft,
} from "./rentalSearch";

const configuration: RentalConfiguration = {
  minStayDays: 7,
  longTermMinDays: 30,
  longTermDiscountRate: 0.1,
  maxStayDays: 365,
  bookingStartMonthsAhead: 6,
  maxActiveBookingsPerCustomer: 3,
  today: "2026-09-09",
  latestCheckInDate: "2027-03-09",
};

describe("rental search URL contract", () => {
  it("round-trips date-range and monthly searches", () => {
    const dateRange = {
      termType: "DATE_RANGE" as const,
      checkInDate: "2026-10-01",
      checkOutDate: "2026-10-08",
      guests: 3,
    };
    const monthly = {
      termType: "MONTHLY" as const,
      checkInDate: "2026-11-15",
      months: 2,
      guests: 2,
    };
    expect(parseRentalSearchQuery(serializeRentalSearch(dateRange), configuration).applied)
      .toEqual(dateRange);
    expect(parseRentalSearchQuery(serializeRentalSearch(monthly), configuration).applied)
      .toEqual(monthly);
  });

  it("keeps recognizable invalid values in draft and does not apply a search", () => {
    const parsed = parseRentalSearchQuery(new URLSearchParams(
      "termType=DATE_RANGE&checkInDate=2026-10-01&checkOutDate=2026-10-03&guests=abc",
    ), configuration);
    expect(parsed.applied).toBeNull();
    expect(parsed.draft.guests).toBe("abc");
    expect(parsed.errors).toMatchObject({ checkOutDate: "minimum", guests: "guests" });
  });

  it("supports browse-all only without rental conditions", () => {
    expect(parseRentalSearchQuery(new URLSearchParams("view=all"), configuration).applied)
      .toEqual({ view: "all" });
    expect(parseRentalSearchQuery(
      new URLSearchParams("view=all&guests=2"),
      configuration,
    ).applied).toBeNull();
  });

  it("validates the backend business today and preserves a switched draft", () => {
    const draft = { ...emptyRentalSearchDraft(), checkInDate: "2026-09-08", guests: "101" };
    expect(validateRentalSearchDraft(draft, configuration)).toMatchObject({
      checkInDate: "outsideHorizon",
      checkOutDate: "required",
      guests: "guests",
    });
    expect(rentalSearchRequestFromDraft({
      ...draft,
      termType: "MONTHLY",
      checkInDate: "2026-10-01",
      months: "3",
      guests: "4",
    })).toEqual({ termType: "MONTHLY", checkInDate: "2026-10-01", months: 3, guests: 4 });
  });
});
