package com.uteexpress.service;

import com.uteexpress.entity.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ShippingFeeService {

    // Phí cơ bản cho mỗi km
    private static final BigDecimal BASE_RATE_PER_KM = new BigDecimal("5000");
    
    // Phí cơ bản tối thiểu
    private static final BigDecimal MINIMUM_FEE = new BigDecimal("15000");

    public BigDecimal calculateShippingFee(String fromAddress, String toAddress, Order.ServiceType serviceType) {
        // Tính khoảng cách dựa trên địa chỉ (simplified)
        double distance = calculateDistance(fromAddress, toAddress);
        
        // Tính phí cơ bản
        BigDecimal baseFee = BASE_RATE_PER_KM.multiply(BigDecimal.valueOf(distance));
        
        // Áp dụng hệ số dịch vụ
        BigDecimal serviceFee = baseFee.multiply(BigDecimal.valueOf(serviceType.getMultiplier()));
        
        // Đảm bảo phí tối thiểu
        if (serviceFee.compareTo(MINIMUM_FEE) < 0) {
            serviceFee = MINIMUM_FEE;
        }
        
        return serviceFee.setScale(0, RoundingMode.HALF_UP);
    }

    private double calculateDistance(String fromAddress, String toAddress) {
        // Simplified distance calculation based on address keywords
        // In real implementation, you would use Google Maps API or similar
        
        if (fromAddress.toLowerCase().contains("hà nội") && toAddress.toLowerCase().contains("hồ chí minh")) {
            return 1200; // km
        } else if (fromAddress.toLowerCase().contains("hồ chí minh") && toAddress.toLowerCase().contains("hà nội")) {
            return 1200; // km
        } else if (fromAddress.toLowerCase().contains("hà nội") && toAddress.toLowerCase().contains("đà nẵng")) {
            return 800; // km
        } else if (fromAddress.toLowerCase().contains("hồ chí minh") && toAddress.toLowerCase().contains("đà nẵng")) {
            return 900; // km
        } else {
            // Default distance for same city or unknown locations
            return 50; // km
        }
    }
}
