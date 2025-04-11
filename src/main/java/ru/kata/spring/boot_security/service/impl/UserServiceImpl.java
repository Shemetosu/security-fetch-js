package ru.kata.spring.boot_security.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.entity.User;
import ru.kata.spring.boot_security.repository.UserRepository;
import ru.kata.spring.boot_security.service.UserService;

import javax.persistence.EntityNotFoundException;
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
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        User userToUpdate = getUser(user.getId());
        userToUpdate.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));
        userRepository.save(userToUpdate);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }
}