package ru.kata.spring.boot_security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.entity.Role;
import ru.kata.spring.boot_security.entity.User;
import ru.kata.spring.boot_security.service.RoleService;
import ru.kata.spring.boot_security.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        String firstname = (String) payload.get("firstname");
        String lastname = (String) payload.get("lastname");
        String password = (String) payload.get("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Integer> roleIds = (List<Integer>) payload.get("roles");
        if (roleIds == null || roleIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Long> roleIdsLong = roleIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());

        Set<Role> roles = new HashSet<>(roleService.findAllById(roleIdsLong));
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, firstname, lastname, roles);
        userService.saveUser(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody Map<String, Object> payload) {
        try {
            Long id = Long.valueOf(payload.get("id").toString());
            User existingUser = userService.getUser(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            String username = (String) payload.get("username");
            String firstname = (String) payload.get("firstname");
            String lastname = (String) payload.get("lastname");
            String newPassword = (String) payload.get("password");
            List<Integer> roleIds = (List<Integer>) payload.get("roles");

            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            existingUser.setUsername(username);
            existingUser.setFirstname(firstname);
            existingUser.setLastname(lastname);
            existingUser.setPassword(newPassword);

            List<Long> roleIdsLong = roleIds.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            Set<Role> roles = new HashSet<>(roleService.findAllById(roleIdsLong));
            existingUser.setRoles(roles);

            userService.updateUser(existingUser);
            return ResponseEntity.ok(existingUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<User> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }
}