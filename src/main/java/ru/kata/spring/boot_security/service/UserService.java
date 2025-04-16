package ru.kata.spring.boot_security.service;

import ru.kata.spring.boot_security.entity.User;

import java.util.List;

public interface UserService {

    boolean existsByUsername(String username);

    List<User> getAllUsers();

    User getUser(Long id);

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);
}