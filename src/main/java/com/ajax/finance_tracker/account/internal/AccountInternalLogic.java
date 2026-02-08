package com.ajax.finance_tracker.account.internal;

import org.springframework.stereotype.Component;

@Component
public class AccountInternalLogic {
    public void validateUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }
}
