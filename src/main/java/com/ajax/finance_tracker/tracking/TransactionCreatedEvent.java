package com.ajax.finance_tracker.tracking;

import lombok.Value;

@Value
public class TransactionCreatedEvent {
    Transaction transaction;
}
