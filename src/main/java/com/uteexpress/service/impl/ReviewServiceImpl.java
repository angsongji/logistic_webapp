package com.uteexpress.service.impl;

import com.uteexpress.dto.customer.ReviewDto;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.Review;
import com.uteexpress.entity.User;
import com.uteexpress.repository.OrderRepository;
import com.uteexpress.repository.ReviewRepository;
import com.uteexpress.service.customer.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public ReviewDto createReview(User customer, Long orderId, ReviewDto reviewDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if review already exists for this order
        if (reviewRepository.findByOrderId(orderId).isPresent()) {
            throw new RuntimeException("Review already exists for this order");
        }

        Review review = Review.builder()
                .order(order)
                .customer(customer)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return toDto(saved);
    }

    @Override
    public List<ReviewDto> getReviewsByCustomer(User customer) {
        return reviewRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDto getReviewByOrder(Long orderId) {
        return reviewRepository.findByOrderId(orderId)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public Double getAverageRating() {
        return reviewRepository.getAverageRating();
    }

    @Override
    public Long getReviewCount() {
        return reviewRepository.count();
    }

    private ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .orderId(review.getOrder().getId())
                .orderCode(review.getOrder().getOrderCode())
                .customerName(review.getCustomer().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
