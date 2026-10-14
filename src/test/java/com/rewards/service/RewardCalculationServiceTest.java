package com.rewards.service;

import com.rewards.model.RewardCalculationRequest;
import com.rewards.model.RewardCalculationResponse;
import com.rewards.model.TransactionRequest;
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
		assertThat(service.calculatePoints(new BigDecimal("60"))).isEqualTo(10);
		assertThat(service.calculatePoints(new BigDecimal("75"))).isEqualTo(25);
		assertThat(service.calculatePoints(new BigDecimal("100"))).isEqualTo(50);
		assertThat(service.calculatePoints(new BigDecimal("120"))).isEqualTo(90);
		assertThat(service.calculatePoints(new BigDecimal("120.99"))).isEqualTo(90);
		assertThat(service.calculatePoints(new BigDecimal("200"))).isEqualTo(250);
	}

	@Test
	void rejectsNegativeAmount() {
		assertThatThrownBy(() -> service.calculatePoints(new BigDecimal("-1")))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void handlesFractionalAmountsUsingWholeDollarsOnly() {
		assertThat(service.calculatePoints(new BigDecimal("49.99"))).isZero();
		assertThat(service.calculatePoints(new BigDecimal("50.99"))).isZero();
		assertThat(service.calculatePoints(new BigDecimal("51.99"))).isEqualTo(1);
		assertThat(service.calculatePoints(new BigDecimal("99.99"))).isEqualTo(49);
		assertThat(service.calculatePoints(new BigDecimal("100.99"))).isEqualTo(50);
		assertThat(service.calculatePoints(new BigDecimal("101.99"))).isEqualTo(52);
	}

	@Test
	void rejectsNullAmount() {
		assertThatThrownBy(() -> service.calculatePoints(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("amount must be zero or greater");
	}

	@Test
	void aggregatesByCustomerAndCalendarMonth() {
		RewardCalculationRequest request = new RewardCalculationRequest(List.of(tx("C200", "2026-06-01", "75"),
				tx("C100", "2026-06-15", "120"), tx("C200", "2026-06-30", "60"), tx("C100", "2026-07-01", "100")));

		RewardCalculationResponse response = service.calculate(request);

		assertThat(response.customers()).hasSize(2);
		assertThat(response.customers()).extracting("customerId").containsExactly("C100", "C200");
		assertThat(response.customers().get(0).monthlyRewards()).extracting("points").containsExactly(90L, 50L);
		assertThat(response.customers().get(0).totalPoints()).isEqualTo(140L);
		assertThat(response.customers().get(1).monthlyRewards()).extracting("points").containsExactly(35L);
		assertThat(response.customers().get(1).totalPoints()).isEqualTo(35L);
	}

	@Test
	void trimsCustomerIdsBeforeGrouping() {
		RewardCalculationRequest request = new RewardCalculationRequest(
				List.of(tx(" C100 ", "2026-06-01", "75"), tx("C100", "2026-06-02", "60")));

		RewardCalculationResponse response = service.calculate(request);

		assertThat(response.customers()).hasSize(1);
		assertThat(response.customers().get(0).customerId()).isEqualTo("C100");
		assertThat(response.customers().get(0).totalPoints()).isEqualTo(35L);
	}

	@Test
	void matchesReferenceMonthlyRewardsAndTotal() {
		RewardCalculationRequest request = new RewardCalculationRequest(List.of(tx("C100", "2026-06-15", "120"),
				tx("C100", "2026-06-28", "75"), tx("C100", "2026-07-10", "200"), tx("C100", "2026-07-20", "45"),
				tx("C100", "2026-08-05", "130"), tx("C100", "2026-08-15", "60")));

		RewardCalculationResponse response = service.calculate(request);

		assertThat(response.customers()).hasSize(1);
		assertThat(response.customers().get(0).monthlyRewards()).extracting("month")
				.containsExactly(YearMonth.of(2026, 6), YearMonth.of(2026, 7), YearMonth.of(2026, 8));
		assertThat(response.customers().get(0).monthlyRewards()).extracting("points").containsExactly(115L, 250L, 120L);
		assertThat(response.customers().get(0).totalPoints()).isEqualTo(485);
	}

	private TransactionRequest tx(String customerId, String date, String amount) {
		return new TransactionRequest(customerId, LocalDate.parse(date), new BigDecimal(amount));
	}
}
