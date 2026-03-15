package com.edulanguage.repository;

import com.edulanguage.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByClazzId(Long clazzId);
    
    int countByClazzId(Long clazzId);
    
    boolean existsByStudentIdAndClazzId(Long studentId, Long clazzId);
}
