package ru.kata.spring.boot_security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.entity.Role;
import ru.kata.spring.boot_security.entity.User;
import ru.kata.spring.boot_security.service.RoleService;
import ru.kata.spring.boot_security.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("isAuthenticated()")
public class UserRestController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserRestController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/get")
    public ResponseEntity<User> getUser(@RequestParam Long userId) {
        User user = userService.getUser(userId);
        return user != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/add")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        user.setRoles(resolveRoles(user.getRoles()));
        userService.saveUser(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody @Valid User user) {
        User existingUser = userService.getUser(user.getId());
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setRoles(resolveRoles(user.getRoles()));

        userService.updateUser(existingUser);
        return ResponseEntity.ok(existingUser);
    }

    @GetMapping("/delete")
    public ResponseEntity<User> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }

    private Set<Role> resolveRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return new HashSet<>();
        }
        List<Long> rolesIds = roles.stream()
                .filter(Objects::nonNull)
                .map(Role::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return new HashSet<>(roleService.findAllById(rolesIds));
    }
}