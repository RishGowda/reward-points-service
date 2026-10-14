package com.rewards.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Request body containing the transactions to calculate. */
public record RewardCalculationRequest(
        @NotNull(message = "transactions is required")
        @NotEmpty(message = "transactions must contain at least one transaction")
        List<@Valid TransactionRequest> transactions
) {
}
