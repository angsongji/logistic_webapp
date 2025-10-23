package com.uteexpress.service;

import com.uteexpress.dto.customer.ShippingFeeRequestDto;
import com.uteexpress.dto.customer.ShippingFeeResponseDto;

public interface ShippingFeeService {
    ShippingFeeResponseDto calculateShippingFee(ShippingFeeRequestDto request);
}