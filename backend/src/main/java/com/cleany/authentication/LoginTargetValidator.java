package com.cleany.authentication;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class LoginTargetValidator {

    private static final String DEFAULT_TARGET = "/";
    private static final Set<String> ALLOWED_ROUTE_ROOTS = Set.of(
            "/cleaning",
            "/rent",
            "/admin"
    );

    public String normalize(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return DEFAULT_TARGET;
        }
        String value = candidate.strip();
        if (containsUnsafeCharacters(value)) {
            return DEFAULT_TARGET;
        }
        try {
            URI uri = new URI(value);
            String path = uri.getRawPath();
            if (uri.isAbsolute()
                    || uri.getRawAuthority() != null
                    || uri.getRawFragment() != null
                    || path == null
                    || !path.startsWith("/")
                    || path.startsWith("//")
                    || !isAllowedPath(path)) {
                return DEFAULT_TARGET;
            }
            return uri.getRawQuery() == null ? path : path + "?" + uri.getRawQuery();
        } catch (URISyntaxException exception) {
            return DEFAULT_TARGET;
        }
    }

    private boolean isAllowedPath(String path) {
        if (DEFAULT_TARGET.equals(path)) {
            return true;
        }
        return ALLOWED_ROUTE_ROOTS.stream().anyMatch(root ->
                path.equals(root) || path.startsWith(root + "/")
        );
    }

    private boolean containsUnsafeCharacters(String value) {
        String lowerCase = value.toLowerCase(Locale.ROOT);
        return value.indexOf('\\') >= 0
                || value.chars().anyMatch(Character::isISOControl)
                || lowerCase.contains("%2f")
                || lowerCase.contains("%5c")
                || containsEncodedControl(lowerCase);
    }

    private boolean containsEncodedControl(String value) {
        for (int index = 0; index + 2 < value.length(); index++) {
            if (value.charAt(index) != '%') {
                continue;
            }
            int high = Character.digit(value.charAt(index + 1), 16);
            int low = Character.digit(value.charAt(index + 2), 16);
            if (high >= 0 && low >= 0) {
                int decoded = high * 16 + low;
                if (decoded <= 0x1f || decoded == 0x7f) {
                    return true;
                }
            }
        }
        return false;
    }
}
