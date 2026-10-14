package com.rewards.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rewards.entity.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
