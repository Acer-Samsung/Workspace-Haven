package org.example.chairservice.controller;

import org.example.chairservice.entity.Chair;
import org.example.chairservice.service.ChairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
//
//@RestController
//@RequestMapping("/chairs")
//public class ChairController {
//    private final ChairService chairService;
//
//    @Autowired
//    public ChairController(ChairService chairService) {
//        this.chairService = chairService;
//    }
//
//    @PostMapping
//    public ResponseEntity<Chair> createChair(@RequestBody Chair chair) {
//        return ResponseEntity.ok(chairService.createChair(chair));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Chair>> getAllChairs() {
//        return ResponseEntity.ok(chairService.getAllChairs());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Chair> getChairById(@PathVariable Long id) {
//        return chairService.getChairById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/holder/{telegramUsername}")
//    public ResponseEntity<Chair> getChairByHolder(@PathVariable String telegramUsername) {
//        return chairService.findChairByHolderTelegramUsername(telegramUsername)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PostMapping("/{chairId}/assign/{telegramUsername}")
//    public ResponseEntity<Chair> assignChair(@PathVariable Long chairId, @PathVariable String telegramUsername) {
//        return ResponseEntity.ok(chairService.assignChairToUser(chairId, telegramUsername));
//    }
//
//    @PostMapping("/{chairId}/unassign")
//    public ResponseEntity<Void> unassignChair(@PathVariable Long chairId) {
//        chairService.unassignChair(chairId);
//        return ResponseEntity.noContent().build();
//    }
//}

@RestController
@RequestMapping("/chairs") // Base URL for all chair-related operations
public class ChairController {

    private final ChairService chairService;

    @Autowired
    public ChairController(ChairService chairService) {
        this.chairService = chairService;
    }

    // Create a chair
    @PostMapping
    public ResponseEntity<Chair> createChair(@RequestBody Chair chair) {
        return ResponseEntity.ok(chairService.createChair(chair));
    }

    // Get all available chairs
    @GetMapping
    public ResponseEntity<List<Chair>> getAllChairs() {
        return ResponseEntity.ok(chairService.getAllChairs());
    }

    // Get a chair by ID
    @GetMapping("/{id}")
    public ResponseEntity<Chair> getChairById(@PathVariable Long id) {
        return chairService.getChairById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get the chair booked by a specific user (mybooking)
    @GetMapping("/mybooking/{telegramUsername}")
    public boolean getChairByHolder(@PathVariable String telegramUsername) {
        return chairService.findChairByHolderTelegramUsername(telegramUsername);
    }

    // Book a chair (assign a chair to a user)
    @PostMapping("/book")
    public ResponseEntity<?> bookChair(@RequestBody Map<String, String> request) {
        String telegramUsername = request.get("telegramUsername");
        Integer chairId = Integer.valueOf(request.get("chairId"));
        return ResponseEntity.ok(chairService.bookChair(chairId, telegramUsername));
    }

    // Cancel chair booking (unassign chair from a user)
    @DeleteMapping("/cancel/{telegramUsername}")
    public String cancelBooking(@PathVariable String telegramUsername) {
        return chairService.unassignChair(telegramUsername);
    }
}
