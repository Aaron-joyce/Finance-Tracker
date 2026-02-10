package com.ajax.finance_tracker.account;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
class AccountController {

    private final AccountService service;

    @PostMapping("/")
    void create(@RequestBody AccountService.UserDto userDto) {
        service.registerUser(userDto);
    }

    @PostMapping("/login")
    org.springframework.http.ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        if (service.login(loginDto.username(), loginDto.password())) {
            return org.springframework.http.ResponseEntity.ok().build();
        }

        if (!service.isAccountVerified(loginDto.username())) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body("Account not verified");
        }
        return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                .body("Invalid credentials");
    }

    public record LoginDto(String username, String password) {
    }
}
