package com.ajax.finance_tracker.budget;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository repository;

    @Transactional
    public Budget getBudget(String accountId) {
        return repository.findByAccountId(accountId)
                .orElseGet(() -> {
                    Budget newBudget = new Budget();
                    newBudget.setAccountId(accountId);
                    newBudget.setTotalProjectedSavings(BigDecimal.ZERO);
                    return repository.save(newBudget);
                });
    }

    @Transactional
    public void addRule(String accountId, BudgetRule rule) {
        Budget budget = getBudget(accountId);
        budget.getRules().add(rule);
        recalculateProjectedSavings(budget);
        repository.save(budget);
    }

    private void recalculateProjectedSavings(Budget budget) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (BudgetRule rule : budget.getRules()) {
            // Calculate monthly projection
            BigDecimal monthlyAmount = getMonthlyAmount(rule);

            if (rule.getType() == BudgetRule.Type.INCOME) {
                totalIncome = totalIncome.add(monthlyAmount);
            } else {
                totalExpenses = totalExpenses.add(monthlyAmount);
            }
        }
        // Projected Monthly Savings
        budget.setTotalProjectedSavings(totalIncome.subtract(totalExpenses));
    }

    public BigDecimal getMonthlyAmount(BudgetRule rule) {
        BigDecimal amount = rule.getAmount();
        if (amount == null)
            return BigDecimal.ZERO;

        switch (rule.getPeriod()) {
            case DAILY:
                return amount.multiply(BigDecimal.valueOf(30));
            case YEARLY:
                // Standard financial projection (12 months)
                return amount.divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);
            default: // MONTHLY
                return amount;
        }
    }
}
