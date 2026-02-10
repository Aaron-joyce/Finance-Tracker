package com.ajax.finance_tracker.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
class NotificationController {

    private final NotificationService service;

    @GetMapping("/settings")
    NotificationSettings getSettings(@RequestParam String accountName) {
        return service.getSettings(accountName);
    }

    @org.springframework.web.bind.annotation.PostMapping("/settings")
    void updateSettings(@RequestParam String accountName,
            @org.springframework.web.bind.annotation.RequestBody NotificationSettingsDto dto) {
        service.updateSettings(accountName, dto);
    }

}
