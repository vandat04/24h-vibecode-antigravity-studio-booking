package com.studio.service.impl;

import com.studio.constant.BookingStatus;
import com.studio.entity.Booking;
import com.studio.entity.BookingAssignment;
import com.studio.entity.User;
import com.studio.repository.BookingAssignmentRepository;
import com.studio.repository.BookingRepository;
import com.studio.repository.UserRepository;
import com.studio.service.BookingAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingAssignmentServiceImpl implements BookingAssignmentService {

    private final BookingAssignmentRepository bookingAssignmentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingAssignment assignStaff(Long bookingId, Long staffId, LocalDate date, LocalTime timeSlot) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff user not found with ID: " + staffId));

        // CRITICAL CHECK: Double scheduling collision check
        bookingAssignmentRepository.findByStaffIdAndShootDateAndShootTimeSlot(staffId, date, timeSlot)
                .ifPresent(existingAssignment -> {
                    throw new IllegalStateException("CONFLICT: Staff '" + staff.getFullName() + 
                            "' is already assigned to booking code '" + existingAssignment.getBooking().getBookingCode() + 
                            "' at " + date + " " + timeSlot);
                });

        BookingAssignment assignment = BookingAssignment.builder()
                .booking(booking)
                .staff(staff)
                .shootDate(date)
                .shootTimeSlot(timeSlot)
                .build();

        BookingAssignment savedAssignment = bookingAssignmentRepository.save(assignment);

        // Update booking status to ASSIGNED if currently PENDING or CONFIRMED
        if (booking.getBookingStatus() == BookingStatus.PENDING || booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            booking.setBookingStatus(BookingStatus.ASSIGNED);
            bookingRepository.save(booking);
        }

        return savedAssignment;
    }

    @Override
    public List<BookingAssignment> getAssignmentsByBooking(Long bookingId) {
        return bookingAssignmentRepository.findByBookingId(bookingId);
    }

    @Override
    public List<BookingAssignment> getAssignmentsByStaff(Long staffId) {
        return bookingAssignmentRepository.findByStaffId(staffId);
    }

    @Override
    @Transactional
    public void removeAssignment(Long assignmentId) {
        if (!bookingAssignmentRepository.existsById(assignmentId)) {
            throw new IllegalArgumentException("Booking assignment not found with ID: " + assignmentId);
        }
        bookingAssignmentRepository.deleteById(assignmentId);
    }
}
