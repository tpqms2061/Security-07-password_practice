package com.ssh.password_practice.repository;


import com.ssh.password_practice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String Username);

    Optional<User> findByEmail(String Email);

    boolean exitByUsername(String Username);

    boolean exitByEmail(String Email);


}
