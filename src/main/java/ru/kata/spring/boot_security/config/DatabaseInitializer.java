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
        Role roleAdmin = roleService.findByName("ROLE_ADMIN");
        if (roleAdmin == null) {
            roleAdmin = new Role("ROLE_ADMIN");
            roleService.save(roleAdmin);
        }

        Role roleUser = roleService.findByName("ROLE_USER");
        if (roleUser == null) {
            roleUser = new Role("ROLE_USER");
            roleService.save(roleUser);
        }

        if (!userService.existsByUsername("admin")) {
            User admin = new User("admin", "admin", "admin", "admin", Set.of(roleAdmin));
            userService.saveUser(admin);
        }

        if (!userService.existsByUsername("user")) {
            User user = new User("user", "user", "user", "user", Set.of(roleUser));
            userService.saveUser(user);
        }
    }

}
