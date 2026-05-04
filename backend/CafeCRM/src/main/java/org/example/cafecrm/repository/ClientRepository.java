package org.example.cafecrm.repository;

import org.example.cafecrm.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByPhone(String phone);
}