package com.ajax.finance_tracker.statistics;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface StatisticRepository extends CrudRepository<Statistic, Long> {
    Optional<Statistic> findByAccountId(String accountId);
}
