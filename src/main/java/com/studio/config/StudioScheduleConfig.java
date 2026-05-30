package com.studio.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "studio.schedule")
public class StudioScheduleConfig {

    /**
     * Danh sách khung giờ làm việc của Studio (định dạng HH:mm).
     * Cấu hình trong application.properties:
     *   studio.schedule.time-slots=07:30,09:00,10:30,13:00,14:30,16:00
     */
    private List<String> timeSlots;

    /**
     * Chuyển đổi danh sách String "HH:mm" sang LocalTime để dùng trong logic nghiệp vụ.
     */
    public List<LocalTime> getAllTimeSlots() {
        if (timeSlots == null || timeSlots.isEmpty()) {
            // Fallback mặc định nếu chưa cấu hình
            return List.of(
                    LocalTime.of(7, 30),
                    LocalTime.of(9, 0),
                    LocalTime.of(10, 30),
                    LocalTime.of(13, 0),
                    LocalTime.of(14, 30),
                    LocalTime.of(16, 0)
            );
        }
        return timeSlots.stream()
                .map(LocalTime::parse)
                .sorted()
                .collect(Collectors.toList());
    }
}
