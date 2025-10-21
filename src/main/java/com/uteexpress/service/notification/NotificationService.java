package com.uteexpress.service.notification;

import com.uteexpress.entity.User;

public interface NotificationService {
    void sendNotification(User recipient, String title, String body);
}
