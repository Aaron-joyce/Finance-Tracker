package com.ajax.finance_tracker.analysis;

import com.ajax.finance_tracker.model.Category;
import com.ajax.finance_tracker.tracking.TrackingService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BudgetVsActualService budgetService;
    private final TrackingService trackingService;

    public double getBudgetStatus(String accountId, String categoryName) {
        try {
            Category category = Category.valueOf(categoryName);
            List<BudgetVsActualService.VarianceDto> variance = budgetService.getMonthlyVariance(accountId);

            return variance.stream()
                    .filter(v -> v.getCategory() == category)
                    .findFirst()
                    .map(BudgetVsActualService.VarianceDto::getPercentUsed)
                    .orElse(0.0) / 100.0;
        } catch (IllegalArgumentException e) {
            return 0.0;
        }
    }

    public BigDecimal getTodaysSpend(String accountId) {
        return trackingService.getTodaysSpending(accountId);
    }
}
