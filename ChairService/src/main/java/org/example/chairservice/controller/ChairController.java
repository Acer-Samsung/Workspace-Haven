package org.example.chairservice.controller;

import org.example.chairservice.entity.Chair;
import org.example.chairservice.service.ChairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chairs")
public class ChairController {
    private final ChairService chairService;

    @Autowired
    public ChairController(ChairService chairService) {
        this.chairService = chairService;
    }

    @PostMapping
    public ResponseEntity<Chair> createChair(@RequestBody Chair chair) {
        return ResponseEntity.ok(chairService.createChair(chair));
    }

    @GetMapping
    public ResponseEntity<List<Chair>> getAllChairs() {
        return ResponseEntity.ok(chairService.getAllChairs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chair> getChairById(@PathVariable Long id) {
        return chairService.getChairById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/holder/{telegramUsername}")
    public ResponseEntity<Chair> getChairByHolder(@PathVariable String telegramUsername) {
        return chairService.findChairByHolderTelegramUsername(telegramUsername)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{chairId}/assign/{telegramUsername}")
    public ResponseEntity<Chair> assignChair(@PathVariable Long chairId, @PathVariable String telegramUsername) {
        return ResponseEntity.ok(chairService.assignChairToUser(chairId, telegramUsername));
    }

    @PostMapping("/{chairId}/unassign")
    public ResponseEntity<Void> unassignChair(@PathVariable Long chairId) {
        chairService.unassignChair(chairId);
        return ResponseEntity.noContent().build();
    }
}
