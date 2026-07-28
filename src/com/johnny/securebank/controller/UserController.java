package com.johnny.securebank.controller;

import com.johnny.securebank.dto.UserResponseDTO;
import com.johnny.securebank.model.User;
import com.johnny.securebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDTO createUser(@Valid @RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getUsers();
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@RequestBody User user, @PathVariable Long id) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
