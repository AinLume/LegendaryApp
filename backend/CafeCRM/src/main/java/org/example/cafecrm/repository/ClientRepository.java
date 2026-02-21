package org.example.cafecrm.repository;

import org.example.cafecrm.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

}