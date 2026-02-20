package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {
    Integer countByUserId(Long userId);


    Optional<Address> findByIdAndUserId(Long addressId, Long id);

    List<Address> findByUserId(Long id);
}
