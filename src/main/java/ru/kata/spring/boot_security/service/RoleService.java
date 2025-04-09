package ru.kata.spring.boot_security.service;

import ru.kata.spring.boot_security.entity.Role;

import java.util.List;

public interface RoleService {

    Role findByName(String name);

    List<Role> findAll();

    List<Role> findAllById(List<Long> roleIds);

    void save(Role role);
}