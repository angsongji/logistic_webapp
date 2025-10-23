package com.uteexpress.dto.customer;

import lombok.Data;

@Data
public class ShippingFeeResponseDto {
    private Double shippingFee;
    private String message;
    private Boolean success;
    
    public ShippingFeeResponseDto(Double shippingFee, String message, Boolean success) {
        this.shippingFee = shippingFee;
        this.message = message;
        this.success = success;
    }
}
