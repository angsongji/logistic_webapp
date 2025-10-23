package com.uteexpress.service.impl;

import com.uteexpress.dto.accountant.CommissionDto;
import com.uteexpress.entity.Commission;
import com.uteexpress.entity.Commission.CommissionStatus;
import com.uteexpress.entity.Commission.CommissionType;
import com.uteexpress.entity.Order;
import com.uteexpress.entity.Shipper;
import com.uteexpress.repository.CommissionRepository;
import com.uteexpress.repository.OrderRepository;
import com.uteexpress.repository.ShipperRepository;
import com.uteexpress.service.accountant.CommissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepository;
    private final ShipperRepository shipperRepository;
    private final OrderRepository orderRepository;
    
    // Phần trăm hoa hồng mặc định (10%)
    private static final BigDecimal DEFAULT_COMMISSION_PERCENTAGE = new BigDecimal("10");

    public CommissionServiceImpl(CommissionRepository commissionRepository,
                                 ShipperRepository shipperRepository,
                                 OrderRepository orderRepository) {
        this.commissionRepository = commissionRepository;
        this.shipperRepository = shipperRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<CommissionDto> getAllCommissions() {
        return commissionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommissionDto getCommissionById(Long id) {
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoa hồng với ID: " + id));
        return convertToDto(commission);
    }

    @Override
    public List<CommissionDto> getCommissionsByShipper(Long shipperId) {
        return commissionRepository.findByShipperId(shipperId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommissionDto> getCommissionsByStatus(CommissionStatus status) {
        return commissionRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommissionDto> getCommissionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return commissionRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Commission createCommission(Long shipperId, Long orderId, BigDecimal shippingFee) {
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper với ID: " + shipperId));
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        // Tính hoa hồng = phí vận chuyển * phần trăm hoa hồng / 100
        BigDecimal commissionAmount = shippingFee
                .multiply(DEFAULT_COMMISSION_PERCENTAGE)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        Commission commission = Commission.builder()
                .shipper(shipper)
                .order(order)
                .baseAmount(shippingFee)
                .commissionRate(DEFAULT_COMMISSION_PERCENTAGE)
                .commissionAmount(commissionAmount)
                .commissionType(CommissionType.DELIVERY)
                .status(CommissionStatus.PENDING)
                .calculatedDate(LocalDateTime.now())
                .build();
        
        return commissionRepository.save(commission);
    }

    @Override
    @Transactional
    public CommissionDto approveCommission(Long commissionId) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoa hồng với ID: " + commissionId));
        
        if (commission.getStatus() != CommissionStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể duyệt hoa hồng đang chờ thanh toán");
        }
        
        commission.setStatus(CommissionStatus.APPROVED);
        Commission saved = commissionRepository.save(commission);
        
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public CommissionDto payCommission(Long commissionId) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoa hồng với ID: " + commissionId));
        
        if (commission.getStatus() != CommissionStatus.APPROVED) {
            throw new RuntimeException("Chỉ có thể thanh toán hoa hồng đã được duyệt");
        }
        
        commission.setStatus(CommissionStatus.PAID);
        commission.setPaidDate(LocalDateTime.now());
        Commission saved = commissionRepository.save(commission);
        
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public CommissionDto cancelCommission(Long commissionId) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoa hồng với ID: " + commissionId));
        
        if (commission.getStatus() == CommissionStatus.PAID) {
            throw new RuntimeException("Không thể hủy hoa hồng đã thanh toán");
        }
        
        commission.setStatus(CommissionStatus.CANCELLED);
        Commission saved = commissionRepository.save(commission);
        
        return convertToDto(saved);
    }

    @Override
    public Double getTotalPendingCommission(Long shipperId) {
        Double total = commissionRepository.sumCommissionAmountByShipperIdAndStatus(shipperId, CommissionStatus.PENDING);
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional
    public CommissionDto updateCommission(Long commissionId, CommissionDto updateData) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoa hồng với ID: " + commissionId));
        
        // Update fields if provided
        if (updateData.getCommissionAmount() != null) {
            commission.setCommissionAmount(updateData.getCommissionAmount());
        }
        
        if (updateData.getCommissionRate() != null) {
            commission.setCommissionRate(updateData.getCommissionRate());
        }
        
        if (updateData.getBaseAmount() != null) {
            commission.setBaseAmount(updateData.getBaseAmount());
        }
        
        if (updateData.getStatus() != null) {
            commission.setStatus(updateData.getStatus());
        }
        
        if (updateData.getCommissionType() != null) {
            commission.setCommissionType(updateData.getCommissionType());
        }
        
        if (updateData.getNotes() != null) {
            commission.setNotes(updateData.getNotes());
        }
        
        if (updateData.getCalculatedDate() != null) {
            commission.setCalculatedDate(updateData.getCalculatedDate());
        }
        
        if (updateData.getPaidDate() != null) {
            commission.setPaidDate(updateData.getPaidDate());
        }
        
        Commission saved = commissionRepository.save(commission);
        return convertToDto(saved);
    }

    /**
     * Chuyển đổi Entity sang DTO
     */
    private CommissionDto convertToDto(Commission commission) {
        return CommissionDto.builder()
                .id(commission.getId())
                .shipperId(commission.getShipper() != null ? commission.getShipper().getId() : null)
                .shipperName(commission.getShipper() != null ? commission.getShipper().getName() : null)
                .shipperCode(commission.getShipper() != null ? commission.getShipper().getCode() : null)
                .orderId(commission.getOrder() != null ? commission.getOrder().getId() : null)
                .orderCode(commission.getOrder() != null ? commission.getOrder().getOrderCode() : null)
                .baseAmount(commission.getBaseAmount())
                .commissionRate(commission.getCommissionRate())
                .commissionAmount(commission.getCommissionAmount())
                .commissionType(commission.getCommissionType())
                .commissionTypeDisplay(commission.getCommissionType() != null ? 
                        commission.getCommissionType().getDisplayName() : null)
                .status(commission.getStatus())
                .statusDisplay(commission.getStatus() != null ? 
                        commission.getStatus().getDisplayName() : null)
                .calculatedDate(commission.getCalculatedDate())
                .paidDate(commission.getPaidDate())
                .notes(commission.getNotes())
                .createdAt(commission.getCreatedAt())
                .build();
    }
}

