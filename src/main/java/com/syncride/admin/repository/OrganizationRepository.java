package com.syncride.admin.repository;

import com.syncride.admin.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrganizationRepository extends MongoRepository<Organization, String> {
    
    Optional<Organization> findByOrgId(String orgId);
}
