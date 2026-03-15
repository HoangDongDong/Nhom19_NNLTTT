package com.edulanguage.service.impl;

import com.edulanguage.entity.Teacher;
import com.edulanguage.entity.UserAccount;
import com.edulanguage.entity.enums.Role;
import com.edulanguage.repository.TeacherRepository;
import com.edulanguage.repository.UserAccountRepository;
import com.edulanguage.service.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserAccountRepository userAccountRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository, 
                              UserAccountRepository userAccountRepository) {
        this.teacherRepository = teacherRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    @Override
    @Transactional
    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Xóa UserAccount liên kết trước
        Optional<UserAccount> accountOpt = userAccountRepository.findAll().stream()
            .filter(a -> a.getRole() == Role.TEACHER && id.equals(a.getRelatedId()))
            .findFirst();
            
        accountOpt.ifPresent(userAccountRepository::delete);
        
        teacherRepository.deleteById(id);
    }
}
