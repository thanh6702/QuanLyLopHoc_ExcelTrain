package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Response.APIResponse;
import com.example.quanlylophoc.entity.Room;
import com.example.quanlylophoc.service.RoomSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomSerivce roomSerivce;

    @PostMapping
    public ResponseEntity<APIResponse<Room>> createRoom(@RequestBody Room room) {
        Room createdRoom = roomSerivce.createRoom(room);
        return ResponseEntity.ok(APIResponse.success(createdRoom));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Room>> updateRoom(
            @PathVariable Long id,
            @RequestBody Room updatedData,
            @RequestParam(value = "confirmationCode", required = false) String confirmationCode
    ) {
        Room updatedRoom = roomSerivce.updateRoom(id, updatedData, confirmationCode);
        return ResponseEntity.ok(APIResponse.success(updatedRoom));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteRoom(@PathVariable Long id) {
        roomSerivce.deleteRoom(id);
        return ResponseEntity.ok(APIResponse.success("Deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<Room>>> getAllRooms() {
        List<Room> rooms = roomSerivce.getAllRooms();
        return ResponseEntity.ok(APIResponse.success(rooms));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Room>> getRoomById(@PathVariable Long id) {
        Room room = roomSerivce.getRoomById(id);
        return ResponseEntity.ok(APIResponse.success(room));
    }
}
