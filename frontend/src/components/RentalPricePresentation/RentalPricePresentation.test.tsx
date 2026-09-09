import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { RentalPricePresentation } from "./RentalPricePresentation";

describe("RentalPricePresentation", () => {
  it("makes the discounted monthly price primary and keeps total secondary", () => {
    render(<RentalPricePresentation locale="en-GB" price={{
      baseDailyPrice: 100,
      baseMonthlyPrice: 3000,
      monthlyPrice: 2700,
      baseAmount: 5400,
      discountRate: 0.1,
      discountAmount: 600,
      longTermDiscountApplied: true,
      totalPrice: 5400,
      currency: "TRY",
      rentalMonths: 2,
      durationDays: 61,
    }} />);
    expect(screen.getByText(/2,700/)).toBeInTheDocument();
    expect(screen.getByText("−10%")).toBeInTheDocument();
    expect(screen.getByText(/Total:/)).toHaveTextContent(/5,400/);
  });

  it("does not render a fake discount when no amount was saved", () => {
    render(<RentalPricePresentation locale="en-GB" price={{
      baseDailyPrice: 100,
      baseMonthlyPrice: 3000,
      monthlyPrice: 3000,
      baseAmount: 3000,
      discountRate: 0.1,
      discountAmount: 0,
      longTermDiscountApplied: false,
      totalPrice: 3000,
      currency: "TRY",
      rentalMonths: 1,
      durationDays: 30,
    }} />);
    expect(screen.queryByText("−10%")).not.toBeInTheDocument();
  });
});
