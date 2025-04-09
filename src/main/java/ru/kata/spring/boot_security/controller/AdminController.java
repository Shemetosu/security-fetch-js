package ru.kata.spring.boot_security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.entity.Role;
import ru.kata.spring.boot_security.entity.User;
import ru.kata.spring.boot_security.service.RoleService;
import ru.kata.spring.boot_security.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String showAdminPage(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<User> users = userService.getAllUsers();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", users);
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "admin";
    }

    @GetMapping(value = "/addUser")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "addUser";
    }

    @PostMapping("/addUser")
    public String createUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "roles", required = false) List<Long> roleIds,
                             Model model,
                             Authentication authentication) {
        if (userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username",
                    "error.user",
                    "User with that email already exists!");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            model.addAttribute("rolesError", "At least one role is required!");
            bindingResult.reject("roles", "At least one role is required!");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("user", user);
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("currentUser", authentication.getPrincipal());
            model.addAttribute("activeTab", "new-user");
            return "admin";
        }
        Set<Role> roles = new HashSet<>(roleService.findAllById(roleIds));
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/updateUser")
    public String updateUser(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.getUser(id));
        model.addAttribute("roles", roleService.findAll());
        return "updateUser";
    }

    @PostMapping(value = "/updateUser")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam("roles") List<Long> roleIds) {
        Set<Role> roles = new HashSet<>(roleService.findAllById(roleIds));
        user.setRoles(roles);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/deleteUser")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}