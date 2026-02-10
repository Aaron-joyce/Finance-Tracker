package com.ajax.finance_tracker.notification;

import com.ajax.finance_tracker.analysis.StatisticsService;
import com.ajax.finance_tracker.tracking.Transaction;
import com.ajax.finance_tracker.tracking.TransactionCreatedEvent;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BudgetAlertListener {

    private final StatisticsService statisticsService;
    private final EmailService emailService;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    @Async
    @Transactional
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        Transaction transaction = event.getTransaction();
        String accountId = transaction.getAccountId();
        String category = transaction.getCategory().name();

        // Check if alerts are enabled/configured for this user
        // Ideally we associate accountId with a User/Email.
        // For this task, we will try to look up email from NotificationSettings.

        notificationRepository.findByAccountName(accountId).ifPresent(settings -> {
            String email = settings.getEmail();
            if (email == null || email.isEmpty()) {
                return;
            }

            // Check enabled flag
            if (Boolean.TRUE.equals(settings.getBudgetAlertEnabled())) {

                double usedPercent = statisticsService.getBudgetStatus(accountId, category);

                // Use configured threshold or default 90
                int threshold = settings.getBudgetThreshold() != null ? settings.getBudgetThreshold() : 90;

                if (usedPercent * 100 > threshold) {
                    LocalDate today = LocalDate.now();

                    // Check if already sent
                    if (notificationLogRepository.findByUserEmailAndCategoryAndSentDate(email, category, today)
                            .isPresent()) {
                        log.info("Alert already sent for {} today. Skipping.", category);
                        return;
                    }

                    String subject = "Budget Alert: " + category;
                    String message = String.format("You have reached %.0f%% of your %s budget!", usedPercent * 100,
                            category);

                    emailService.sendSimpleMessage(email, subject, message);

                    notificationLogRepository.save(NotificationLog.builder()
                            .userEmail(email)
                            .category(category)
                            .sentDate(today)
                            .build());
                }
            }
        });
    }
}
