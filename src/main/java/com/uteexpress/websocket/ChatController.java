package com.uteexpress.websocket;

import com.uteexpress.dto.websocket.ChatMessageDto;
import com.uteexpress.entity.ChatMessage;
import com.uteexpress.entity.User;
import com.uteexpress.repository.ChatMessageRepository;
import com.uteexpress.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatMessageRepository chatMessageRepository,
                         UserRepository userRepository,
                         SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessageDto sendMessage(@Payload ChatMessageDto chatMessageDto) {
        // Find sender and receiver
        User sender = userRepository.findByUsername(chatMessageDto.getSenderName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        User receiver = userRepository.findByUsername(chatMessageDto.getReceiverName())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Save message to database
        ChatMessage message = ChatMessage.builder()
                .content(chatMessageDto.getContent())
                .sender(sender)
                .receiver(receiver)
                .isFromCustomer(chatMessageDto.getIsFromCustomer())
                .isRead(false)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);

        // Convert to DTO
        ChatMessageDto response = ChatMessageDto.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .senderName(saved.getSender().getUsername())
                .receiverName(saved.getReceiver().getUsername())
                .isFromCustomer(saved.getIsFromCustomer())
                .isRead(saved.getIsRead())
                .createdAt(saved.getCreatedAt())
                .build();

        // Send to specific user
        messagingTemplate.convertAndSendToUser(
                chatMessageDto.getReceiverName(),
                "/queue/messages",
                response
        );

        return response;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDto addUser(@Payload ChatMessageDto chatMessageDto) {
        return chatMessageDto;
    }
}
