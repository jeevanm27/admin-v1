package com.syncride.userservice.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private Map<String, Object> extraFields;
    private static final Logger log = LoggerFactory.getLogger(ApiResponse.class);

    @JsonAnyGetter
    public Map<String, Object> getExtraFields() {
        return extraFields;
    }

    // { success: true, message: "..." }
    public static ApiResponse<Void> success(String message) {
        log.info("\nsuccess api response => message: "+message);
        return ApiResponse.<Void>builder().success(true).message(message).build();
    }

    // { success: true, [message: "...",] key: val, ... } — message null = omitted
    public static <T> ApiResponse<T> success(String message, Map<String, Object> extra) {
        log.info("\nsuccess api response => message: "+message+"\nextras: "+extra);
        return ApiResponse.<T>builder().success(true).message(message).extraFields(extra).build();
    }

    // { success: false, message: "..." }
    public static ApiResponse<Void> error(String message) {
        log.error("\nError api response => message: "+message);
        return ApiResponse.<Void>builder().success(false).message(message).build();
    }
}