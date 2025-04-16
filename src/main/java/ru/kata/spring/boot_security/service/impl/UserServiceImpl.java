package ru.kata.spring.boot_security.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.entity.User;
import ru.kata.spring.boot_security.repository.UserRepository;
import ru.kata.spring.boot_security.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUser(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));
    }

    @Transactional
    @Override
    public User saveUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        User userToUpdate = getUser(user.getId());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userToUpdate.setRoles(new HashSet<>(user.getRoles()));
        return userRepository.save(userToUpdate);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }
}