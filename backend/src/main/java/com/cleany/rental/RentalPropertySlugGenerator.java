package com.cleany.rental;

import java.text.Normalizer;
import java.util.Locale;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class RentalPropertySlugGenerator {

    private static final int MAX_LENGTH = 120;

    private final RentalPropertyRepository propertyRepository;

    String generate(String englishTitle, long propertyId) {
        String base = baseSlug(englishTitle);
        String candidate = base;
        int suffix = 2;
        while (propertyRepository.existsBySlugIgnoreCaseAndIdNot(candidate, propertyId)) {
            String suffixText = "-" + suffix++;
            candidate = trimTrailingHyphens(
                    base.substring(0, Math.min(base.length(), MAX_LENGTH - suffixText.length()))
            ) + suffixText;
        }
        return candidate;
    }

    static String baseSlug(String englishTitle) {
        if (englishTitle == null || englishTitle.isBlank()) {
            throw new IllegalArgumentException("English title is required to generate a slug");
        }
        String normalized = Normalizer.normalize(englishTitle, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("English title must contain Latin letters or digits");
        }
        return trimTrailingHyphens(normalized.substring(0, Math.min(normalized.length(), MAX_LENGTH)));
    }

    private static String trimTrailingHyphens(String value) {
        return value.replaceFirst("-+$", "");
    }
}
