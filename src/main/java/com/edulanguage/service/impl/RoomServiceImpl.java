package com.edulanguage.service.impl;

import com.edulanguage.dao.RoomDao;
import com.edulanguage.entity.Room;
import com.edulanguage.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;

    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        return roomDao.findById(id);
    }

    @Override
    @Transactional
    public Room save(Room room) {
        return roomDao.save(room);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        roomDao.deleteById(id);
    }
}
