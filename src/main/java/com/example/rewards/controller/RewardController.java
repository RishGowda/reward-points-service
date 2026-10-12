package com.example.rewards.controller;

import com.example.rewards.model.RewardCalculationRequest;
import com.example.rewards.model.RewardCalculationResponse;
import com.example.rewards.service.RewardCalculationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HTTP endpoints for reward calculations. */
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardCalculationService rewardCalculationService;

    public RewardController(RewardCalculationService rewardCalculationService) {
        this.rewardCalculationService = rewardCalculationService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<RewardCalculationResponse> calculate(
            @Valid @RequestBody RewardCalculationRequest request) {
        return ResponseEntity.ok(rewardCalculationService.calculate(request));
    }
}
