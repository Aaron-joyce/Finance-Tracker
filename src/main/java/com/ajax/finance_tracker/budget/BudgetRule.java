package com.ajax.finance_tracker.budget;

import com.ajax.finance_tracker.model.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRule {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private BigDecimal amount;
    private String currency;
    private Period period = Period.MONTHLY;
    private Type type = Type.EXPENSE;
    private Category category = Category.OTHER;
    private java.time.LocalDate date = java.time.LocalDate.now();

    public enum Period {
        MONTHLY, YEARLY, DAILY
    }

    public enum Type {
        INCOME, EXPENSE
    }

    public java.time.LocalDate getDate() {
        return date != null ? date : java.time.LocalDate.now();
    }

    public Category getCategory() {
        return category != null ? category : Category.OTHER;
    }

    public Period getPeriod() {
        return period != null ? period : Period.MONTHLY;
    }

    public Type getType() {
        return type != null ? type : Type.EXPENSE;
    }
}
