package com.uteexpress.service.accountant;

import com.uteexpress.dto.accountant.CommissionDto;
import com.uteexpress.entity.Commission;
import com.uteexpress.entity.Commission.CommissionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CommissionService {
    

    List<CommissionDto> getAllCommissions();
    

    CommissionDto getCommissionById(Long id);
    

    List<CommissionDto> getCommissionsByShipper(Long shipperId);
    

    List<CommissionDto> getCommissionsByStatus(CommissionStatus status);
    

    List<CommissionDto> getCommissionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    

    Commission createCommission(Long shipperId, Long orderId, BigDecimal shippingFee);
    

    CommissionDto approveCommission(Long commissionId);
    

    CommissionDto payCommission(Long commissionId);
    

    CommissionDto cancelCommission(Long commissionId);
    

    Double getTotalPendingCommission(Long shipperId);
    
    CommissionDto updateCommission(Long commissionId, CommissionDto updateData);
}

