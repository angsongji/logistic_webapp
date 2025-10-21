package com.uteexpress.service.impl;

import com.uteexpress.dto.websocket.NotificationMessage;
import com.uteexpress.entity.Notification;
import com.uteexpress.entity.User;
import com.uteexpress.repository.NotificationRepository;
import com.uteexpress.service.notification.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void sendNotification(User recipient, String title, String body) {
        // ✅ Lấy vai trò chính của user
        String mainRole = recipient.getRoles()
                .stream()
                .findFirst()
                .map(Enum::name)
                .orElse("ROLE_USER");

        // ✅ Tạo đối tượng Notification
        Notification n = new Notification();
        n.setRecipientId(recipient.getId());
        n.setRecipientType(mainRole);
        n.setTitle(title);
        n.setMessage(body);
        n.setType(Notification.NotificationType.INFO);
        n.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(n);

        NotificationMessage msg = new NotificationMessage(title, body, "INFO");
        simpMessagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/notifications",
                msg
        );
    }
}
