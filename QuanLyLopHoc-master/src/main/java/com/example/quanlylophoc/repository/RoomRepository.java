package com.example.quanlylophoc.repository;

import com.example.quanlylophoc.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findById(long id);

}
