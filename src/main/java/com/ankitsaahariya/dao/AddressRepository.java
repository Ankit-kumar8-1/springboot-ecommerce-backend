package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {
    Integer countByUserId(Long userId);
}
