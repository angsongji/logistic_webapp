package com.uteexpress.repository;

import com.uteexpress.entity.Address;
import com.uteexpress.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    List<Address> findByUser(User user);
}
