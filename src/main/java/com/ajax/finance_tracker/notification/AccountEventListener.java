package com.ajax.finance_tracker.notification;

import com.ajax.finance_tracker.account.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventListener {

    private final EmailService emailService;

    @ApplicationModuleListener
    void on(UserRegisteredEvent event) {
        String verificationLink = "http://localhost:8080/auth/verify?token=" + event.verificationToken();
        log.debug("Sending verification email to {}. Link: {}", event.email(), verificationLink);

        String htmlBody = """
                <h1>Welcome to Finance Tracker!</h1>
                <p>Please verify your email address to activate your account.</p>
                <p style="padding:20px; border:2px; border-radius:10px; background-color:#f0f0f0;"><a style="color:#111" href="%s">Click here to verify</a></p>
                <br>
                <p>If you did not register, please ignore this email.</p>
                """
                .formatted(verificationLink);

        emailService.sendHtmlMessage(
                event.email(),
                "Welcome to PiggyModule! Please Verify Your Email",
                htmlBody);
    }
}
