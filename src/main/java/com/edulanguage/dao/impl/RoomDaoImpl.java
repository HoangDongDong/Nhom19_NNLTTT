package com.edulanguage.dao.impl;

import com.edulanguage.dao.RoomDao;
import com.edulanguage.entity.Room;
import com.edulanguage.repository.RoomRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoomDaoImpl implements RoomDao {

    private final RoomRepository roomRepository;

    public RoomDaoImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public void deleteById(Long id) {
        roomRepository.deleteById(id);
    }
}

