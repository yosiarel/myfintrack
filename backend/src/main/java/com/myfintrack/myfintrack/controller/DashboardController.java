package com.myfintrack.myfintrack.controller;

import com.myfintrack.myfintrack.dto.response.ApiResponse;
import com.myfintrack.myfintrack.dto.response.DashboardResponse;
import com.myfintrack.myfintrack.entity.User;
import com.myfintrack.myfintrack.service.DashboardService;
import com.myfintrack.myfintrack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());

        if (month == null || year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        DashboardResponse response = dashboardService.getDashboardSummary(user, month, year);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
