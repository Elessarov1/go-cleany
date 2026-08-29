package com.cleany.transfer;

import jakarta.validation.constraints.Min;

public record AssignTransferDriverRequest(@Min(1) long driverId) {
}
