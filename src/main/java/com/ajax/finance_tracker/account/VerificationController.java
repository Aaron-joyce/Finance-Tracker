package com.ajax.finance_tracker.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class VerificationController {

    private final AccountService accountService;

    @GetMapping("/auth/verify")
    public String verifyAccount(@RequestParam("token") String token, Model model) {
        boolean verified = accountService.verifyUser(token);
        if (verified) {
            return "verify-success";
        } else {
            return "verify-fail";
        }
    }

    @GetMapping("/check-email")
    public String checkEmail() {
        return "check-email";
    }
}
