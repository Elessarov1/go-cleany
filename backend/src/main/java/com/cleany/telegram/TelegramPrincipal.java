package com.cleany.telegram;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record TelegramPrincipal(
        long id,
        String username,
        String firstName,
        String lastName
) {

    public String displayName() {
        var name = Stream.of(firstName, lastName)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(" "));
        return name.isBlank() ? "Telegram user " + id : name;
    }
}

