package com.rewards.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/** A single customer purchase supplied to the calculation endpoint. */
public record TransactionRequest(@NotBlank(message = "customerId must not be blank") String customerId,

		@NotNull(message = "transactionDate is required") LocalDate transactionDate,

		@NotNull(message = "amount is required") @PositiveOrZero(message = "amount must be zero or greater") BigDecimal amount) {
}
