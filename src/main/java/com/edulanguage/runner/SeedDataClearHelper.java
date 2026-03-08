package com.edulanguage.runner;

import com.edulanguage.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper để xóa dữ liệu trong transaction riêng (commit trước khi seed insert).
 * Tránh lỗi duplicate key do Hibernate flush INSERT trước DELETE trong cùng transaction.
 */
@Component
public class SeedDataClearHelper {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClazzRepository clazzRepository;
    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RoomRepository roomRepository;
    private final TeacherRepository teacherRepository;

    public SeedDataClearHelper(PaymentRepository paymentRepository,
                               InvoiceRepository invoiceRepository,
                               AttendanceRepository attendanceRepository,
                               EnrollmentRepository enrollmentRepository,
                               ClazzRepository clazzRepository,
                               UserAccountRepository userAccountRepository,
                               StudentRepository studentRepository,
                               CourseRepository courseRepository,
                               RoomRepository roomRepository,
                               TeacherRepository teacherRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.clazzRepository = clazzRepository;
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.roomRepository = roomRepository;
        this.teacherRepository = teacherRepository;
    }

    /**
     * Xóa toàn bộ dữ liệu mẫu. Chạy trong transaction riêng và commit ngay,
     * để lần insert sau không bị duplicate key.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearAll() {
        paymentRepository.deleteAll();
        invoiceRepository.deleteAll();
        attendanceRepository.deleteAll();
        enrollmentRepository.deleteAll();
        clazzRepository.deleteAll();
        userAccountRepository.deleteAll();
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        roomRepository.deleteAll();
        teacherRepository.deleteAll();
        System.out.println("  Đã xóa dữ liệu cũ.");
    }
}
