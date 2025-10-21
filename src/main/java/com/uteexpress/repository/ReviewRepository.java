package com.uteexpress.repository;

import com.uteexpress.entity.Review;
import com.uteexpress.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCustomerOrderByCreatedAtDesc(User customer);
    Optional<Review> findByOrderId(Long orderId);
    
    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getAverageRating();
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.rating = :rating")
    Long countByRating(Integer rating);
}
