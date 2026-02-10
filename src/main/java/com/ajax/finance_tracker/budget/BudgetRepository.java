package com.ajax.finance_tracker.budget;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByAccountId(String accountId);
}
