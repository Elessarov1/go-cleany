package com.cleany.order;

import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

@Component
public class PhoneNumberNormalizer {

    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public String normalize(String rawPhone) {
        if (rawPhone == null) {
            throw new InvalidPhoneNumberException();
        }

        String candidate = rawPhone.trim();
        if (!candidate.startsWith("+")) {
            throw new InvalidPhoneNumberException();
        }

        try {
            var parsed = phoneNumberUtil.parse(candidate, null);
            if (parsed.hasExtension() || !phoneNumberUtil.isValidNumber(parsed)) {
                throw new InvalidPhoneNumberException();
            }
            return phoneNumberUtil.format(parsed, PhoneNumberFormat.E164);
        } catch (NumberParseException exception) {
            throw new InvalidPhoneNumberException();
        }
    }
}
