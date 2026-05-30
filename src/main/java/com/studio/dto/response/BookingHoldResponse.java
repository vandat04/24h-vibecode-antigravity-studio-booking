package com.studio.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingHoldResponse {
    private String holdToken;
    private LocalDateTime holdExpiredAt;
    private String message;
}
