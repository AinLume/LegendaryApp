package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Staff;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<@NotNull Staff, @NotNull Long> {
    Staff findByEmail(String email);
}
