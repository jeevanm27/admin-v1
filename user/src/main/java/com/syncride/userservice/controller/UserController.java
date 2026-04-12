package com.syncride.userservice.controller;

import com.cloudinary.Api;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syncride.userservice.dto.ApiResponse;
import com.syncride.userservice.model.User;
import com.syncride.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

//ROUTES

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

        private static final Logger log = LoggerFactory.getLogger(UserController.class);
        private final UserService userService;
        private final ObjectMapper objectMapper;

        @GetMapping("/ping")
        public String ping() {
                log.info("User service is Working!: PING SERVICE");
                return "pong";
        }

        @PostMapping("/create-user")
        public ResponseEntity<ApiResponse<?>> createUser(
                        @RequestParam String phone,
                        @RequestParam String username,
                        @RequestParam String fcmToken,
                        @RequestParam String gender,
                        @RequestParam String role,
                        @RequestParam(required = false) String otp,
                        @RequestParam(value = "vehicle[number]", required = false) String number,
                        @RequestParam(value = "vehicle[type]", required = false) String type,
                        @RequestParam(value = "vehicle[seat_capacity]", required = false, defaultValue = "0") int seatCapacity,
                        @RequestParam(value = "aadhaar_url", required = false) String aadhaarUrl,
                        @RequestParam(value = "license_url", required = false) String licenseUrl) {

                ApiResponse<?> response = userService.createUser(
                                phone, username, fcmToken, gender, role,
                                otp, number, type, seatCapacity, aadhaarUrl, licenseUrl);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PostMapping("/update-otp")
        public ResponseEntity<ApiResponse<?>> updateOtp(@RequestBody Map<String, String> body) {

                String otp = body.get("otp");
                String phone = body.get("phone");
                String fcmToken = body.get("fcmToken");

                ApiResponse<?> response = userService.updateOtp(otp, phone, fcmToken);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/verify-otp")
        public ResponseEntity<ApiResponse<?>> verifyOtp(
                        @RequestParam String phone,
                        @RequestParam String otp) {

                ApiResponse<?> response = userService.verifyOtp(phone, otp);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/get-user")
        public ResponseEntity<ApiResponse<?>> getUser(Principal principal) {

                String id = principal.getName();

                ApiResponse<?> response = userService.getUser(id);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/get-user-fcm")
        public ResponseEntity<ApiResponse<?>> getUserFcm(Principal principal) {

                String id = principal.getName();

                ApiResponse<?> response = userService.getUserFcm(id);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PostMapping("/update-history")
        public ResponseEntity<ApiResponse<?>> updateHistory(
                        Principal principal,
                        @RequestBody Map<String, Object> body) {

                String id = principal.getName();

                Object detailsObj = body.containsKey("details") ? body.get("details") : body;

                User.RideHistory details = objectMapper.convertValue(detailsObj, User.RideHistory.class);

                ApiResponse<?> response = userService.updateHistory(id, details);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PostMapping("/remove-ride-history")
        public ResponseEntity<ApiResponse<?>> removeRideHistory(
                        Principal principal,
                        @RequestBody Map<String, String> body) {

                String id = principal.getName();
                String poolId = body.get("poolId");

                ApiResponse<?> response = userService.removeRideHistory(id, poolId);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/logout")
        public ResponseEntity<ApiResponse<?>> logout(Principal principal) {

                String id = principal.getName();

                ApiResponse<?> response = userService.logout(id);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/get-user-history")
        public ResponseEntity<ApiResponse<?>> getUserHistory(Principal principal) {

                String id = principal.getName();

                ApiResponse<?> response = userService.getUserHistory(id);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/update-cancelled-ride")
        public ResponseEntity<ApiResponse<?>> updateCancelledRide(
                        Principal principal,
                        @RequestParam String poolId) {

                String id = principal.getName();

                ApiResponse<?> response = userService.updateRideCancelled(id, poolId);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/update-complete-ride")
        public ResponseEntity<ApiResponse<?>> updateCompleteRide(
                        Principal principal,
                        @RequestParam String poolId) {

                String id = principal.getName();

                ApiResponse<?> response = userService.updateCompleteRide(id, poolId);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/update-leave-ride")
        public ResponseEntity<ApiResponse<?>> updateLeaveRide(
                        Principal principal,
                        @RequestParam String poolId) {

                String id = principal.getName();

                ApiResponse<?> response = userService.updateRideLeave(id, poolId);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/get-drivers")
        public ResponseEntity<ApiResponse<?>> getDrivers(
                        Principal principal,
                        @RequestParam double lat,
                        @RequestParam double lng) {

                ApiResponse<?> response = userService.getDrivers(lat, lng);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/get-specific-drivers")
        public ResponseEntity<ApiResponse<?>> getSpecificDrivers(
                        Principal principal,
                        @RequestParam double lat,
                        @RequestParam double lng,
                        @RequestParam String type) {

                ApiResponse<?> response = userService.getSpecificDrivers(lat, lng, type);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/set-driver-status")
        public ResponseEntity<ApiResponse<?>> setDriverStatus(
                        Principal principal,
                        @RequestParam String status) {

                String id = principal.getName();

                ApiResponse<?> response = userService.setDriverStatus(id, status);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PostMapping("/update-location")
        public ResponseEntity<ApiResponse<?>> updateLocation(
                        Principal principal,
                        @RequestBody Map<String, Object> body) {

                String id = principal.getName();

                double latitude = ((Number) body.get("latitude")).doubleValue();

                double longitude = ((Number) body.get("longitude")).doubleValue();

                ApiResponse<?> response = userService.updateLocation(id, latitude, longitude);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/update-request-complete-ride")
        public ResponseEntity<ApiResponse<?>> updateRequestCompleteRide(
                        Principal principal,
                        @RequestParam String rideId) {

                String id = principal.getName();

                ApiResponse<?> response = userService.updateRequestRideComplete(id, rideId);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/update-request-confirmed-ride")
        public ResponseEntity<ApiResponse<?>> updateRequestConfirmedRide(
                        Principal principal,
                        @RequestParam String rideId) {

                String id = principal.getName();

                ApiResponse<?> response = userService.updateRequestRideConfirmed(id, rideId);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/update-request-cancel-ride")
        public ResponseEntity<ApiResponse<?>> updateRequestCancelRide(
                        Principal principal,
                        @RequestParam String rideId,
                        @RequestParam String remarks) {

                String id = principal.getName();

                ApiResponse<?> response = userService.updateRequestRideCancelled(id, rideId, remarks);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @GetMapping("/get-profile")
        public ResponseEntity<ApiResponse<?>> getProfile(Principal principal) {

                String id = principal.getName();

                ApiResponse<?> response = userService.getProfile(id);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PostMapping("/update-ride-history")
        public ResponseEntity<ApiResponse<?>> updateRideHistory(
                        Principal principal,
                        @RequestBody Map<String, Object> body) {

                String id = principal.getName();

                String rideId = (String) body.get("rideId");

                Map<String, Object> details = (Map<String, Object>) body.get("details");

                ApiResponse<?> response = userService.updateRideHistory(id, rideId, details);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PostMapping("/update-schedule-ride-history")
        public ResponseEntity<ApiResponse<?>> updateScheduleRideHistory(
                        Principal principal,
                        @RequestBody Map<String, Object> body) {

                String id = principal.getName();

                String rideId = (String) body.get("rideId");

                Map<String, Object> details = (Map<String, Object>) body.get("details");

                ApiResponse<?> response = userService.updateScheduleRideHistory(id, rideId, details);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PostMapping("/upload-documents")
        public ResponseEntity<ApiResponse<?>> uploadDocuments(
                        Principal principal,
                        @RequestParam("aadhaar") MultipartFile aadhaar,
                        @RequestParam("license") MultipartFile license,
                        @RequestParam("number") String number,
                        @RequestParam("type") String type,
                        @RequestParam("seat_capacity") int seatCapacity) {

                String id = principal.getName();

                ApiResponse<?> response = userService.uploadDocuments(id, number, type, seatCapacity, aadhaar, license);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @PostMapping("/create-superadmin")
        public ResponseEntity<ApiResponse<?>> createSuperadmin(
                        @RequestBody Map<String, String> body) {

                ApiResponse<?> response = userService.createSuperadmin(
                                body.get("phone"),
                                body.get("username"),
                                body.get("fcmToken"),
                                body.get("gender"),
                                body.get("role"),
                                body.get("org_id"));

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PostMapping("/create-admin")
        public ResponseEntity<ApiResponse<?>> createAdmin(
                        @RequestBody Map<String, String> body) {

                ApiResponse<?> response = userService.createAdmin(
                                body.get("phone"),
                                body.get("username"),
                                body.get("fcmToken"),
                                body.get("gender"),
                                body.get("role"),
                                body.get("org_id"));

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @GetMapping("/get-superadmins")
        public ResponseEntity<ApiResponse<?>> getSuperadmins() {

                ApiResponse<?> response = userService.getSuperadmins();

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @GetMapping("/get-admins")
        public ResponseEntity<ApiResponse<?>> getAdmins() {

                ApiResponse<?> response = userService.getAdmins();

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PreAuthorize("hasRole('SYSTEM_ADMIN')")
        @GetMapping("/get-admins-by-org")
        public ResponseEntity<ApiResponse<?>> getAdminsByOrg(@RequestParam String orgId) {

                ApiResponse<?> response = userService.getAdminsByOrgId(orgId);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

        @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SUPER_ADMIN')")
        @DeleteMapping("/delete-user/{id}")
        public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable String id) {

                ApiResponse<?> response = userService.deleteUserById(id);

                return ResponseEntity
                                .status(response.isSuccess() ? 200 : 400)
                                .body(response);
        }

}
