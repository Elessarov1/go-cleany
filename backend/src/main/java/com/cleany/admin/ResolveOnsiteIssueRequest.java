package com.cleany.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResolveOnsiteIssueRequest(
        @NotBlank @Size(max = 1000) String resolutionComment
) {
}
