package com.ajax.finance_tracker.notification;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {
    @Id
    @GeneratedValue
    private Long id;
    private String accountName;
    private String email;

    @ElementCollection
    private Map<String, String> scheduledNotifications;
}
