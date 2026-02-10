package com.ajax.finance_tracker.notification;

import com.ajax.finance_tracker.analysis.StatisticsService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailySummaryScheduler {

    private final NotificationRepository notificationRepository;
    private final StatisticsService statisticsService;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 * * * ?") // Run every hour (simplification for "time" check)
    public void sendDailySummaries() {
        log.info("Starting daily summary job check");
        java.time.LocalTime now = java.time.LocalTime.now().withSecond(0).withNano(0);

        notificationRepository.findAll().forEach(settings -> {
            if (Boolean.TRUE.equals(settings.getDailySummaryEnabled())) {

                // Check if current time matches preference (approx)
                // In real app, we might query db for users with matching time
                java.time.LocalTime prefTime = settings.getDailySummaryTime();
                if (prefTime != null && prefTime.withSecond(0).withNano(0).equals(now)) {

                    String accountId = settings.getAccountName();
                    String email = settings.getEmail();

                    if (email != null && !email.isEmpty()) {
                        BigDecimal todaysSpend = statisticsService.getTodaysSpend(accountId);
                        if (todaysSpend.compareTo(BigDecimal.ZERO) > 0) {
                            String subject = "Daily Spending Summary";
                            String text = String.format("Hi %s, you spent $%s today.", accountId, todaysSpend);
                            emailService.sendSimpleMessage(email, subject, text);
                        }
                    }
                }
            }
        });
        log.info("Completed daily summary job check");
    }
}
