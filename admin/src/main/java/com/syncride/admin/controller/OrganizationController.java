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

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService service;



    @PostMapping
    @PreAuthorize("hasRole('SYSTEMADMIN')")
    public ResponseEntity<Map<String,Object>> create(@RequestBody OrganizationDTO dto){

        Map<String,Object> response = new HashMap<>();

        try{
            OrganizationDTO created = service.create(dto);
            response.put("success", true);
            response.put("message", "Organization created");
            response.put("data", created);

            return ResponseEntity.status(201).body(response);

        }catch (Exception e){
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(400).body(response);
        }
    }



    @PreAuthorize("hasRole('SYSTEMADMIN')")
    @GetMapping("/{orgId}")
    public ResponseEntity<Map<String,Object>> get(@PathVariable String orgId){

        Map<String,Object> response = new HashMap<>();

        try{
            OrganizationDTO dto = service.getByOrgId(orgId);
            response.put("success", true);
            response.put("data", dto);

            return ResponseEntity.ok(response);

        }catch (Exception e){
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(404).body(response);
        }
    }

    @PreAuthorize("hasRole('SYSTEMADMIN')")
    @GetMapping
    public ResponseEntity<Map<String,Object>> list(){

        Map<String,Object> response = new HashMap<>();

        List<OrganizationDTO> list = service.getAll();

        response.put("success", true);
        response.put("data", list);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SYSTEMADMIN')")
    @PutMapping("/{orgId}")
    public ResponseEntity<Map<String,Object>> update(@PathVariable String orgId,
                                                     @RequestBody OrganizationDTO dto){

        Map<String,Object> response = new HashMap<>();

        try{
            OrganizationDTO updated = service.update(orgId,dto);
            response.put("success", true);
            response.put("message", "Organization updated");
            response.put("data", updated);

            return ResponseEntity.ok(response);

        }catch (Exception e){
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(404).body(response);
        }
    }

    @PreAuthorize("hasRole('SYSTEMADMIN')")
    @DeleteMapping("/{orgId}")
    public ResponseEntity<Map<String,Object>> delete(@PathVariable String orgId){

        Map<String,Object> response = new HashMap<>();

        try{
            service.delete(orgId);
            response.put("success", true);
            response.put("message", "Organization deleted");

            return ResponseEntity.ok(response);

        }catch (Exception e){
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(404).body(response);
        }
    }
}