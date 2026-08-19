package com.cleany.finance;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderFinancialCalculatorTest {

    @Test
    void organicOrder_commissionPoolAndCleanerTargetPreserved() {
        var snapshot = calculator().organic(amount("1000"));

        Assertions.assertAll(
                () -> assertAmount("1000.00", snapshot.basePrice()),
                () -> assertAmount("150.00", snapshot.baseCommission()),
                () -> assertAmount("0.00", snapshot.customerDiscount()),
                () -> assertAmount("1000.00", snapshot.finalCustomerPrice()),
                () -> assertAmount("150.00", snapshot.platformNet())
        );
    }

    @Test
    void customerReferral_friendDiscountConsumesCurrentOrderCommission() {
        var snapshot = calculator().customerReferral(amount("1000"));

        Assertions.assertAll(
                () -> assertAmount("150.00", snapshot.customerDiscount()),
                () -> assertAmount("850.00", snapshot.finalCustomerPrice()),
                () -> assertAmount("0.00", snapshot.partnerPayout()),
                () -> assertAmount("0.00", snapshot.platformNet()),
                () -> Assertions.assertEquals(AcquisitionSource.CUSTOMER_REFERRAL, snapshot.acquisitionSource())
        );
    }

    @Test
    void referrerReward_usesSeparateOrderCommissionPool() {
        var snapshot = calculator().referrerReward(amount("1000"));

        Assertions.assertAll(
                () -> assertAmount("100.00", snapshot.customerDiscount()),
                () -> assertAmount("900.00", snapshot.finalCustomerPrice()),
                () -> assertAmount("50.00", snapshot.platformNet()),
                () -> Assertions.assertEquals(CustomerDiscountType.REFERRER_REWARD, snapshot.customerDiscountType())
        );
    }

    @Test
    void partnerReferral_discountAndPayoutNeverExceedCommissionPool() {
        var snapshot = calculator().partnerReferral(amount("1000"));

        Assertions.assertAll(
                () -> assertAmount("50.00", snapshot.customerDiscount()),
                () -> assertAmount("100.00", snapshot.partnerPayout()),
                () -> assertAmount("950.00", snapshot.finalCustomerPrice()),
                () -> assertAmount("0.00", snapshot.platformNet()),
                () -> assertAmount("150.00", snapshot.customerDiscount().add(snapshot.partnerPayout()))
        );
    }

    @Test
    void expensiveOrder_configuredMonetaryCapApplied() {
        var snapshot = calculator().customerReferral(amount("20000"));

        Assertions.assertAll(
                () -> assertAmount("2000.00", snapshot.customerDiscount()),
                () -> assertAmount("3000.00", snapshot.baseCommission()),
                () -> assertAmount("1000.00", snapshot.platformNet())
        );
    }

    @Test
    void invalidPartnerRates_applicationConfigurationRejected() {
        var properties = properties(
                new ReferralFinancialProperties.Partner(
                        amount("0.07"), amount("2000"), amount("0.12"), amount("2000")
                )
        );

        Assertions.assertThrows(
                InvalidFinancialConfigurationException.class,
                () -> new OrderFinancialCalculator(properties)
        );
    }

    private static OrderFinancialCalculator calculator() {
        return new OrderFinancialCalculator(properties(new ReferralFinancialProperties.Partner(
                amount("0.05"), amount("2000"), amount("0.10"), amount("2000")
        )));
    }

    private static ReferralFinancialProperties properties(ReferralFinancialProperties.Partner partner) {
        return new ReferralFinancialProperties(
                amount("0.15"),
                new ReferralFinancialProperties.Customer(
                        amount("0.15"), amount("2000"), amount("0.10"), amount("2000")
                ),
                partner
        );
    }

    private static BigDecimal amount(String value) {
        return new BigDecimal(value);
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        Assertions.assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
