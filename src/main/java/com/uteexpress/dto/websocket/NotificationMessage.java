package com.uteexpress.dto.websocket;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NotificationMessage {
    private String title;
    private String body;
    private String type;   // ORDER_UPDATE, PAYMENT, INFO...
}
