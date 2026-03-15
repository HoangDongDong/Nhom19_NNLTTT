package com.edulanguage.repository;

import com.edulanguage.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.enrollment.id = :enrollmentId")
    List<Payment> findByEnrollmentId(@Param("enrollmentId") Long enrollmentId);
}
