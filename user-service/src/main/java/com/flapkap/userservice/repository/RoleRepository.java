package com.flapkap.userservice.repository;

import com.flapkap.userservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByIdIn(List<Long> ids);

    Role getRoleByName(String name);
    Optional<Role> findById(Long id);
}