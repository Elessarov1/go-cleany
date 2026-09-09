import { useState } from "react";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
import type { RentalConfiguration } from "../../domain/rental";
import type { RentalSearchDraft } from "../../utils/rentalSearch";
import { RentalSearchForm } from "./RentalSearchForm";

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

describe("RentalSearchForm", () => {
  it("keeps guests and a valid start date while removing incompatible mode fields", async () => {
    const user = userEvent.setup();
    render(<Harness />);
    await user.click(screen.getByRole("radio", { name: "By months" }));
    expect(screen.getByLabelText("Check-in")).toHaveValue("2026-10-01");
    expect(screen.getByLabelText("Number of guests")).toHaveValue(4);
    expect(screen.queryByLabelText("Check-out")).not.toBeInTheDocument();
    expect(screen.getByLabelText("Rental duration")).toHaveValue(1);
  });
});

function Harness() {
  const [draft, setDraft] = useState<RentalSearchDraft>({
    termType: "DATE_RANGE",
    checkInDate: "2026-10-01",
    checkOutDate: "2026-10-08",
    months: "1",
    guests: "4",
  });
  return <RentalSearchForm
    configuration={configuration}
    draft={draft}
    errors={{}}
    searching={false}
    onChange={setDraft}
    onSubmit={() => undefined}
    onBrowseAll={() => undefined}
  />;
}
