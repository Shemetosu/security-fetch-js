package ru.kata.spring.boot_security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(String username);
}