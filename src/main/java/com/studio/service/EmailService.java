package com.studio.service;

import com.studio.entity.Booking;

public interface EmailService {
    void sendBookingConfirmation(Booking booking);
    void sendBookingStatusUpdate(Booking booking, String note);
    void sendPaymentUpdate(Booking booking, String method);
    void sendStaffAssignment(Booking booking);
    void sendPhotosDelivered(Booking booking, String editedPhotoLink);
}

