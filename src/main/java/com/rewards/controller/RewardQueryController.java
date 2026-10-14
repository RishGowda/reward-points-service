package com.rewards.controller;

import com.rewards.dto.RewardSummaryResponse;
import com.rewards.service.RewardQueryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardQueryController {
    private final RewardQueryService rewardQueryService;

    public RewardQueryController(RewardQueryService rewardQueryService) {
        this.rewardQueryService = rewardQueryService;
    }

    @GetMapping
    public ResponseEntity<List<RewardSummaryResponse>> getAllRewards() {
        return ResponseEntity.ok(rewardQueryService.getAllRewards());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardSummaryResponse> getRewardsByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(rewardQueryService.getRewardsByCustomer(customerId));
    }
}
