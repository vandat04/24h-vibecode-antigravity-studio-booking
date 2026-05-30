package com.studio.entity;

import com.studio.constant.BookingStatus;
import com.studio.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "bookings", uniqueConstraints = {
    @UniqueConstraint(name = "uk_booking_code", columnNames = "booking_code")
}, indexes = {
    @Index(name = "idx_bookings_search", columnList = "customer_phone, booking_code"),
    @Index(name = "idx_bookings_schedule", columnList = "shoot_date, shoot_time_slot")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @Column(name = "booking_code", length = 20, nullable = false)
    private String bookingCode;

    @Column(name = "customer_name", length = 100, nullable = false)
    private String customerName;

    @Column(name = "customer_email", length = 100, nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone", length = 15, nullable = false)
    private String customerPhone;

    @Column(name = "customer_facebook", length = 255)
    private String customerFacebook;

    @Column(name = "shoot_date", nullable = false)
    private LocalDate shootDate;

    @Column(name = "shoot_time_slot", nullable = false)
    private LocalTime shootTimeSlot;

    @Column(name = "shoot_location", length = 255, nullable = false)
    private String shootLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bookings_package"))
    private ServicePackage servicePackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concept_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bookings_concept"))
    private Concept concept;

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id != null && id.equals(booking.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
