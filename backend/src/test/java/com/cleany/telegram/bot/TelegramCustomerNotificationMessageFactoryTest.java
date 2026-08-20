package com.cleany.telegram.bot;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cleany.finance.ReferralFinancialProperties;

class TelegramCustomerNotificationMessageFactoryTest {

    private TelegramCustomerNotificationMessageFactory factory;

    @BeforeEach
    void setUp() {
        var properties = new ReferralFinancialProperties(
                new BigDecimal("0.15"),
                new ReferralFinancialProperties.Customer(
                        new BigDecimal("0.15"),
                        new BigDecimal("2000"),
                        new BigDecimal("0.10"),
                        new BigDecimal("2000")
                ),
                new ReferralFinancialProperties.Partner(
                        new BigDecimal("0.05"),
                        new BigDecimal("2000"),
                        new BigDecimal("0.10"),
                        new BigDecimal("2000")
                )
        );
        factory = new TelegramCustomerNotificationMessageFactory(properties);
    }

    @Test
    void russianCustomer_messageExplainsBothReferralBenefits() {
        String message = factory.referralUnlocked("ALEX7K2", "ru");

        Assertions.assertAll(
                () -> Assertions.assertTrue(message.contains("ALEX7K2")),
                () -> Assertions.assertTrue(message.contains("скидку 15%")),
                () -> Assertions.assertTrue(message.contains("скидку 10%")),
                () -> Assertions.assertTrue(message.contains("успешно завершена"))
        );
    }

    @Test
    void englishCustomer_messageExplainsBothReferralBenefits() {
        String message = factory.referralUnlocked("ALEX7K2", "en-US");

        Assertions.assertAll(
                () -> Assertions.assertTrue(message.contains("ALEX7K2")),
                () -> Assertions.assertTrue(message.contains("15% off")),
                () -> Assertions.assertTrue(message.contains("10% off")),
                () -> Assertions.assertTrue(message.contains("successfully completed"))
        );
    }
}
