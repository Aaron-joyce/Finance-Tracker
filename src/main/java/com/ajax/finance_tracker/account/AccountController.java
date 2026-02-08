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
}
