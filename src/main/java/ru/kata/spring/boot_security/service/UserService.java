package ru.kata.spring.boot_security.service;

import ru.kata.spring.boot_security.entity.User;

import java.util.List;

public interface UserService {

    public boolean existsByUsername(String username);

    public List<User> getAllUsers();

    public User getUser(Long id);

    public void saveUser(User user);

    public void updateUser(User user);

    public void deleteUser(Long id);
}