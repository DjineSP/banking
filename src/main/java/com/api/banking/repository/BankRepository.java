package com.api.banking.repository;

import com.api.banking.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
    boolean existsByCode(String code);
    Optional<Bank> findByCode(String code);
}
