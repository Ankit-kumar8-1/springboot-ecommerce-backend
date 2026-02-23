package com.ankitsaahariya.Service;

import com.ankitsaahariya.entities.SellerReport;

public interface SellerReportService {


    /**
     * Get or create seller report
     */
    SellerReport getOrCreateReport(Long sellerId);

    /**
     * Update report when order is created
     */
    void updateOnOrderCreated(Long sellerId);

    /**
     * Update report when order is delivered
     */
    void updateOnOrderDelivered(Long sellerId, Long orderAmount);

    /**
     * Update report when order is cancelled
     */
    void updateOnOrderCancelled(Long sellerId);

    /**
     * Update report when refund is issued
     */
    void updateOnRefund(Long sellerId, Long refundAmount);

    /**
     * Recalculate net earnings
     */
    void recalculateNetEarnings(Long sellerId);
}
