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

        Account account = new Account(userDto.username(), userDto.password(), userDto.email(), LocalDateTime.now());
        repository.save(account);

        publisher.publishEvent(new UserRegisteredEvent(account.getUsername(), account.getEmail()));
    }

    @Transactional(readOnly = true)
    public boolean login(String username, String password) {
        return repository.findById(username)
                .map(account -> account.getPassword().equals(password))
                .orElse(false);
    }

    public record UserDto(String username, String password, String email) {
    }
}
