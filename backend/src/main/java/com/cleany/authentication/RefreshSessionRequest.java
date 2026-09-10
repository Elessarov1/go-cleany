package com.cleany.authentication;

import jakarta.validation.constraints.NotBlank;

public record RefreshSessionRequest(@NotBlank String refreshToken) {
}
