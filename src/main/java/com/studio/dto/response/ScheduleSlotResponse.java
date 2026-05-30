package com.studio.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class ScheduleSlotResponse {
    private String date;
    private List<LocalTime> bookedSlots;
    private List<LocalTime> availableSlots;
}
