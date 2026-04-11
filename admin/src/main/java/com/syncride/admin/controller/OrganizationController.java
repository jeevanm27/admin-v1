package com.syncride.admin.controller;

import com.syncride.admin.dto.OrganizationDTO;
import com.syncride.admin.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody OrganizationDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {
            OrganizationDTO created = service.create(dto);
            response.put("success", true);
            response.put("message", "Organization created");
            response.put("data", created);

            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(400).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{orgId}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String orgId) {

        Map<String, Object> response = new HashMap<>();

        try {
            OrganizationDTO dto = service.getByOrgId(orgId);

            if (!dto.isEnabled()) {
                response.put("success", false);
                response.put("message", "Organization is disabled.");
                return ResponseEntity.status(403).body(response);
            }

            response.put("success", true);
            response.put("data", dto);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(404).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list() {

        Map<String, Object> response = new HashMap<>();

        List<OrganizationDTO> list = service.getAll();

        response.put("success", true);
        response.put("data", list);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{orgId}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String orgId,
            @RequestBody OrganizationDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {
            OrganizationDTO updated = service.update(orgId, dto);
            response.put("success", true);
            response.put("message", "Organization updated");
            response.put("data", updated);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(404).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{orgId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String orgId) {

        Map<String, Object> response = new HashMap<>();

        try {
            service.delete(orgId);
            response.put("success", true);
            response.put("message", "Organization deleted");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(404).body(response);
        }
    }
}
