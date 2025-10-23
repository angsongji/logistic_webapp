package com.uteexpress.repository;

import com.uteexpress.entity.Commission;
import com.uteexpress.entity.Commission.CommissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    
    // Tìm hoa hồng theo shipper
    List<Commission> findByShipperId(Long shipperId);
    
    // Tìm hoa hồng theo trạng thái
    List<Commission> findByStatus(CommissionStatus status);
    
    // Tìm hoa hồng theo shipper và trạng thái
    List<Commission> findByShipperIdAndStatus(Long shipperId, CommissionStatus status);
    
    // Tìm hoa hồng theo order
    List<Commission> findByOrderId(Long orderId);
    
    // Tìm hoa hồng trong khoảng thời gian
    @Query("SELECT c FROM Commission c WHERE c.calculatedDate BETWEEN :startDate AND :endDate")
    List<Commission> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    // Tìm hoa hồng của shipper trong khoảng thời gian
    @Query("SELECT c FROM Commission c WHERE c.shipper.id = :shipperId " +
           "AND c.calculatedDate BETWEEN :startDate AND :endDate")
    List<Commission> findByShipperIdAndDateRange(@Param("shipperId") Long shipperId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
    
    // Thống kê tổng hoa hồng chưa thanh toán theo shipper
    @Query("SELECT SUM(c.commissionAmount) FROM Commission c WHERE c.shipper.id = :shipperId AND c.status = :status")
    Double sumCommissionAmountByShipperIdAndStatus(@Param("shipperId") Long shipperId, 
                                                    @Param("status") CommissionStatus status);
    
    // Tìm hoa hồng theo khoảng ngày (LocalDate)
    @Query("SELECT c FROM Commission c WHERE DATE(c.calculatedDate) BETWEEN :startDate AND :endDate")
    List<Commission> findByCalculatedDateBetween(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);
}

