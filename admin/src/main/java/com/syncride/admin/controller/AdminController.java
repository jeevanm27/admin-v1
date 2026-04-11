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


  
}
