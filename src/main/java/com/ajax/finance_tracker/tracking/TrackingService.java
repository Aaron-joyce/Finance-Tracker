package com.ajax.finance_tracker.tracking;

import com.ajax.finance_tracker.model.Category;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TransactionRepository repository;
    private static final List<Category> INCOME_CATEGORIES = List.of(Category.SALARY);

    private final org.springframework.context.ApplicationEventPublisher publisher;

    @Transactional
    public Transaction addTransaction(Transaction transaction) {
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(LocalDateTime.now());
        }
        Transaction saved = repository.save(transaction);
        publisher.publishEvent(new TransactionCreatedEvent(saved));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getRecentTransactions(String accountId) {
        return repository.findByAccountIdOrderByTimestampDesc(accountId).stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions(String accountId) {
        return repository.findByAccountIdOrderByTimestampDesc(accountId);
    }

    @Transactional(readOnly = true)
    public Map<Category, BigDecimal> getMonthlySpendingByCategory(String accountId) {
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = repository.findByAccountIdOrderByTimestampDesc(accountId);

        return transactions.stream()
                .filter(t -> t.getTimestamp().getMonth() == now.getMonth()
                        && t.getTimestamp().getYear() == now.getYear())
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyTotalSpending(String accountId) {
        LocalDateTime now = LocalDateTime.now();
        return repository.findByAccountIdAndCategoryNotInOrderByTimestampDesc(accountId, INCOME_CATEGORIES).stream()
                .filter(t -> t.getTimestamp().getMonth() == now.getMonth()
                        && t.getTimestamp().getYear() == now.getYear())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTodaysSpending(String accountId) {
        LocalDateTime start = LocalDateTime.now().with(java.time.LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(java.time.LocalTime.MAX);

        return repository.findByAccountIdAndCategoryNotInOrderByTimestampDesc(accountId, INCOME_CATEGORIES).stream()
                .filter(t -> t.getTimestamp().isAfter(start) && t.getTimestamp().isBefore(end))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Map<java.time.LocalDate, BigDecimal> getDailySpendingLast7Days(String accountId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7).with(java.time.LocalTime.MIN);

        // Init map with last 7 days dates and zero amounts
        Map<java.time.LocalDate, BigDecimal> dailyMap = new java.util.TreeMap<>();
        for (int i = 0; i < 7; i++) {
            dailyMap.put(java.time.LocalDate.now().minusDays(i), BigDecimal.ZERO);
        }

        List<Transaction> transactions = repository.findByAccountIdAndCategoryNotInOrderByTimestampDesc(accountId,
                INCOME_CATEGORIES);

        transactions.stream()
                .filter(t -> t.getTimestamp().isAfter(sevenDaysAgo))
                .forEach(t -> {
                    java.time.LocalDate date = t.getTimestamp().toLocalDate();
                    dailyMap.merge(date, t.getAmount(), BigDecimal::add);
                });

        return dailyMap;
    }
}
