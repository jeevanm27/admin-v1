package com.syncride.admin.service;

import com.syncride.admin.model.Organization;
import com.syncride.admin.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final WebClient webClient;
    private final OrganizationRepository organizationRepository;

    @Value("${user.service.url}")
    private String userServiceUrl;


    public Map<String, Object> createAdmin(String phone, String username, String fcmToken,
            String gender, String role, String orgId) {

        Map<String, Object> result = new HashMap<>();

        // --- Validate role ---
        if (!"admin".equalsIgnoreCase(role) && !"superadmin".equalsIgnoreCase(role)) {
            result.put("success", false);
            result.put("message", "Role must be 'admin' or 'superadmin'.");
            return result;
        }

        // --- Validate org_id ---
        if (orgId == null || orgId.isBlank()) {
            result.put("success", false);
            result.put("message", "org_id is required.");
            return result;
        }

        // --- Validate other required fields ---
        if (phone == null || phone.isBlank() ||
                username == null || username.isBlank() ||
                fcmToken == null || fcmToken.isBlank() ||
                gender == null || gender.isBlank()) {
            result.put("success", false);
            result.put("message", "Details are missing. Fill all fields.");
            return result;
        }

        try {
            // --- Call user-service to register the admin user ---
            Map<String, String> userPayload = new HashMap<>();
            userPayload.put("phone", phone);
            userPayload.put("username", username);
            userPayload.put("fcmToken", fcmToken);
            userPayload.put("gender", gender);
            userPayload.put("role", role.toLowerCase());
            userPayload.put("org_id", orgId);

            Map userServiceResponse = webClient.post()
                    .uri(userServiceUrl + "/api/add-admin")
                    .bodyValue(userPayload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (userServiceResponse == null || !Boolean.TRUE.equals(userServiceResponse.get("success"))) {
                String errMsg = userServiceResponse != null
                        ? (String) userServiceResponse.get("message")
                        : "No response from user-service";
                result.put("success", false);
                result.put("message", errMsg);
                return result;
            }

            // --- Ensure organization exists in admin DB ---
            Optional<Organization> existing = organizationRepository.findByOrgId(orgId);
            if (existing.isEmpty()) {
                Organization org = new Organization();
                org.setOrg_id(orgId);
                org.setOrganizationName(username + "'s Organization");
                organizationRepository.save(org);
                log.info("Created new organization record for org_id: {}", orgId);
            }

            result.put("success", true);
            result.put("message", "Admin created successfully.");
            result.put("id", userServiceResponse.get("id"));
            result.put("role", role.toLowerCase());
            return result;

        } catch (Exception e) {
            log.error("Failed to create admin: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "Failed to create admin: " + e.getMessage());
            return result;
        }
    }
}
