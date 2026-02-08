package com.ajax.finance_tracker.statistics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private BigDecimal amount;
    private String currency;
    private Period period;
    private Type type;

    public enum Period {
        MONTHLY, YEARLY
    }

    public enum Type {
        INCOME, EXPENSE
    }
}
