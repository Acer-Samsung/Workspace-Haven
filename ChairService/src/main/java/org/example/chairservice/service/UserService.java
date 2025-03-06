package org.example.chairservice.service;

import org.example.chairservice.entity.User;
import org.example.chairservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createUser(User user) {
        if (userRepository.findByTelegramUsername(user.getTelegramUsername()).isPresent()) {
            return "User already exists";
        }
        userRepository.save(user);
        return "success";
    }

    public Optional<User> findUserByTelegramUsername(String telegramUsername) {
        return userRepository.findByTelegramUsername(telegramUsername);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
