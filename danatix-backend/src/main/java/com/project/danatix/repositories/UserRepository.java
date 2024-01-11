package com.project.danatix.repositories;

import com.project.danatix.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findUserByEmailVerificationToken(String token);

    Optional<User> findUserByName(String name);

}
