package com.uteexpress.service.impl;

import com.uteexpress.dto.websocket.NotificationMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * A small static bridge so JPA entity listeners (non-managed) can send messages
 * without direct DI. The template is injected once by Spring at startup.
 */
@Component
public class NotificationServiceImplBridge {
    private static SimpMessagingTemplate template;

    public NotificationServiceImplBridge(SimpMessagingTemplate t) {
        template = t;
    }

    public static void sendToUsername(String username, String title, String body, String type) {
        if (template == null || username == null || username.isBlank()) return;
        NotificationMessage msg = new NotificationMessage(title, body, type);
        template.convertAndSendToUser(username, "/queue/notifications", msg);
    }
}


