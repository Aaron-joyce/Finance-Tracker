package com.ajax.finance_tracker.web;

import com.ajax.finance_tracker.analysis.BudgetVsActualService;
import com.ajax.finance_tracker.budget.Budget;
import com.ajax.finance_tracker.budget.BudgetRule;
import com.ajax.finance_tracker.budget.BudgetService;
import com.ajax.finance_tracker.model.Category;
import com.ajax.finance_tracker.tracking.TrackingService;
import com.ajax.finance_tracker.tracking.Transaction;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final BudgetService budgetService;
    private final TrackingService trackingService;
    private final BudgetVsActualService analysisService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/dashboard/{username}")
    public String dashboard(@PathVariable String username, Model model) {
        // Tab 1: Plan
        Budget budget = budgetService.getBudget(username);
        List<BudgetRule> rules = budget.getRules();

        List<BudgetRule> incomeRules = rules.stream()
                .filter(r -> r.getType() == BudgetRule.Type.INCOME)
                .toList();
        List<BudgetRule> expenseRules = rules.stream()
                .filter(r -> r.getType() == BudgetRule.Type.EXPENSE)
                .toList();

        // Chart Data: Planned Distribution (Expenses Only)
        // Map<Category, BigDecimal> -> Javascript Arrays
        // We will pass the raw map or list to thymeleaf to generate JS array

        // Tab 2: Tracker
        List<Transaction> recentTransactions = trackingService.getRecentTransactions(username);
        BigDecimal monthlySpending = trackingService.getMonthlyTotalSpending(username);
        BigDecimal todaysSpending = trackingService.getTodaysSpending(username);
        var dailySpendingLast7Days = trackingService.getDailySpendingLast7Days(username);

        List<String> dailySpendingKeys = dailySpendingLast7Days.keySet().stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("dd MMM")))
                .toList();

        // Tab 3: Analysis
        List<BudgetVsActualService.VarianceDto> varianceReport = analysisService.getMonthlyVariance(username);

        List<String> chartCategories = new ArrayList<>();
        List<BigDecimal> budgetValues = new ArrayList<>();
        List<BigDecimal> actualValues = new ArrayList<>();

        for (Category cat : Category.values()) {
            chartCategories.add(cat.name());
            var variance = varianceReport.stream()
                    .filter(v -> v.getCategory() == cat)
                    .findFirst();

            if (variance.isPresent()) {
                budgetValues.add(variance.get().getPlanned());
                actualValues.add(variance.get().getActual());
            } else {
                budgetValues.add(BigDecimal.ZERO);
                actualValues.add(BigDecimal.ZERO);
            }
        }

        // Common Attributes
        model.addAttribute("username", username);
        model.addAttribute("categories", Category.values());

        // Tab 1 Data
        model.addAttribute("budgetRules", rules); // Keep for legacy if needed, but we use split lists
        model.addAttribute("incomeRules", incomeRules);
        model.addAttribute("expenseRules", expenseRules);
        model.addAttribute("projectedSavings", budget.getTotalProjectedSavings());

        // Tab 2 Data
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("monthlySpending", monthlySpending);
        model.addAttribute("todaysSpending", todaysSpending);
        model.addAttribute("dailySpendingKeys", dailySpendingKeys);
        model.addAttribute("dailySpendingValues", dailySpendingLast7Days.values());

        // Tab 3 Data
        model.addAttribute("varianceReport", varianceReport);
        model.addAttribute("chartCategories", chartCategories);
        model.addAttribute("budgetValues", budgetValues);
        model.addAttribute("actualValues", actualValues);

        return "dashboard";
    }
}
