package com.studio.repository;

import com.studio.constant.BookingStatus;
import com.studio.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingCode(String bookingCode);
    List<Booking> findByCustomerPhone(String customerPhone);
    List<Booking> findByCustomerPhoneOrBookingCode(String customerPhone, String bookingCode);
    List<Booking> findByShootDate(LocalDate shootDate);
    List<Booking> findByShootDateAndShootTimeSlot(LocalDate shootDate, LocalTime shootTimeSlot);
    List<Booking> findByBookingStatus(BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.shootDate BETWEEN :startDate AND :endDate AND b.bookingStatus <> 'CANCELLED'")
    List<Booking> findBookingsForRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b.servicePackage.packageName, COUNT(b) FROM Booking b GROUP BY b.servicePackage.packageName ORDER BY COUNT(b) DESC")
    List<Object[]> findPackagePopularity();

    @Query("SELECT b.customerName, b.customerPhone, b.customerEmail FROM Booking b GROUP BY b.customerName, b.customerPhone, b.customerEmail")
    List<Object[]> findUniqueCustomers();
}
