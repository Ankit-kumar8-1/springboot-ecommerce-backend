package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Service.SellerReportService;
import com.ankitsaahariya.dao.SellerProfileRepository;
import com.ankitsaahariya.dao.SellerReportRepository;
import com.ankitsaahariya.entities.SellerProfile;
import com.ankitsaahariya.entities.SellerReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerReportServiceImpl implements SellerReportService {

    private final SellerReportRepository sellerReportRepository;
    private final SellerProfileRepository sellerProfileRepository;

    // Platform commission rate (example: 5%)
    private static final double COMMISSION_RATE = 0.05;

    @Override
    @Transactional
    public SellerReport getOrCreateReport(Long sellerId) {
        return sellerReportRepository.findBySellerId(sellerId)
                .orElseGet(() -> createNewReport(sellerId));
    }

    @Override
    @Transactional
    public void updateOnOrderCreated(Long sellerId) {
        log.info("Updating seller report for seller: {} - Order Created", sellerId);

        SellerReport report = getOrCreateReport(sellerId);

        // Increment counters
        report.setTotalOrders(report.getTotalOrders() + 1);
        report.setTotalTransactions(report.getTotalTransactions() + 1);

        sellerReportRepository.save(report);

        log.info("Seller report updated: Total Orders = {}", report.getTotalOrders());
    }

    @Override
    @Transactional
    public void updateOnOrderDelivered(Long sellerId, Long orderAmount) {
        log.info("Updating seller report for seller: {} - Order Delivered, Amount: {}",
                sellerId, orderAmount);

        SellerReport report = getOrCreateReport(sellerId);

        // Update earnings
        report.setTotalSales(report.getTotalSales() + orderAmount);

        // Calculate platform commission (tax)
        Long commission = Math.round(orderAmount * COMMISSION_RATE);
        report.setTotalTax(report.getTotalTax() + commission);

        // Earnings = Sales - Commission
        Long earnings = orderAmount - commission;
        report.setTotalEarnings(report.getTotalEarnings() + earnings);

        // Recalculate net earnings
        recalculateNetEarnings(report);

        sellerReportRepository.save(report);

        log.info("Seller report updated: Total Sales = {}, Total Earnings = {}, Net Earnings = {}",
                report.getTotalSales(), report.getTotalEarnings(), report.getNetEarnings());
    }

    @Override
    @Transactional
    public void updateOnOrderCancelled(Long sellerId) {
        log.info("Updating seller report for seller: {} - Order Cancelled", sellerId);

        SellerReport report = getOrCreateReport(sellerId);

        // Increment cancelled orders
        report.setCanceledOrders(report.getCanceledOrders() + 1);

        sellerReportRepository.save(report);

        log.info("Seller report updated: Cancelled Orders = {}", report.getCanceledOrders());
    }

    @Override
    @Transactional
    public void updateOnRefund(Long sellerId, Long refundAmount) {
        log.info("Updating seller report for seller: {} - Refund Issued, Amount: {}",
                sellerId, refundAmount);

        SellerReport report = getOrCreateReport(sellerId);

        // Update refund amount
        report.setTotalRefund(report.getTotalRefund() + refundAmount);

        // Recalculate net earnings
        recalculateNetEarnings(report);

        sellerReportRepository.save(report);

        log.info("Seller report updated: Total Refund = {}, Net Earnings = {}",
                report.getTotalRefund(), report.getNetEarnings());
    }

    @Override
    @Transactional
    public void recalculateNetEarnings(Long sellerId) {
        SellerReport report = getOrCreateReport(sellerId);
        recalculateNetEarnings(report);
        sellerReportRepository.save(report);
    }



    private SellerReport createNewReport(Long sellerId) {
        log.info("Creating new seller report for seller: {}", sellerId);

        SellerProfile seller = sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + sellerId));

        SellerReport report = new SellerReport();
        report.setSeller(seller);
        report.setTotalEarnings(0L);
        report.setTotalSales(0L);
        report.setTotalRefund(0L);
        report.setTotalTax(0L);
        report.setNetEarnings(0L);
        report.setTotalOrders(0);
        report.setCanceledOrders(0);
        report.setTotalTransactions(0);

        return sellerReportRepository.save(report);
    }

    private void recalculateNetEarnings(SellerReport report) {
        // Net Earnings = Total Earnings - Total Refund
        Long netEarnings = report.getTotalEarnings() - report.getTotalRefund();
        report.setNetEarnings(netEarnings);
    }
}