package com.uteexpress.repository;

import com.uteexpress.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    
    Optional<Shipper> findByCode(String code);
    
    Optional<Shipper> findByUserId(Long userId);
    
    List<Shipper> findByIsActive(Boolean isActive);
    
    @Query("SELECT s FROM Shipper s WHERE s.isActive = true ORDER BY s.totalDeliveries DESC")
    List<Shipper> findTopActiveShippers();
}

