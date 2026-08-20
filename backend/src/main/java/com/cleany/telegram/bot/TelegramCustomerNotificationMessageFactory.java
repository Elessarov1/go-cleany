package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.cleany.finance.ReferralFinancialProperties;

@Component
public class TelegramCustomerNotificationMessageFactory {

    private final ReferralFinancialProperties financialProperties;

    public TelegramCustomerNotificationMessageFactory(ReferralFinancialProperties financialProperties) {
        this.financialProperties = financialProperties;
    }

    public String referralUnlocked(String referralCode, String languageCode) {
        var customer = financialProperties.customer();
        String friendDiscount = percentage(customer.friendDiscountRate());
        String referrerReward = percentage(customer.referrerRewardRate());
        return isEnglish(languageCode)
                ? english(referralCode, friendDiscount, referrerReward)
                : russian(referralCode, friendDiscount, referrerReward);
    }

    private static String russian(String code, String friendDiscount, String referrerReward) {
        return """
                🎁 Подарите другу скидку на уборку

                Ваш персональный код: %s

                Друг получит скидку %s на свою первую уборку.

                После того как его первая уборка будет успешно завершена,
                вы получите скидку %s на свой следующий заказ.

                Просто отправьте этот код другу.
                """.formatted(code, friendDiscount, referrerReward).strip();
    }

    private static String english(String code, String friendDiscount, String referrerReward) {
        return """
                🎁 Give a friend a cleaning discount

                Your personal code: %s

                Your friend will receive %s off their first cleaning.

                After their first cleaning is successfully completed,
                you will receive %s off your next order.

                Simply send this code to your friend.
                """.formatted(code, friendDiscount, referrerReward).strip();
    }

    private static String percentage(BigDecimal rate) {
        return rate.movePointRight(2).stripTrailingZeros().toPlainString() + "%";
    }

    private static boolean isEnglish(String languageCode) {
        return languageCode != null
                && languageCode.toLowerCase(Locale.ROOT).startsWith("en");
    }
}
