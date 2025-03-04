package org.example.chairservice.service;

import org.example.chairservice.entity.Chair;
import org.example.chairservice.entity.User;
import org.example.chairservice.repositories.ChairRepository;
import org.example.chairservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChairService {
    private final ChairRepository chairRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChairService(ChairRepository chairRepository, UserRepository userRepository) {
        this.chairRepository = chairRepository;
        this.userRepository = userRepository;
    }

    public Chair createChair(Chair chair) {
        return chairRepository.save(chair);
    }

    public List<Chair> getAllChairs() {
        return chairRepository.findAll();
    }

    public Optional<Chair> getChairById(Long id) {
        return chairRepository.findById(id);
    }

    public Optional<Chair> findChairByHolderTelegramUsername(String telegramUsername) {
        return chairRepository.findByHolder_TelegramUsername(telegramUsername);
    }

    public Chair assignChairToUser(Long chairId, String telegramUsername) {
        User user = userRepository.findByTelegramUsername(telegramUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new RuntimeException("Chair not found"));

        if (!chair.isAvailable()) {
            throw new RuntimeException("Chair is already occupied");
        }

        chair.setHolder(user);
        chair.setAvailable(false);
        return chairRepository.save(chair);
    }

    public void unassignChair(Long chairId) {
        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new RuntimeException("Chair not found"));

        chair.setHolder(null);
        chair.setAvailable(true);
        chairRepository.save(chair);
    }
}
