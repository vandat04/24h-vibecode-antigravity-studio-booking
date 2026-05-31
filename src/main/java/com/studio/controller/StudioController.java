package com.studio.controller;

import com.studio.constant.ConceptType;
import com.studio.dto.request.BookingRequest;
import com.studio.dto.request.BookingHoldRequest;
import com.studio.dto.response.*;
import com.studio.service.StudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/studio")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    // =========================================================
    // 1. STUDIO INFORMATION
    // =========================================================
    @GetMapping("/info")
    public ResponseEntity<StudioInfoResponse> getStudioInfo() {
        return ResponseEntity.ok(studioService.getStudioInfo());
    }

    // =========================================================
    // 2. CONCEPTS & PORTFOLIO
    // =========================================================
    @GetMapping("/concepts")
    public ResponseEntity<List<ConceptSummaryResponse>> getConcepts(
            @RequestParam(required = false) ConceptType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studioService.getConcepts(type, page, size));
    }

    @GetMapping("/concepts/{slug}")
    public ResponseEntity<ConceptDetailResponse> getConceptDetail(@PathVariable String slug) {
        return ResponseEntity.ok(studioService.getConceptDetail(slug));
    }

    // =========================================================
    // 3. SERVICE PACKAGES
    // =========================================================
    @GetMapping("/packages")
    public ResponseEntity<List<ServicePackageResponse>> getPackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studioService.getPackages(page, size));
    }

    @GetMapping("/packages/{slug}")
    public ResponseEntity<ServicePackageResponse> getPackageDetail(@PathVariable String slug) {
        return ResponseEntity.ok(studioService.getPackageDetail(slug));
    }

    // =========================================================
    // 4. STAFF PROFILES
    // =========================================================
    @GetMapping("/staff")
    public ResponseEntity<List<StaffProfileResponse>> getStaff(
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studioService.getStaff(role, page, size));
    }

    // =========================================================
    // 5. BLOGS
    // =========================================================
    @GetMapping("/blogs")
    public ResponseEntity<List<BlogSummaryResponse>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studioService.getBlogs(page, size));
    }

    @GetMapping("/blogs/{slug}")
    public ResponseEntity<BlogDetailResponse> getBlogDetail(@PathVariable String slug) {
        return ResponseEntity.ok(studioService.getBlogDetail(slug));
    }

    // =========================================================
    // 6. CUSTOMER STORIES
    // =========================================================
    @GetMapping("/stories")
    public ResponseEntity<List<CustomerStoryResponse>> getCustomerStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studioService.getCustomerStories(page, size));
    }

    // =========================================================
    // 7. SCHEDULE - CHECK AVAILABLE SLOTS
    // =========================================================
    @GetMapping("/bookings/schedule")
    public ResponseEntity<ScheduleSlotResponse> getSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long packageId,
            @RequestParam(required = false) Long conceptId) {
        return ResponseEntity.ok(studioService.getSchedule(date, packageId, conceptId));
    }

    @PostMapping("/bookings/hold")
    public ResponseEntity<BookingHoldResponse> holdSlot(@Valid @RequestBody BookingHoldRequest request) {
        return ResponseEntity.ok(studioService.holdSlot(request));
    }

    // =========================================================
    // 8. BOOKING - CREATE
    // =========================================================
    @PostMapping("/bookings")
    public ResponseEntity<BookingCreateResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(studioService.createBooking(request));
    }

    // =========================================================
    // 9. BOOKING LOOKUP - TRACK PROGRESS
    // =========================================================
    @GetMapping("/bookings/lookup")
    public ResponseEntity<BookingLookupResponse> lookupBooking(
            @RequestParam String phone,
            @RequestParam String code) {
        return ResponseEntity.ok(studioService.lookupBooking(phone, code));
    }
}
