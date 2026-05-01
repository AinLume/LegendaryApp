package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {
}
