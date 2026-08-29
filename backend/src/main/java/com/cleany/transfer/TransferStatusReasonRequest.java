package com.cleany.transfer;

import jakarta.validation.constraints.Size;

public record TransferStatusReasonRequest(@Size(max = 1000) String reason) {
}
