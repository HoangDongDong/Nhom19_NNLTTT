package com.edulanguage.service.impl;

import com.edulanguage.entity.Room;
import com.edulanguage.repository.RoomRepository;
import com.edulanguage.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }
}
