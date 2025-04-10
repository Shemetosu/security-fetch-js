package ru.kata.spring.boot_security.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.entity.Role;
import ru.kata.spring.boot_security.repository.RoleRepository;
import ru.kata.spring.boot_security.service.RoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> findAllById(List<Long> roleIds) {
        return roleRepository.findAllById(roleIds);
    }

    @Transactional
    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }
}