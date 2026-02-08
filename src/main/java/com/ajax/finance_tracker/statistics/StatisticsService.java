package com.ajax.finance_tracker.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticRepository repository;

    @Transactional(readOnly = true)
    public Statistic getStatistic(String accountId) {
        return repository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No statistics found for account: " + accountId));
    }

    @Transactional
    public void addItem(String accountId, Item item) {
        Statistic stat = getStatistic(accountId);
        stat.getItems().add(item);
        recalculateSavings(stat);
        repository.save(stat);
    }

    private void recalculateSavings(Statistic stat) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Item item : stat.getItems()) {
            BigDecimal monthlyAmount = item.getAmount();
            if (item.getPeriod() == Item.Period.YEARLY) {
                monthlyAmount = item.getAmount().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            }

            if (item.getType() == Item.Type.INCOME) {
                totalIncome = totalIncome.add(monthlyAmount);
            } else {
                totalExpenses = totalExpenses.add(monthlyAmount);
            }
        }
        stat.setTotalAmount(totalIncome.subtract(totalExpenses));
    }
}
