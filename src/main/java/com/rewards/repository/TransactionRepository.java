package com.rewards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rewards.entity.TransactionEntity;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByCustomerIdOrderByTransactionDateAsc(Long customerId);
}
