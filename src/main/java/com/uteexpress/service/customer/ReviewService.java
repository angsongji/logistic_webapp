package com.uteexpress.service.customer;

import com.uteexpress.dto.customer.ReviewDto;
import com.uteexpress.entity.User;

import java.util.List;

public interface ReviewService {
    ReviewDto createReview(User customer, Long orderId, ReviewDto reviewDto);
    List<ReviewDto> getReviewsByCustomer(User customer);
    List<ReviewDto> getAllReviews();
    ReviewDto getReviewByOrder(Long orderId);
    Double getAverageRating();
    Long getReviewCount();
}
