package com.syncride.userservice.repository;

import java.util.List;
import com.syncride.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByPhone(String phone);

    Optional<User> findByIdEquals(String id);

    List<User> findByIsDriverTrueAndDriverStatus(String driverStatus);

    List<User> findByOrgId(String orgId);
}