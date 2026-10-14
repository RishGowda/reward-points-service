package com.rewards.controller;

import com.rewards.model.RewardCalculationRequest;
import com.rewards.model.RewardCalculationResponse;
import com.rewards.service.RewardCalculationService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints for reward calculations. */
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

	private final RewardCalculationService rewardCalculationService;

	public RewardController(RewardCalculationService rewardCalculationService) {
		this.rewardCalculationService = rewardCalculationService;
	}

	@PostMapping("/calculate")
	public ResponseEntity<RewardCalculationResponse> calculate(@Valid @RequestBody RewardCalculationRequest request) {
		return ResponseEntity.ok(rewardCalculationService.calculate(request));
	}
}
