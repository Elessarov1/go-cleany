package com.cleany.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResolveSupportCaseRequest(
        @NotBlank @Size(max = 2000) String resolutionComment
) {
}
