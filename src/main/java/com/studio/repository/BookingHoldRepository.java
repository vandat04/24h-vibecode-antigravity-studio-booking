package com.studio.repository;

import com.studio.entity.BookingHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingHoldRepository extends JpaRepository<BookingHold, Long> {

    List<BookingHold> findByShootDateAndHoldExpiredAtAfter(LocalDate date, LocalDateTime now);

    Optional<BookingHold> findByHoldTokenAndHoldExpiredAtAfter(String holdToken, LocalDateTime now);

    List<BookingHold> findByShootDateAndShootTimeSlotAndHoldExpiredAtAfter(LocalDate date, LocalTime slot, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM BookingHold b WHERE b.holdExpiredAt <= :now")
    void deleteExpiredHolds(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM BookingHold b WHERE b.shootDate = :date AND b.shootTimeSlot = :slot")
    void deleteHoldsOnSlot(@Param("date") LocalDate date, @Param("slot") LocalTime slot);
}
