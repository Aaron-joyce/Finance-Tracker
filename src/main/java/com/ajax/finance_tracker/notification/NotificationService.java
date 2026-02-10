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

        NotificationSettings settings = new NotificationSettings();
        settings.setAccountName(event.username());
        settings.setEmail(event.email());
        settings.setScheduledNotifications(defaults);
        settings.setDailySummaryEnabled(false);
        settings.setBudgetAlertEnabled(true);
        settings.setBudgetThreshold(90);

        repository.save(settings);
    }

    @Transactional
    public NotificationSettings getSettings(String accountName) {
        return repository.findByAccountName(accountName)
                .orElseGet(() -> {
                    Map<String, String> defaults = new HashMap<>();
                    defaults.put("Weekly Backup", "Active");
                    defaults.put("Reminders", "Active");

                    NotificationSettings settings = new NotificationSettings();
                    settings.setAccountName(accountName);
                    settings.setScheduledNotifications(defaults);
                    settings.setDailySummaryEnabled(false);
                    settings.setBudgetAlertEnabled(true);
                    settings.setBudgetThreshold(90);

                    return repository.save(settings);
                });
    }

    @Transactional
    public void addNotification(String accountName, String key, String value) {
        NotificationSettings settings = getSettings(accountName);
        settings.getScheduledNotifications().put(key, value);
        repository.save(settings);
    }

    @Transactional
    public void updateSettings(String accountName, NotificationSettingsDto dto) {
        NotificationSettings settings = getSettings(accountName);
        settings.setDailySummaryEnabled(dto.dailySummaryEnabled());
        settings.setDailySummaryTime(dto.dailySummaryTime());
        settings.setBudgetAlertEnabled(dto.budgetAlertEnabled());
        settings.setBudgetThreshold(dto.budgetThreshold());
        // Email is typically updated via account profile, but we can allow it here if
        // needed
        // settings.setEmail(dto.emailAddress());
        repository.save(settings);
    }
}
