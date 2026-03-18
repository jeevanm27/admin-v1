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
    private String _id;

    @Indexed(unique = true)
    @Field("org_id")
    @JsonProperty("org_id")
    private String org_id = UUID.randomUUID().toString();

    @JsonProperty("organizationName")
    private String OrganizationName;
}
