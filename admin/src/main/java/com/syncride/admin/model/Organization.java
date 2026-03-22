package com.syncride.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@Document(collection = "organizations")
public class Organization {

    @Id
    @JsonIgnore
    private String id;

    @Indexed(unique = true)
    @Field("org_id")
    @JsonProperty("org_id")
    private String orgId = UUID.randomUUID().toString();

    @Field("organization_name")
    @JsonProperty("organization_name")
    private String organizationName;

    @Field("organization_address")
    @JsonProperty("organization_address")
    private String organizationAddress;

    @Field("phone")
    @JsonProperty("phone")
    private String phone;

    @Field("is_enabled")
    @JsonProperty("is_enabled")
    private boolean enabled;
}
