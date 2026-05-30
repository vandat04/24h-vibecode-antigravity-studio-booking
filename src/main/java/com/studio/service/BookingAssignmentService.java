package com.studio.service;

import com.studio.entity.BookingAssignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingAssignmentService {
    BookingAssignment assignStaff(Long bookingId, Long staffId, LocalDate date, LocalTime timeSlot);
    List<BookingAssignment> getAssignmentsByBooking(Long bookingId);
    List<BookingAssignment> getAssignmentsByStaff(Long staffId);
    void removeAssignment(Long assignmentId);
}
