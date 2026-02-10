package com.ajax.finance_tracker.account;

import com.ajax.finance_tracker.account.internal.AccountInternalLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final ApplicationEventPublisher publisher;
    private final AccountInternalLogic internalLogic;

    @Transactional
    public void registerUser(UserDto userDto) {
        internalLogic.validateUser(userDto.username());

        String token = java.util.UUID.randomUUID().toString();
        Account account = new Account(
                userDto.username(),
                userDto.password(),
                userDto.email(),
                LocalDateTime.now(),
                false, // enabled
                token,
                LocalDateTime.now().plusHours(24) // 24h expiry
        );
        repository.save(account);

        publisher.publishEvent(new UserRegisteredEvent(account.getUsername(), account.getEmail(), token));
    }

    @Transactional(readOnly = true)
    public boolean login(String username, String password) {
        return repository.findById(username)
                .map(account -> account.getPassword().equals(password) && account.isEnabled())
                .orElse(false);
    }

    @Transactional
    public boolean verifyUser(String token) {
        return repository.findByVerificationToken(token)
                .map(account -> {
                    if (account.getTokenExpiry().isBefore(LocalDateTime.now())) {
                        return false;
                    }
                    account.setEnabled(true);
                    account.setVerificationToken(null);
                    account.setTokenExpiry(null);
                    repository.save(account);
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isAccountVerified(String username) {
        return repository.findById(username)
                .map(Account::isEnabled)
                .orElse(false); // or return true to not reveal account existence? Assuming false for now.
    }

    public record UserDto(String username, String password, String email) {
    }
}
