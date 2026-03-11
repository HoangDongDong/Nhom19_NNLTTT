package com.edulanguage.dao;

import com.edulanguage.entity.Room;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) cho Phòng học.
 */
public interface RoomDao {

    List<Room> findAll();
    Optional<Room> findById(Long id);
    Room save(Room room);
    void deleteById(Long id);
}

