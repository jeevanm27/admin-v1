package com.syncride.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "users")
public class User {

    @Id
    @JsonIgnore
    private String _id;

    @Indexed(unique = true)
    private String id = UUID.randomUUID().toString();

    private String role = "user"; // Default role
    private String name;

    @Indexed(unique = true)
    private String phone;

    private String email;
    private String gender;
    private String otp;

    @Field("org_id")
    @JsonProperty("org_id")
    private String orgId = "";

    @Field("otp_expires_at")
    @JsonProperty("otp_expires_at")
    private Date otpExpiresAt;

    private String fcmToken;

    @Field("Aadhar_url")
    @JsonProperty("Aadhar_url")
    private String aadharUrl;

    @Field("License_url")
    @JsonProperty("License_url")
    private String licenseUrl;

    private boolean isDriver = false;

    @JsonProperty("isDriver")
    public boolean isDriver() {
        return isDriver;
    }

    private Vehicle vehicle;

    @Field("driver_status")
    @JsonProperty("driver_status")
    private String driverStatus = "available";

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private List<RideHistory> history = new java.util.ArrayList<>();

    @JsonProperty("islogged")
    private boolean islogged = true;

    @Field("created_by")
    @JsonProperty("created_by")
    private String createdBy;

    @Field("deleted_by")
    @JsonProperty("deleted_by")
    private String deletedBy;

    @Field("created_at")
    @JsonProperty("created_at")
    private Date createdAt = new Date();

    @Field("updated_at")
    @JsonProperty("updated_at")
    private Date updatedAt = new Date();

    @Data
    public static class Vehicle {
        private String number;
        private String type;

        @Field("seat_capacity")
        @JsonProperty("seat_capacity")
        private int seatCapacity = 0;
    }

    @Data
    public static class RideHistory {
        @JsonProperty("rideId")
        private String rideId;

        @JsonProperty("rideType")
        private String rideType;

        @Field("driver_name")
        @JsonProperty("driver_name")
        private String driverName;

        @Field("driver_phone")
        @JsonProperty("driver_phone")
        private String driverPhone;

        @JsonProperty("cost")
        private Double cost;

        @Field("booking_type")
        @JsonProperty("booking_type")
        private Object bookingType;

        @Field("updated_at")
        @JsonProperty("updated_at")
        private Date updatedAt = new Date();

        @JsonProperty("pickupLocation")
        private String pickupLocation;

        @JsonProperty("dropoffLocation")
        private String dropoffLocation;

        @JsonProperty("startTime")
        private String startTime;

        @JsonProperty("startDate")
        private String startDate;

        @JsonProperty("remarks")
        private String remarks;

        @JsonProperty("rideStatus")
        private String rideStatus = "pending";
    }
}