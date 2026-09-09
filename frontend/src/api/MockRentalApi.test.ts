import { describe, expect, it } from "vitest";
import type { RentalTermCriteria } from "../domain/rental";
import type { Platform } from "../platform/Platform";
import { MockRentalApi, mockRentalConfiguration } from "./MockRentalApi";

describe("MockRentalApi period-first quote", () => {
  it("uses the same normalized criteria and price projection as search", async () => {
    const api = new MockRentalApi({} as Platform);
    const criteria: RentalTermCriteria = {
      termType: "DATE_RANGE",
      checkInDate: addDays(mockRentalConfiguration.today, 60),
      checkOutDate: addDays(mockRentalConfiguration.today, 67),
      guests: 2,
    };

    const quote = await api.quotePublic(201, criteria);
    const search = await api.search(criteria);
    const result = search.properties.find((property) => property.id === 201);

    expect(quote.criteria).toEqual({
      mode: "DATE_RANGE",
      termType: "DATE_RANGE",
      checkInDate: criteria.checkInDate,
      checkOutDate: criteria.checkOutDate,
      rentalMonths: null,
      durationDays: 8,
      guests: 2,
    });
    expect(result?.price).toEqual(quote.price);
  });
});

function addDays(value: string, days: number): string {
  const date = new Date(`${value}T00:00:00Z`);
  date.setUTCDate(date.getUTCDate() + days);
  return date.toISOString().slice(0, 10);
}
