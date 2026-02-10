package com.ajax.finance_tracker.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService service;

    @Autowired
    private NotificationRepository repository;

    @Test
    @Transactional
    void getSettings_CreatesDefaultSettingsIfMissing() {
        String accountName = "new_user_without_settings";

        // Ensure no settings exist initially
        assertTrue(repository.findByAccountName(accountName).isEmpty());

        // Call method under test
        NotificationSettings settings = service.getSettings(accountName);

        // Verify settings are created and returned
        assertNotNull(settings);
        assertEquals(accountName, settings.getAccountName());
        assertEquals("Active", settings.getScheduledNotifications().get("Weekly Backup"));
        assertEquals("Active", settings.getScheduledNotifications().get("Reminders"));

        // Verify persistence
        assertTrue(repository.findByAccountName(accountName).isPresent());
    }

    @Test
    @Transactional
    void addNotification_UpdatesSettings() {
        String accountName = "user_updating_settings";

        // Trigger default creation first
        service.getSettings(accountName);

        // Add a new notification setting
        service.addNotification(accountName, "New Alert", "Enabled");

        // Verify update
        NotificationSettings updatedSettings = service.getSettings(accountName);
        assertEquals("Enabled", updatedSettings.getScheduledNotifications().get("New Alert"));

        // Verify defaults persist
        assertEquals("Active", updatedSettings.getScheduledNotifications().get("Weekly Backup"));
    }
}
