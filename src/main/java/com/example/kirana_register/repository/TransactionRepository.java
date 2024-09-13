package com.example.kirana_register.repository;

import com.example.kirana_register.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByUserIdAndTimestampBetween(String userId, LocalDateTime start, LocalDateTime end);
}
