package com.example.quanlylophoc.service;

import com.example.quanlylophoc.Exception.*;
import com.example.quanlylophoc.entity.Room;
import com.example.quanlylophoc.repository.ClassScheduleRepository;
import com.example.quanlylophoc.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomSerivce {

    private final RoomRepository roomRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final ConfirmationTokenService confirmationTokenService ;

    public Room createRoom(Room room) {
        try {
            room.setName(room.getName());
            room.setCode(room.getCode());
            room.setCapacity(room.getCapacity());
            room.setLocation(room.getLocation());
            room.setCreatedAt(new Date());
            return roomRepository.save(room);
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public Room updateRoom(Long id, Room updatedData, String confirmationCode) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.ROOM_NOT_FOUND));

        boolean isAssigned = checkRoomAssigned(id);

        if (isAssigned) {
            if (confirmationCode == null) {
                String generatedCode = confirmationTokenService.generateForAction("UPDATE_ROOM", id);

                room.setConfirCode(generatedCode);
                room.setConfirmationCodeCreatedAt(LocalDateTime.now());
                roomRepository.save(room);

                throw new ConfirmationRequiredException(
                        ErrorCode.ROOM_ALREADY_ASSIGNED.getMessage(),
                        "UPDATE_ROOM",
                        generatedCode
                );
            }

            if (!confirmationCode.equals(room.getConfirCode())) {
                throw new InvalidConfirmationCodeException(ErrorCode.INVALID_CONFIRMATION_CODE);
            }

            if (room.getConfirmationCodeCreatedAt() == null ||
                    Duration.between(room.getConfirmationCodeCreatedAt(), LocalDateTime.now()).toMinutes() > 5) {
                throw new InvalidConfirmationCodeException(ErrorCode.CONFIRMATION_CODE_EXPIRED);
            }

            // Đúng mã và còn hạn → cho phép update
            room.setConfirCode(null);
            room.setConfirmationCodeCreatedAt(null);
        }

        room.setName(updatedData.getName());
        room.setCode(updatedData.getCode());
        room.setCapacity(updatedData.getCapacity());
        room.setLocation(updatedData.getLocation());

        return roomRepository.save(room);
    }



    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.ROOM_NOT_FOUND));

        if (checkRoomAssigned(id)) {
            throw new BadRequestException(ErrorCode.ROOM_ALREADY_ASSIGNED_CANNOT_DELETE);
        }

        try {
            roomRepository.delete(room);
        } catch (Exception e) {
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean checkRoomAssigned(Long roomId) {
        return classScheduleRepository.existsByRoomId(roomId);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.ROOM_NOT_FOUND));
    }
}