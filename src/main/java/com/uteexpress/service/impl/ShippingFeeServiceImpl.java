package com.uteexpress.service.impl;

import com.uteexpress.dto.customer.ShippingFeeRequestDto;
import com.uteexpress.dto.customer.ShippingFeeResponseDto;
import com.uteexpress.service.ShippingFeeService;
import org.springframework.stereotype.Service;

@Service
public class ShippingFeeServiceImpl implements ShippingFeeService {

    @Override
    public ShippingFeeResponseDto calculateShippingFee(ShippingFeeRequestDto request) {
        try {
            // Base fee by service type
            double baseFee = getBaseFeeByServiceType(request.getServiceType());
            
            // Weight-based fee
            double weightFee = calculateWeightFee(request.getWeight());
            
            // COD fee (2% of COD amount, minimum 5000)
            double codFee = calculateCodFee(request.getCodAmount());
            
            // Distance-based fee
            double distanceFee = calculateDistanceFee(request.getPickupAddress(), request.getDeliveryAddress());
            
            // Calculate final fee
            double finalFee = baseFee + weightFee + codFee + distanceFee;
            
            return new ShippingFeeResponseDto(
                (double) Math.round(finalFee),
                "Tính phí vận chuyển thành công",
                true
            );
        } catch (Exception e) {
            return new ShippingFeeResponseDto(
                0.0,
                "Lỗi khi tính phí vận chuyển: " + e.getMessage(),
                false
            );
        }
    }
    
    private double getBaseFeeByServiceType(String serviceType) {
        switch (serviceType) {
            case "CHUAN":
                return 25000;
            case "NHANH":
                return 35000;
            case "TIET_KIEM":
                return 20000;
            default:
                return 25000;
        }
    }
    
    private double calculateWeightFee(Double weight) {
        if (weight == null || weight <= 0) {
            return 0;
        }
        
        if (weight <= 1) {
            return 0;
        } else if (weight <= 3) {
            return 5000;
        } else if (weight <= 5) {
            return 10000;
        } else {
            return 15000 + (weight - 5) * 2000;
        }
    }
    
    private double calculateCodFee(Double codAmount) {
        if (codAmount == null || codAmount <= 0) {
            return 0;
        }
        
        double codFee = Math.max(5000, Math.round(codAmount * 0.02));
        return codFee;
    }
    
    private double calculateDistanceFee(ShippingFeeRequestDto.AddressDto pickupAddress, String deliveryAddress) {
        if (pickupAddress == null || deliveryAddress == null) {
            return 0;
        }
        
        String pickupCity = pickupAddress.getCity() != null ? pickupAddress.getCity().toLowerCase() : "";
        String deliveryCity = "";
        
        // Extract city from delivery address (simplified)
        String[] parts = deliveryAddress.split(",");
        if (parts.length > 1) {
            deliveryCity = parts[1].trim().toLowerCase();
        }
        
        // Inter-city fee
        if (!pickupCity.isEmpty() && !deliveryCity.isEmpty() && !pickupCity.equals(deliveryCity)) {
            return 10000;
        }
        
        return 0;
    }
}
