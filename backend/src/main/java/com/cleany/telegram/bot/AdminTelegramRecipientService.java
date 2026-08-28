package com.cleany.telegram.bot;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.CustomerExternalIdentityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminTelegramRecipientService {

    private final CustomerExternalIdentityRepository identityRepository;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<Long> recipients() {
        return identityRepository.findEligibleAdminTelegramIdentities().stream()
                .map(identity -> parseTelegramId(identity.getExternalSubject()))
                .distinct()
                .toList();
    }

    private static long parseTelegramId(String value) {
        try {
            long id = Long.parseLong(value);
            if (id <= 0) {
                throw new NumberFormatException("Telegram user id must be positive");
            }
            return id;
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Persisted Telegram identity has an invalid subject", exception);
        }
    }
}
