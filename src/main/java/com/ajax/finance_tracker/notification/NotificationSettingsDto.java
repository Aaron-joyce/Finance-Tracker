package com.ajax.finance_tracker.notification;

import java.time.LocalTime;

public record NotificationSettingsDto(
        Boolean dailySummaryEnabled,
        LocalTime dailySummaryTime,
        Boolean budgetAlertEnabled,
        Integer budgetThreshold,
        String emailAddress) {
}
