package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address,Long> {
    Integer countByUserId(Long userId);


    Optional<Address> findByIdAndUserId(Long addressId, Long id);

    List<Address> findByUserId(Long id);


    @Transactional
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void removeDefaultForUser(@Param("userId") Long userId);
}
