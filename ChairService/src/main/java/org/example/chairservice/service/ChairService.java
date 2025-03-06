//package org.example.chairservice.service;
//
//import org.example.chairservice.entity.Chair;
//import org.example.chairservice.entity.User;
//import org.example.chairservice.repositories.ChairRepository;
//import org.example.chairservice.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ChairService {
//    private final ChairRepository chairRepository;
//    private final UserRepository userRepository;
//
//    @Autowired
//    public ChairService(ChairRepository chairRepository, UserRepository userRepository) {
//        this.chairRepository = chairRepository;
//        this.userRepository = userRepository;
//    }
//
//    public Chair createChair(Chair chair) {
//        return chairRepository.save(chair);
//    }
//
//    public List<Chair> getAllChairs() {
//        return chairRepository.findAll();
//    }
//
//    public Optional<Chair> getChairById(Long id) {
//        return chairRepository.findById(id);
//    }
//
//    public Optional<Chair> findChairByHolderTelegramUsername(String telegramUsername) {
//        return chairRepository.findByHolder_TelegramUsername(telegramUsername);
//    }
//
//    public Chair assignChairToUser(Long chairId, String telegramUsername) {
//        User user = userRepository.findByTelegramUsername(telegramUsername)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Chair chair = chairRepository.findById(chairId)
//                .orElseThrow(() -> new RuntimeException("Chair not found"));
//
//        if (!chair.isAvailable()) {
//            throw new RuntimeException("Chair is already occupied");
//        }
//
//        chair.setHolder(user);
//        chair.setAvailable(false);
//        return chairRepository.save(chair);
//    }
//
//    public void unassignChair(Long chairId) {
//        Chair chair = chairRepository.findById(chairId)
//                .orElseThrow(() -> new RuntimeException("Chair not found"));
//
//        chair.setHolder(null);
//        chair.setAvailable(true);
//        chairRepository.save(chair);
//    }
//}

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

    // Create a new chair
    public Chair createChair(Chair chair) {
        return chairRepository.save(chair);
    }

    // Get all available chairs
//    public List<Chair> getAllChairs() {
//        return chairRepository.findAll();
//    }

    public List<Chair> getAllChairs() {
        return chairRepository.findByHolderIsNull();
    }

    // Find chair by ID
    public Optional<Chair> getChairById(Long id) {
        return chairRepository.findById(id);
    }

    // Get the chair currently booked by a user
    public boolean findChairByHolderTelegramUsername(String telegramUsername) {
//        return chairRepository.findByHolder_TelegramUsername(telegramUsername);
        return chairRepository.existsByHolder_TelegramUsername(telegramUsername);
    }

    // Book a specific chair by chairId
    public String bookChair(Integer chairId, String telegramUsername) {
        if (chairRepository.findById(Long.valueOf(chairId)).isEmpty()) {
            return "Chair not found";
        }
        if (userRepository.findByTelegramUsername(telegramUsername).isEmpty()) {
            return "User not found";
        }
        User user = userRepository.findByTelegramUsername(telegramUsername).get();
        Chair chair = chairRepository.findById(Long.valueOf(chairId)).get();

        if (!chair.isAvailable()) {
            return ("Chair is already occupied");
        }
        boolean byHolderId = chairRepository.existsByHolder_Id((user.getId()));
        if (byHolderId) {
            return ("You already have a chair");
        }
        chair.setHolder(user);
        chair.setAvailable(false);
        chairRepository.save(chair);
        return "Successfully booked a chair - " + chair.getId();
    }

    // Cancel a booking by chairId (only if user owns it)
    public String unassignChair(String telegramUsername) {
        Long chairId = chairRepository.findByHolder_TelegramUsername(telegramUsername).get().getId();
        Chair chair = chairRepository.findById(chairId).get();

        if (chair.getHolder().getTelegramUsername().equals(telegramUsername)) {
            chair.setHolder(null);
            chair.setAvailable(true);
            chairRepository.save(chair);
            return "Successfully unassigned a chair";
        } else {
            return "You don't own this chair";
        }
    }
}
