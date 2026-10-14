package com.rewards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void postCalculateReturnsExpectedMonthlyAndTotalPoints() throws Exception {
		String request = "{\"transactions\":["
				+ "{\"customerId\":\"C100\",\"transactionDate\":\"2026-06-15\",\"amount\":120},"
				+ "{\"customerId\":\"C100\",\"transactionDate\":\"2026-06-28\",\"amount\":75},"
				+ "{\"customerId\":\"C100\",\"transactionDate\":\"2026-07-10\",\"amount\":200},"
				+ "{\"customerId\":\"C100\",\"transactionDate\":\"2026-07-20\",\"amount\":45},"
				+ "{\"customerId\":\"C100\",\"transactionDate\":\"2026-08-05\",\"amount\":130},"
				+ "{\"customerId\":\"C100\",\"transactionDate\":\"2026-08-15\",\"amount\":60}]}";

		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andExpect(jsonPath("$.customers[0].monthlyRewards[0].points").value(115))
				.andExpect(jsonPath("$.customers[0].monthlyRewards[1].points").value(250))
				.andExpect(jsonPath("$.customers[0].monthlyRewards[2].points").value(120))
				.andExpect(jsonPath("$.customers[0].totalPoints").value(485));
	}

	@Test
	void getAllRewardsReturnsReferenceSampleForFirstCustomer() throws Exception {
		mockMvc.perform(get("/api/rewards")).andExpect(status().isOk()).andExpect(jsonPath("$[0].customerId").value(1))
				.andExpect(jsonPath("$[0].customerName").value("Harish"))
				.andExpect(jsonPath("$[0].monthlyRewards[0].points").value(115))
				.andExpect(jsonPath("$[0].monthlyRewards[1].points").value(250))
				.andExpect(jsonPath("$[0].monthlyRewards[2].points").value(120))
				.andExpect(jsonPath("$[0].totalPoints").value(485));
	}

	@Test
	void getOneCustomerReturnsExpectedSummary() throws Exception {
		mockMvc.perform(get("/api/rewards/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.customerName").value("Harish")).andExpect(jsonPath("$.totalPoints").value(485));
	}

	@Test
	void unknownCustomerReturnsNotFound() throws Exception {
		mockMvc.perform(get("/api/rewards/999")).andExpect(status().isNotFound());
	}

	@Test
	void missingTransactionsFieldReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void nullTransactionsReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transactions\":null}")).andExpect(status().isBadRequest());
	}

	@Test
	void missingCustomerIdReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transactions\":[{\"transactionDate\":\"2026-06-15\",\"amount\":120}]}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Validation failed"));
	}

	@Test
	void blankCustomerIdReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content(
				"{\"transactions\":[{\"customerId\":\"   \",\"transactionDate\":\"2026-06-15\",\"amount\":120}]}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void missingTransactionDateReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transactions\":[{\"customerId\":\"C100\",\"amount\":120}]}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void invalidTransactionDateFormatReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content(
				"{\"transactions\":[{\"customerId\":\"C100\",\"transactionDate\":\"15-06-2026\",\"amount\":120}]}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Invalid request body"));
	}

	@Test
	void missingAmountReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transactions\":[{\"customerId\":\"C100\",\"transactionDate\":\"2026-06-15\"}]}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void negativeAmountReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content(
				"{\"transactions\":[{\"customerId\":\"C100\",\"transactionDate\":\"2026-06-15\",\"amount\":-10}]}"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Validation failed"));
	}

	@Test
	void malformedJsonReturnsBadRequest() throws Exception {
		mockMvc.perform(
				post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content("{\"transactions\":["))
				.andExpect(status().isBadRequest());
	}

	@Test
	void nullTransactionItemReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transactions\":[null]}")).andExpect(status().isBadRequest());
	}

	@Test
	void nullAmountReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content(
				"{\"transactions\":[{\"customerId\":\"C100\",\"transactionDate\":\"2026-06-15\",\"amount\":null}]}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void impossibleCalendarDateReturnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content(
				"{\"transactions\":[{\"customerId\":\"C100\",\"transactionDate\":\"2026-02-30\",\"amount\":120}]}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void unsupportedContentTypeReturnsUnsupportedMediaType() throws Exception {
		mockMvc.perform(post("/api/rewards/calculate").contentType(MediaType.TEXT_PLAIN).content("not json"))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	void emptyTransactionsReturnsBadRequest() throws Exception {
		mockMvc.perform(
				post("/api/rewards/calculate").contentType(MediaType.APPLICATION_JSON).content("{\"transactions\":[]}"))
				.andExpect(status().isBadRequest());
	}
}