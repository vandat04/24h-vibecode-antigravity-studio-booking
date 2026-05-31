package com.studio.controller;

import com.studio.dto.request.ChangePasswordRequest;
import com.studio.dto.request.PostProductionUpdateRequest;
import com.studio.dto.response.AdminBookingResponse;
import com.studio.entity.PostProductionHistory;
import com.studio.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/bookings")
    public ResponseEntity<List<AdminBookingResponse>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(staffService.getMyBookings(page, size));
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<AdminBookingResponse> getMyBookingDetail(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getMyBookingDetail(id));
    }

    @PostMapping("/bookings/{id}/makeup-complete")
    public ResponseEntity<String> confirmMakeupComplete(@PathVariable Long id) {
        staffService.confirmMakeupComplete(id);
        return ResponseEntity.ok("Xác nhận hoàn thành makeup thành công!");
    }

    @PutMapping("/bookings/{id}/post-production")
    public ResponseEntity<PostProductionHistory> updateMyPostProduction(
            @PathVariable Long id,
            @Valid @RequestBody PostProductionUpdateRequest request) {
        return ResponseEntity.ok(staffService.updateMyPostProduction(id, request));
    }

    @PostMapping("/profile/change-password")
    public ResponseEntity<String> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request) {
        staffService.changeMyPassword(request);
        return ResponseEntity.ok("Thay đổi mật khẩu thành công!");
    }
}
