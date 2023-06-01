package com.bank.neoris.repository;

import com.bank.neoris.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);

}
