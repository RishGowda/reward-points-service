package com.rewards.model;

import java.time.YearMonth;

/** Reward points earned by a customer in one calendar month. */
public record MonthlyReward(YearMonth month, long points) {
}
