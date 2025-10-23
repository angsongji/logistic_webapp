package com.uteexpress.dto.customer;

import lombok.Data;

@Data
public class ShippingFeeRequestDto {
    private AddressDto pickupAddress;
    private String deliveryAddress;
    private String serviceType;
    private Double weight;
    private Double codAmount;
    
    @Data
    public static class AddressDto {
        private String address;
        private String city;
        private String district;
        private String ward;
    }
}
