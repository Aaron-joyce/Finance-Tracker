package com.ajax.finance_tracker.tracking;

import com.ajax.finance_tracker.model.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private Category category;
    private String description;
    private String accountId;
}
