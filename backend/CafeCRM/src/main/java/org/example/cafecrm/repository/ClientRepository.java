package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Client;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<@NotNull Client, @NotNull Long> {

    Client findByPhone(String phone);
}