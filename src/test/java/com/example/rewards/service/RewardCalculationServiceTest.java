package com.example.rewards.service;

import com.example.rewards.model.RewardCalculationRequest;
import com.example.rewards.model.RewardCalculationResponse;
import com.example.rewards.model.TransactionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RewardCalculationServiceTest {

    private final RewardCalculationService service = new RewardCalculationService();

    @Test
    void calculatesPointsAtThresholds() {
        assertThat(service.calculatePoints(new BigDecimal("0"))).isZero();
        assertThat(service.calculatePoints(new BigDecimal("50"))).isZero();
        assertThat(service.calculatePoints(new BigDecimal("75"))).isEqualTo(25);
        assertThat(service.calculatePoints(new BigDecimal("100"))).isEqualTo(50);
        assertThat(service.calculatePoints(new BigDecimal("120"))).isEqualTo(90);
        assertThat(service.calculatePoints(new BigDecimal("120.99"))).isEqualTo(90);
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> service.calculatePoints(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("amount must be zero or greater");
    }

    @Test
    void groupsMultipleCustomersAndMonths() {
        RewardCalculationRequest request = new RewardCalculationRequest(List.of(
                tx("C100", "2026-01-05", "120"),
                tx("C100", "2026-01-20", "75"),
                tx("C100", "2026-02-02", "50"),
                tx("C200", "2026-01-10", "200")
        ));

        RewardCalculationResponse response = service.calculate(request);

        assertThat(response.customers()).hasSize(2);
        assertThat(response.customers().get(0).customerId()).isEqualTo("C100");
        assertThat(response.customers().get(0).monthlyRewards()).containsExactly(
                new com.example.rewards.model.MonthlyReward(YearMonth.of(2026, 1), 115),
                new com.example.rewards.model.MonthlyReward(YearMonth.of(2026, 2), 0));
        assertThat(response.customers().get(0).totalPoints()).isEqualTo(115);
        assertThat(response.customers().get(1).totalPoints()).isEqualTo(250);
    }

    private TransactionRequest tx(String customerId, String date, String amount) {
        return new TransactionRequest(customerId, LocalDate.parse(date), new BigDecimal(amount));
    }
}
