package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.entity.Room;
import com.example.quanlylophoc.service.RoomSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomSerivce roomSerivce;

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room createdRoom = roomSerivce.createRoom(room);
        return ResponseEntity.ok(createdRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long id,
            @RequestBody Room updatedData,
            @RequestParam(value = "confirmationCode", required = false) String confirmationCode
    ) {
        Room updatedRoom = roomSerivce.updateRoom(id, updatedData, confirmationCode);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomSerivce.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomSerivce.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Room>> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(Optional.ofNullable(roomSerivce.getRoomById(id)));
    }
}
