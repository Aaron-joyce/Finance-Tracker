package com.ajax.finance_tracker.notification;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface NotificationRepository extends CrudRepository<NotificationSettings, Long> {
    Optional<NotificationSettings> findByAccountName(String accountName);
}
