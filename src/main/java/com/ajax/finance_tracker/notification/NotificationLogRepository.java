package com.ajax.finance_tracker.notification;

import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface NotificationLogRepository extends CrudRepository<NotificationLog, Long> {
    Optional<NotificationLog> findByUserEmailAndCategoryAndSentDate(String userEmail, String category,
            LocalDate sentDate);
}
