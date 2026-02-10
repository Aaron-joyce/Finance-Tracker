package com.ajax.finance_tracker.tracking;

import com.ajax.finance_tracker.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByTimestampDesc(String accountId);

    // Fix 2: Exclude Income Categories from Expense Calculation
    List<Transaction> findByAccountIdAndCategoryNotInOrderByTimestampDesc(String accountId, List<Category> categories);
}
