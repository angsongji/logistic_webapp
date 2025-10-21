package com.uteexpress.controller.api;

import com.uteexpress.dto.customer.ReviewDto;
import com.uteexpress.entity.User;
import com.uteexpress.service.customer.CustomerService;
import com.uteexpress.service.customer.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customer/reviews")
public class ReviewRestController {

    private final ReviewService reviewService;
    private final CustomerService customerService;

    public ReviewRestController(ReviewService reviewService, CustomerService customerService) {
        this.reviewService = reviewService;
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto, Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        ReviewDto created = reviewService.createReview(user, reviewDto.getOrderId(), reviewDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getMyReviews(Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        List<ReviewDto> reviews = reviewService.getReviewsByCustomer(user);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ReviewDto> getReviewByOrder(@PathVariable Long orderId) {
        ReviewDto review = reviewService.getReviewByOrder(orderId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getReviewStats() {
        Double avgRating = reviewService.getAverageRating();
        Long count = reviewService.getReviewCount();
        
        return ResponseEntity.ok(new Object() {
            public final Double averageRating = avgRating;
            public final Long reviewCount = count;
        });
    }
}
