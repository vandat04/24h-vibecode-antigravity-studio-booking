package com.studio.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_holds", indexes = {
    @Index(name = "idx_booking_holds_expired", columnList = "hold_expired_at"),
    @Index(name = "idx_booking_holds_slot", columnList = "shoot_date, shoot_time_slot")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "shoot_date", nullable = false)
    private LocalDate shootDate;

    @Column(name = "shoot_time_slot", nullable = false)
    private LocalTime shootTimeSlot;

    @Column(name = "hold_expired_at", nullable = false)
    private LocalDateTime holdExpiredAt;

    @Column(name = "hold_token", length = 50, nullable = false, unique = true)
    private String holdToken;

    @Column(name = "concept_id", columnDefinition = "INT UNSIGNED")
    private Long conceptId;

    @Column(name = "package_id", columnDefinition = "INT UNSIGNED")
    private Long packageId;
}
