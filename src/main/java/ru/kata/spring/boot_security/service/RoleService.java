package ru.kata.spring.boot_security.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.entity.Role;
import ru.kata.spring.boot_security.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleRepository.findRoleByName(name);
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Role> findAllById(List<Long> roleIds) {
        return roleRepository.findAllById(roleIds);
    }

    @Transactional
    public void save(Role role) {
        roleRepository.save(role);
    }
}