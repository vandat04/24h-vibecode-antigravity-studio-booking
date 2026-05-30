package com.studio.repository;

import com.studio.entity.BookingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingAssignmentRepository extends JpaRepository<BookingAssignment, Long> {
    List<BookingAssignment> findByBookingId(Long bookingId);
    List<BookingAssignment> findByStaffId(Long staffId);
    Optional<BookingAssignment> findByStaffIdAndShootDateAndShootTimeSlot(Long staffId, LocalDate shootDate, LocalTime shootTimeSlot);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT a FROM BookingAssignment a JOIN FETCH a.staff s JOIN FETCH s.role r WHERE a.booking.concept.id = :conceptId")
    List<BookingAssignment> findCreditsByConceptId(@org.springframework.data.repository.query.Param("conceptId") Long conceptId);
}
