package com.ajax.finance_tracker.notification;

import com.ajax.finance_tracker.account.UserRegisteredEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    @ApplicationModuleListener
    void on(UserRegisteredEvent event) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("Weekly Backup", "Active");
        defaults.put("Reminders", "Active");

        NotificationSettings settings = new NotificationSettings(null, event.username(), event.email(), defaults);
        repository.save(settings);
    }

    @Transactional(readOnly = true)
    public NotificationSettings getSettings(String accountName) {
        return repository.findByAccountName(accountName)
                .orElseThrow(() -> new IllegalArgumentException("No settings found for account: " + accountName));
    }
}
