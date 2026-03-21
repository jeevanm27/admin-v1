package com.syncride.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrganizationDTO {

    @JsonProperty("org_id")
    private String orgId;

    @JsonProperty("organization_name")
    private String organizationName;

    @JsonProperty("organization_address")
    private String organizationAddress;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("is_enabled")   // 🔥 important
    private boolean enabled;
}