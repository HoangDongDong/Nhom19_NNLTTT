package com.edulanguage.dao.impl;

import com.edulanguage.dao.StudentDao;
import com.edulanguage.entity.Student;
import com.edulanguage.repository.StudentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentDaoImpl implements StudentDao {

    private final StudentRepository studentRepository;

    public StudentDaoImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return studentRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return studentRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByPhoneAndIdNot(String phone, Long id) {
        return studentRepository.existsByPhoneAndIdNot(phone, id);
    }
}
