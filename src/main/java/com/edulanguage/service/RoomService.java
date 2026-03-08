package com.edulanguage.service;

import com.edulanguage.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomService {

    List<Room> findAll();
    Optional<Room> findById(Long id);
}
