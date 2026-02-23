package com.ankitsaahariya.dao;

import com.ankitsaahariya.entities.SellerReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerReportRepository extends JpaRepository<SellerReport,Long> {


    Optional<SellerReport> findBySellerId(Long sellerId);

    boolean existsBySellerId(Long sellerId);
}
