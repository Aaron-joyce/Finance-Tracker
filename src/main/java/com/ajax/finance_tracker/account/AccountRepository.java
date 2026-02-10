package com.ajax.finance_tracker.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
    java.util.Optional<Account> findByVerificationToken(String token);
}
