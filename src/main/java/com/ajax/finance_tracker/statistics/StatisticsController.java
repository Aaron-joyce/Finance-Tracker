package com.ajax.finance_tracker.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
class StatisticsController {

    private final StatisticsService service;

    @GetMapping("/current")
    Statistic getCurrent(@RequestParam String accountId) {
        return service.getStatistic(accountId);
    }

    @PostMapping("/items")
    void addItem(@RequestParam String accountId, @RequestBody Item item) {
        service.addItem(accountId, item);
    }
}
