package com.syncride.userservice.service;

import com.syncride.userservice.dto.ApiResponse;
import com.syncride.userservice.model.User;
import com.syncride.userservice.repository.UserRepository;
import com.syncride.userservice.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final CloudinaryService cloudinaryService;

    // CREATE USER — matches Node.js createUser exactly
    public ApiResponse<?> createUser(String phone, String username, String fcmToken,
            String gender, String role, String otp,
            String number, String type, int seatCapacity,
            String aadhaarUrl, String licenseUrl) {
        // Exactly matches Node.js: !phone || !username || !fcmToken || !gender || !role
        if (phone == null || phone.isBlank() ||
                username == null || username.isBlank() ||
                fcmToken == null || fcmToken.isBlank() ||
                gender == null || gender.isBlank() ||
                role == null || role.isBlank()) {
            return ApiResponse.error("Details are missing. Fill all fields.");
        }

        try {
            Optional<User> existing = userRepository.findByPhone(phone);

            if (existing.isPresent()) {
                return ApiResponse.error("User already exists for this phone number.");
            }

            User user = new User();
            user.setRole(role);
            user.setPhone(phone);
            user.setGender(gender);
            user.setName(username);
            user.setFcmToken(fcmToken);
            user.setOtp(otp);
            user.setOtpExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
            user.setCreatedBy(role);

            if ("driver".equalsIgnoreCase(role)) {
                user.setDriver(true);
                user.setAadharUrl(aadhaarUrl);
                user.setLicenseUrl(licenseUrl);

                if (number != null && !number.isBlank()) {
                    User.Vehicle vehicle = new User.Vehicle();
                    vehicle.setNumber(number);
                    vehicle.setType(type);
                    vehicle.setSeatCapacity(seatCapacity);
                    user.setVehicle(vehicle);
                }
            }

            Date now = new Date();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            userRepository.save(user);

            return ApiResponse.success("User successfully created.");

        } catch (Exception e) {
            return ApiResponse.error("Failed to create user in DB.");
        }
    }

    // UPDATE OTP
    public ApiResponse<?> updateOtp(String otp, String phone, String fcmToken) {

        try {

            Optional<User> optionalUser = userRepository.findByPhone(phone);

            if (optionalUser.isEmpty()) {
                return ApiResponse.error("User not found");
            }

            User user = optionalUser.get();

            user.setOtp(otp);

            if (fcmToken != null && !fcmToken.isBlank()) {
                user.setFcmToken(fcmToken);
            }

            user.setOtpExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
            user.setUpdatedAt(new Date());

            userRepository.save(user);

            return ApiResponse.success("Successfully updated the details of the User to DB");

        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // VERIFY OTP
    public ApiResponse<?> verifyOtp(String phone, String otp) {

        try {

            Optional<User> optionalUser = userRepository.findByPhone(phone);

            if (optionalUser.isEmpty()) {
                return ApiResponse.error("User not found");
            }

            User user = optionalUser.get();

            if (user.getOtp() == null ||
                    !user.getOtp().equals(otp) ||
                    user.getOtpExpiresAt() == null ||
                    user.getOtpExpiresAt().before(new Date())) {

                return ApiResponse.error("Invalid OTP or OTP Expired.");
            }

            user.setOtp("");
            user.setIslogged(true);
            user.setUpdatedAt(new Date());

            userRepository.save(user);

            return ApiResponse.success("Successfully Verified",
                    Map.of(
                            "id", user.getId(),
                            "role", user.getRole(),
                            "isDriver", user.isDriver()));

        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // GET USER
    public ApiResponse<?> getUser(String id) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        return ApiResponse.success(null, Map.of("user", optionalUser.get()));
    }

    // GET USER FCM
    public ApiResponse<?> getUserFcm(String id) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        Map<String, Object> result = new HashMap<>();

        result.put("token", user.getFcmToken());
        result.put("isLogged", user.isIslogged());

        return ApiResponse.success(null, result);
    }

    // UPDATE HISTORY
    public ApiResponse<?> updateHistory(String id, User.RideHistory details) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        details.setUpdatedAt(new Date());

        user.getHistory().add(details);

        user.setUpdatedAt(new Date());

        userRepository.save(user);

        return ApiResponse.success("Updated the History Successfully");
    }

    // REMOVE RIDE HISTORY
    public ApiResponse<?> removeRideHistory(String id, String poolId) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        boolean removed = user.getHistory()
                .removeIf(ride -> Objects.equals(ride.getRideId(), poolId));

        if (!removed) {
            return ApiResponse.error("Ride not found in history");
        }

        user.setUpdatedAt(new Date());

        userRepository.save(user);

        return ApiResponse.success("removed ride from history Successfully");
    }

    // LOGOUT
    public ApiResponse<?> logout(String id) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        user.setIslogged(false);
        user.setUpdatedAt(new Date());

        userRepository.save(user);

        return ApiResponse.success("Logged Out Successfully");
    }

    // GET USER HISTORY
    public ApiResponse<?> getUserHistory(String id) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        return ApiResponse.success(
                "History Retrieved Successfully",
                Map.of("history", optionalUser.get().getHistory()));
    }

    // UPDATE CANCELLED RIDE
    public ApiResponse<?> updateRideCancelled(String id, String poolId) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        for (User.RideHistory ride : user.getHistory()) {

            if (Objects.equals(ride.getRideId(), poolId)) {

                ride.setRideStatus("cancelled");
                ride.setUpdatedAt(new Date());

                userRepository.save(user);

                return ApiResponse.success("Updated the Status of the Ride Successfully");
            }
        }

        return ApiResponse.error("Ride not found");
    }

    // UPDATE COMPLETE RIDE
    public ApiResponse<?> updateCompleteRide(String id, String poolId) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        for (User.RideHistory ride : user.getHistory()) {

            if (Objects.equals(ride.getRideId(), poolId)) {

                ride.setRideStatus("completed");
                ride.setUpdatedAt(new Date());

                userRepository.save(user);

                return ApiResponse.success("Updated the Status of the Ride Successfully");
            }
        }

        return ApiResponse.error("Ride not found");
    }

    public ApiResponse<?> updateRideLeave(String id, String poolId) {
        return updateRideCancelled(id, poolId);
    }

    // REQUEST COMPLETE
    public ApiResponse<?> updateRequestRideComplete(String id, String rideId) {
        return updateCompleteRide(id, rideId);
    }

    public ApiResponse<?> updateRequestRideConfirmed(String id, String rideId) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        for (User.RideHistory ride : user.getHistory()) {

            if (Objects.equals(ride.getRideId(), rideId)) {

                ride.setRideStatus("confirmed");
                ride.setUpdatedAt(new Date());

                userRepository.save(user);

                return ApiResponse.success("Updated the Status of the Ride Successfully");
            }
        }

        return ApiResponse.error("Ride not found");
    }

    // REQUEST CANCEL
    public ApiResponse<?> updateRequestRideCancelled(String id, String rideId, String remarks) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        for (User.RideHistory ride : user.getHistory()) {

            if (Objects.equals(ride.getRideId(), rideId)) {

                ride.setRideStatus("cancelled");
                ride.setRemarks(remarks);
                ride.setUpdatedAt(new Date());

                userRepository.save(user);

                return ApiResponse.success("Updated the Status of the Ride Successfully");
            }
        }

        return ApiResponse.error("Ride not found");
    }

    // DRIVER SEARCH — uses $geoWithin/$centerSphere matching Node.js exactly
    public ApiResponse<?> getDrivers(double lat, double lng) {

        Query query = Query.query(
                Criteria.where("role").is("driver")
                        .and("driver_status").is("available")
                        .and("islogged").is(true)
                        .and("location").withinSphere(
                                new Circle(new Point(lng, lat), 5.0 / 6378.1)));

        List<User> drivers = mongoTemplate.find(query, User.class);

        return ApiResponse.success(null, Map.of("drivers", drivers));
    }

    // SPECIFIC DRIVER SEARCH — uses $geoWithin/$centerSphere matching Node.js
    // exactly
    public ApiResponse<?> getSpecificDrivers(double lat, double lng, String type) {

        Query query = Query.query(
                Criteria.where("role").is("driver")
                        .and("vehicle.type").is(type)
                        .and("driver_status").is("available")
                        .and("islogged").is(true)
                        .and("location").withinSphere(
                                new Circle(new Point(lng, lat), 5.0 / 6378.1)));

        List<User> drivers = mongoTemplate.find(query, User.class);

        return ApiResponse.success(null, Map.of("drivers", drivers));
    }

    // SET DRIVER STATUS
    public ApiResponse<?> setDriverStatus(String id, String status) {

        Optional<User> optionalUser = userRepository.findByIdEquals(id);

        if (optionalUser.isEmpty()) {
            return ApiResponse.error("User not found");
        }

        User user = optionalUser.get();

        user.setDriverStatus(status);
        user.setUpdatedAt(new Date());

        userRepository.save(user);

        return ApiResponse.success("Successfully Updated the Status");
    }

    // UPDATE LOCATION
    public ApiResponse<?> updateLocation(String id, double lat, double lng) {
        try {
            Optional<User> optionalUser = userRepository.findByIdEquals(id);

            if (optionalUser.isEmpty()) {
                return ApiResponse.error("User not Found!");
            }

            User user = optionalUser.get();

            user.setLocation(new org.springframework.data.mongodb.core.geo.GeoJsonPoint(lng, lat));
            user.setUpdatedAt(new Date());

            userRepository.save(user);

            return ApiResponse.success("Location updated successfully!");

        } catch (Exception e) {
            return ApiResponse.error("Failed to update the Location.");
        }
    }

    public ApiResponse<?> getProfile(String id) {

        try {

            Optional<User> optionalUser = userRepository.findByIdEquals(id);

            if (optionalUser.isEmpty()) {
                return ApiResponse.error("User not Found!");
            }

            User user = optionalUser.get();

            int demand = 0;
            int schedule = 0;
            int pool = 0;

            for (User.RideHistory ride : user.getHistory()) {

                if ("completed".equals(ride.getRideStatus())) {

                    if ("demand".equals(ride.getRideType())) {
                        demand++;
                    }

                    if ("scheduled".equals(ride.getRideType())) {
                        schedule++;
                    }

                    if ("pool".equals(ride.getRideType())) {
                        pool++;
                    }
                }
            }

            Map<String, Object> rideCount = new HashMap<>();
            rideCount.put("demand", demand);
            rideCount.put("schedule", schedule);
            rideCount.put("pool", pool);

            return ApiResponse.success(
                    "Profile Sent Successfully",
                    Map.of(
                            "details", user,
                            "rideCount", rideCount));

        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<?> updateScheduleRideHistory(String id, String rideId, Map<String, Object> details) {

        try {

            Optional<User> optionalUser = userRepository.findByIdEquals(id);

            if (optionalUser.isEmpty()) {
                return ApiResponse.error("User not found");
            }

            User user = optionalUser.get();

            for (User.RideHistory ride : user.getHistory()) {

                if (Objects.equals(ride.getRideId(), rideId)) {

                    ride.setStartTime((String) details.get("startTime"));
                    ride.setStartDate((String) details.get("startDate"));
                    ride.setPickupLocation((String) details.get("pickupLocation"));
                    ride.setDropoffLocation((String) details.get("dropoffLocation"));

                    ride.setBookingType(details.get("seats"));

                    ride.setUpdatedAt(new Date());

                    user.setUpdatedAt(new Date());

                    userRepository.save(user);

                    return ApiResponse.success("Successfully updated the history details.");
                }
            }

            return ApiResponse.error("Ride not found");

        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<?> updateRideHistory(String id, String rideId, Map<String, Object> details) {

        try {

            Optional<User> optionalUser = userRepository.findByIdEquals(id);

            if (optionalUser.isEmpty()) {
                return ApiResponse.error("User not found");
            }
            User user = optionalUser.get();

            for (User.RideHistory ride : user.getHistory()) {

                if (Objects.equals(ride.getRideId(), rideId)) {

                    ride.setStartTime((String) details.get("startTime"));
                    ride.setStartDate((String) details.get("startDate"));
                    ride.setPickupLocation((String) details.get("pickupLocation"));
                    ride.setDropoffLocation((String) details.get("dropoffLocation"));

                    if (details.get("cost") != null) {
                        ride.setCost(Double.valueOf(details.get("cost").toString()));
                    }
                    ride.setBookingType(details.get("seats"));

                    ride.setUpdatedAt(new Date());
                    user.setUpdatedAt(new Date());
                    userRepository.save(user);
                    return ApiResponse.success("Successfully updated the history details.");
                }
            }

            return ApiResponse.error("Ride not found");

        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // UPLOAD DOCUMENTS (matches Node.js uploadDocuments exactly)
    public ApiResponse<?> uploadDocuments(String id, String number, String type, int seatCapacity,
            MultipartFile aadhaar, MultipartFile license) {
        try {

            if (aadhaar == null || aadhaar.isEmpty() ||
                    license == null || license.isEmpty() ||
                    number == null || number.isBlank()) {
                return ApiResponse.error("Upload aadhaar, license and vehicle details");
            }

            Optional<User> optionalUser = userRepository.findByIdEquals(id);

            if (optionalUser.isEmpty()) {
                return ApiResponse.error("User not Found!");
            }

            User user = optionalUser.get();

            // Upload files to Cloudinary
            String aadhaarUrl = cloudinaryService.uploadFile(aadhaar);
            String licenseUrl = cloudinaryService.uploadFile(license);

            user.setDriver(true);
            user.setAadharUrl(aadhaarUrl);
            user.setLicenseUrl(licenseUrl);

            User.Vehicle vehicle = new User.Vehicle();
            vehicle.setNumber(number);
            vehicle.setType(type);
            vehicle.setSeatCapacity(seatCapacity);
            user.setVehicle(vehicle);

            user.setUpdatedAt(new Date());

            userRepository.save(user);

            return ApiResponse.success("Successfully uploaded the documents");

        } catch (Exception e) {
            return ApiResponse.error("Failed to upload the documents");
        }
    }

//    // ADD ADMIN — called by admin-service to register an admin/superadmin user
//    public ApiResponse<?> addAdmin(String phone, String username, String fcmToken,
//            String gender, String role, String orgId) {
//
//        // Validate role
//        if (!"admin".equalsIgnoreCase(role) && !"superadmin".equalsIgnoreCase(role)) {
//            return ApiResponse.error("Role must be 'admin' or 'superadmin'.");
//        }
//
//        // Validate org_id
//        if (orgId == "" || orgId.isBlank()) {
//            return ApiResponse.error("org_id is required for admin/superadmin.");
//        }
//
//        // Validate required fields
//        if (phone == null || phone.isBlank() ||
//                username == null || username.isBlank() ||
//                fcmToken == null || fcmToken.isBlank() ||
//                gender == null || gender.isBlank()) {
//            return ApiResponse.error("Details are missing. Fill all fields.");
//        }
//
//        try {
//            if (userRepository.findByPhone(phone).isPresent()) {
//                return ApiResponse.error("User already exists for this phone number.");
//            }
//
//            User user = new User();
//            user.setPhone(phone);
//            user.setName(username);
//            user.setFcmToken(fcmToken);
//            user.setGender(gender);
//            user.setRole(role.toLowerCase());
//            user.setOrgId();
//            user.setCreatedBy(role.toLowerCase());
//
//            Date now = new Date();
//            user.setCreatedAt(now);
//            user.setUpdatedAt(now);
//
//            userRepository.save(user);
//
//            return ApiResponse.success("Admin user created successfully.",
//                    Map.of("id", user.getId(), "role", user.getRole()));
//
//        } catch (Exception e) {
//            return ApiResponse.error("Failed to create admin: " + e.getMessage());
//        }
//    }

}