package com.studio.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class RevenueResponse {
    private BigDecimal totalRevenue;
    private long totalBookings;
    private List<RevenueByDateItem> revenueByDate;
    private List<PackagePopularityItem> packagePopularity;

    @Getter
    @Builder
    public static class RevenueByDateItem {
        private String date;
        private BigDecimal amount;
    }

    @Getter
    @Builder
    public static class PackagePopularityItem {
        private String packageName;
        private long bookingCount;
    }
}
