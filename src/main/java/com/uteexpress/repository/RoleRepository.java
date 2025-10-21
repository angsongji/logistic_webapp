package com.uteexpress.repository;

import com.uteexpress.entity.Role;
import com.uteexpress.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}