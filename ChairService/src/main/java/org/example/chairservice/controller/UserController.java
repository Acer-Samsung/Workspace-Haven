package org.example.chairservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.chairservice.entity.User;
import org.example.chairservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check/{telegramUsername}")
    public String checkUser(@PathVariable String telegramUsername) {
        return userService.findUserByTelegramUsername(telegramUsername).isPresent() ? "success" : "failure";
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User incominguser) {
        User user = new User();
        user.setName(incominguser.getName());
        user.setTelegramUsername(incominguser.getTelegramUsername());
        user.setAdmin(false);
        log.info("Creating user: {}", user);
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{telegramUsername}")
    public ResponseEntity<User> getUserByTelegramUsername(@PathVariable String telegramUsername) {
        return userService.findUserByTelegramUsername(telegramUsername)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}