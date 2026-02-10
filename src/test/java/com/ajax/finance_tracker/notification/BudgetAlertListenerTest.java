package com.ajax.finance_tracker.notification;

import com.ajax.finance_tracker.analysis.StatisticsService;
import com.ajax.finance_tracker.model.Category;
import com.ajax.finance_tracker.tracking.Transaction;
import com.ajax.finance_tracker.tracking.TransactionCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetAlertListenerTest {

    @Mock
    private StatisticsService statisticsService;
    @Mock
    private EmailService emailService;
    @Mock
    private NotificationLogRepository notificationLogRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private BudgetAlertListener listener;

    @Test
    void handleTransactionCreated_shouldSendAlert_whenOverBudgetAndNotSentToday() {
        Transaction transaction = new Transaction();
        transaction.setAccountId("user1");
        transaction.setCategory(Category.FOOD);
        TransactionCreatedEvent event = new TransactionCreatedEvent(transaction);

        NotificationSettings settings = new NotificationSettings();
        settings.setEmail("user1@example.com");

        when(notificationRepository.findByAccountName("user1")).thenReturn(Optional.of(settings));
        when(statisticsService.getBudgetStatus("user1", "FOOD")).thenReturn(0.95);
        when(notificationLogRepository.findByUserEmailAndCategoryAndSentDate(anyString(), anyString(),
                any(LocalDate.class)))
                .thenReturn(Optional.empty());

        listener.handleTransactionCreated(event);

        verify(emailService).sendSimpleMessage(eq("user1@example.com"), anyString(), anyString());
        verify(notificationLogRepository).save(any(NotificationLog.class));
    }

    @Test
    void handleTransactionCreated_shouldNotSendAlert_whenUnderBudget() {
        Transaction transaction = new Transaction();
        transaction.setAccountId("user1");
        transaction.setCategory(Category.FOOD);
        TransactionCreatedEvent event = new TransactionCreatedEvent(transaction);

        NotificationSettings settings = new NotificationSettings();
        settings.setEmail("user1@example.com");

        when(notificationRepository.findByAccountName("user1")).thenReturn(Optional.of(settings));
        when(statisticsService.getBudgetStatus("user1", "FOOD")).thenReturn(0.80);

        listener.handleTransactionCreated(event);

        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }
}
