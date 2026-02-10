package com.ajax.finance_tracker.budget;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Budget {
    @Id
    @GeneratedValue
    private Long id;
    private String accountId;
    private BigDecimal totalProjectedSavings; // Renamed from totalAmount for clarity? Or keep generic?
    // The prompt implies BudgetRules are for Plan.
    // Let's keep it simple.

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetRule> rules = new ArrayList<>();
}
