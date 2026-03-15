package com.edulanguage.service.impl;

import com.edulanguage.dao.StudentDao;
import com.edulanguage.entity.Student;
import com.edulanguage.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Tầng nghiệp vụ: triển khai nghiệp vụ học viên.
 * Gọi StudentDao (Data Access), không gọi tầng Presentation.
 */
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;

    public StudentServiceImpl(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        return studentDao.findById(id);
    }

    @Override
    @Transactional
    public Student save(Student student) {
        return studentDao.save(student);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        studentDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return email != null && !email.isBlank() && studentDao.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return phone != null && !phone.isBlank() && studentDao.existsByPhone(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmailExcluding(String email, Long id) {
        return email != null && !email.isBlank() && studentDao.existsByEmailAndIdNot(email, id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhoneExcluding(String phone, Long id) {
        return phone != null && !phone.isBlank() && studentDao.existsByPhoneAndIdNot(phone, id);
    }
}
