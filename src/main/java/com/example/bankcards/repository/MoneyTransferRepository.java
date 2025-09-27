package com.example.bankcards.repository;

import com.example.bankcards.entity.MoneyTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyTransferRepository extends JpaRepository<MoneyTransfer, Long> {
}
