package com.studio.service;

import com.studio.constant.BookingStatus;
import com.studio.constant.PaymentStatus;
import com.studio.entity.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking, Long packageId, Long conceptId, String holdToken);
    Booking updateBookingStatus(Long bookingId, BookingStatus newStatus, String note, Long changedByUserId);
    Booking updatePaymentStatus(Long bookingId, PaymentStatus newStatus);
    Booking getBookingById(Long id);
    Booking getBookingByCode(String code);
    List<Booking> getBookingsByPhone(String phone);
}
