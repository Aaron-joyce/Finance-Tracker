package com.ajax.finance_tracker.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
class BudgetController {

    private final BudgetService service;

    @GetMapping
    Budget getBudget(@RequestParam String accountId) {
        return service.getBudget(accountId);
    }

    @PostMapping("/rules")
    void addRule(@RequestParam String accountId, @RequestBody BudgetRule rule) {
        service.addRule(accountId, rule);
    }
}
