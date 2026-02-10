package com.ajax.finance_tracker.account;

public record UserRegisteredEvent(String username, String email, String verificationToken) {
}
