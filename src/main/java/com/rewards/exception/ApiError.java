package com.rewards.exception;

import java.time.Instant;
import java.util.List;

/** API error response. */
public record ApiError(Instant timestamp, int status, String error, List<String> details) {
}
