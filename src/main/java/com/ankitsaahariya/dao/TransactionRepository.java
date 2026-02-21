package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository  extends JpaRepository<Transaction,Long> {
}
