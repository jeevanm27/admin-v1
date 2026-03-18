package com.syncride.admin.controller;

import com.syncride.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }


    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, Object>> createAdmin(
            @RequestBody Map<String, String> body) {

        Map<String, Object> response = adminService.createAdmin(
                body.get("phone"),
                body.get("username"),
                body.get("fcmToken"),
                body.get("gender"),
                body.get("role"),
                body.get("org_id"));

        boolean success = Boolean.TRUE.equals(response.get("success"));
        return ResponseEntity.status(success ? 200 : 400).body(response);
    }
}
