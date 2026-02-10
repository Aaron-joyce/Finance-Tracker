package com.ajax.finance_tracker.analysis;

import com.ajax.finance_tracker.budget.Budget;
import com.ajax.finance_tracker.budget.BudgetRule;
import com.ajax.finance_tracker.budget.BudgetService;
import com.ajax.finance_tracker.model.Category;
import com.ajax.finance_tracker.tracking.TrackingService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetVsActualService {

    private final BudgetService budgetService;
    private final TrackingService trackingService;

    public List<VarianceDto> getMonthlyVariance(String accountId) {
        // 1. Get Projected (Planned) Amount per Category
        Budget budget = budgetService.getBudget(accountId);
        Map<Category, BigDecimal> plannedMap = new EnumMap<>(Category.class);

        for (BudgetRule rule : budget.getRules()) {
            BigDecimal monthlyAmount = getMonthlyAmount(rule);
            plannedMap.merge(rule.getCategory(), monthlyAmount, BigDecimal::add);
        }

        // 2. Get Actual Spending per Category for current month
        Map<Category, BigDecimal> actualMap = trackingService.getMonthlySpendingByCategory(accountId);

        // 3. Merge and Create Variance Report
        List<VarianceDto> varianceReport = new ArrayList<>();

        for (Category cat : Category.values()) {
            if (cat == Category.OTHER && !plannedMap.containsKey(cat) && !actualMap.containsKey(cat))
                continue; // Skip empty others

            BigDecimal planned = plannedMap.getOrDefault(cat, BigDecimal.ZERO);
            BigDecimal actual = actualMap.getOrDefault(cat, BigDecimal.ZERO);

            // Only add if there is some activity
            if (planned.compareTo(BigDecimal.ZERO) == 0 && actual.compareTo(BigDecimal.ZERO) == 0)
                continue;

            // Determine Type (heuristic: if any rule for this category is INCOME, treat as
            // INCOME)
            BudgetRule.Type type = budget.getRules().stream()
                    .filter(r -> r.getCategory() == cat)
                    .map(BudgetRule::getType)
                    .findFirst()
                    .orElse(BudgetRule.Type.EXPENSE);

            // Override for known Income categories if no rules exist
            if (planned.compareTo(BigDecimal.ZERO) == 0 && cat == Category.SALARY) {
                type = BudgetRule.Type.INCOME;
            }

            BigDecimal remaining;
            if (type == BudgetRule.Type.INCOME) {
                // For Income: Remaining = Actual - Planned (Excess income is positive/good)
                remaining = actual.subtract(planned);
            } else {
                // For Expense: Remaining = Planned - Actual (Savings is positive/good)
                remaining = planned.subtract(actual);
            }

            varianceReport.add(VarianceDto.builder()
                    .category(cat)
                    .planned(planned)
                    .actual(actual)
                    .remaining(remaining)
                    .percentUsed(calculatePercent(actual, planned))
                    .type(type)
                    .build());
        }

        return varianceReport;
    }

    private BigDecimal getMonthlyAmount(BudgetRule rule) {
        BigDecimal amount = rule.getAmount();
        if (amount == null)
            return BigDecimal.ZERO;

        switch (rule.getPeriod()) {
            case DAILY:
                return amount.multiply(BigDecimal.valueOf(30));
            case YEARLY:
                return amount.divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);
            default: // MONTHLY
                return amount;
        }
    }

    private double calculatePercent(BigDecimal actual, BigDecimal planned) {
        if (planned.compareTo(BigDecimal.ZERO) == 0)
            return actual.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        return actual.divide(planned, 2, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    @Data
    @Builder
    public static class VarianceDto {
        private Category category;
        private BigDecimal planned;
        private BigDecimal actual;
        private BigDecimal remaining;
        private double percentUsed;
        private BudgetRule.Type type;
    }
}
