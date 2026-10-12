package com.example.rewards.exception;

import java.time.Instant;
import java.util.List;

/** Consistent API error response. */
public record ApiError(Instant timestamp, int status, String error, List<String> details) {
}
