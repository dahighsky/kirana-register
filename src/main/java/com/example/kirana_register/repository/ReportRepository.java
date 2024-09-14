package com.example.kirana_register.repository;

import com.example.kirana_register.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ReportRepository extends MongoRepository<Report, String> {
    Optional<Report> findByUserIdAndTypeAndStartDateAndEndDate(
            String userId, Report.ReportType type, LocalDateTime startDate, LocalDateTime endDate);
}