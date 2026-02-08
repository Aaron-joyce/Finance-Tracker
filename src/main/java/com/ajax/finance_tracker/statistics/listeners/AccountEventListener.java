package com.ajax.finance_tracker.statistics.listeners;

import com.ajax.finance_tracker.account.UserRegisteredEvent;
import com.ajax.finance_tracker.statistics.Statistic;
import com.ajax.finance_tracker.statistics.StatisticRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountEventListener {

    private final StatisticRepository repository;

    @ApplicationModuleListener
    void on(UserRegisteredEvent event) {
        Statistic stat = new Statistic();
        stat.setAccountId(event.username());
        stat.setTotalAmount(BigDecimal.ZERO);
        repository.save(stat);
    }
}
