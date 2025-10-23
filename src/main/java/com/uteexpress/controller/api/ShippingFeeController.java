package com.uteexpress.controller.api;

import com.uteexpress.dto.customer.ShippingFeeRequestDto;
import com.uteexpress.dto.customer.ShippingFeeResponseDto;
import com.uteexpress.service.ShippingFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class ShippingFeeController {

    @Autowired
    private ShippingFeeService shippingFeeService;

    @PostMapping("/calculate-shipping-fee")
    public ResponseEntity<ShippingFeeResponseDto> calculateShippingFee(@RequestBody ShippingFeeRequestDto request) {
        try {
            ShippingFeeResponseDto response = shippingFeeService.calculateShippingFee(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ShippingFeeResponseDto errorResponse = new ShippingFeeResponseDto(
                0.0,
                "Lỗi server: " + e.getMessage(),
                false
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
