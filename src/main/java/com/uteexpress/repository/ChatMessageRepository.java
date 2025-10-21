package com.uteexpress.repository;

import com.uteexpress.entity.ChatMessage;
import com.uteexpress.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndReceiverOrderByCreatedAtAsc(User sender, User receiver);
    List<ChatMessage> findByReceiverAndIsReadFalse(User receiver);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.sender = :user1 AND cm.receiver = :user2) OR " +
           "(cm.sender = :user2 AND cm.receiver = :user1) " +
           "ORDER BY cm.createdAt ASC")
    List<ChatMessage> findConversationBetweenUsers(User user1, User user2);
}
