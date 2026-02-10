package com.ajax.finance_tracker.tracking;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
class TrackingController {

    private final TrackingService service;

    @GetMapping("/transactions")
    List<Transaction> getRecentTransactions(@RequestParam String accountId) {
        return service.getRecentTransactions(accountId);
    }

    @PostMapping("/transactions")
    void addTransaction(@RequestParam String accountId, @RequestBody Transaction transaction) {
        transaction.setAccountId(accountId);
        if (transaction.getTimestamp() == null)
            transaction.setTimestamp(LocalDateTime.now());
        service.addTransaction(transaction);
    }
}
