package ru.kata.spring.boot_security.config;

import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.entity.Role;
import ru.kata.spring.boot_security.entity.User;
import ru.kata.spring.boot_security.service.RoleService;
import ru.kata.spring.boot_security.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class DatabaseInitializer {

    private final UserService userService;
    private final RoleService roleService;

    public DatabaseInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void init() {
        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleUser = new Role("ROLE_USER");
        roleService.save(roleAdmin);
        roleService.save(roleUser);

        User admin = new User(
                "admin",
                "admin",
                "admin",
                "admin",
                Set.of(roleAdmin));
        User user = new User(
                "user",
                "user",
                "user",
                "user",
                Set.of(roleUser));
        userService.saveUser(admin);
        userService.saveUser(user);
    }
}
