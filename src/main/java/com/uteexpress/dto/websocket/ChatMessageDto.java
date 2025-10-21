package com.uteexpress.dto.websocket;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private String content;
    private String senderName;
    private String receiverName;
    private Boolean isFromCustomer;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
