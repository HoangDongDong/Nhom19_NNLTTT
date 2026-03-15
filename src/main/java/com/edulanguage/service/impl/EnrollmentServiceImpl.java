package com.edulanguage.service.impl;

import com.edulanguage.dao.ClazzDao;
import com.edulanguage.dao.EnrollmentDao;
import com.edulanguage.dao.InvoiceDao;
import com.edulanguage.dao.StudentDao;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Invoice;
import com.edulanguage.entity.Student;
import com.edulanguage.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentDao enrollmentDao;
    private final StudentDao studentDao;
    private final ClazzDao clazzDao;
    private final InvoiceDao invoiceDao;

    public EnrollmentServiceImpl(EnrollmentDao enrollmentDao, StudentDao studentDao, ClazzDao clazzDao, InvoiceDao invoiceDao) {
        this.enrollmentDao = enrollmentDao;
        this.studentDao = studentDao;
        this.clazzDao = clazzDao;
        this.invoiceDao = invoiceDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> findAll() {
        return enrollmentDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Enrollment> findById(Long id) {
        return enrollmentDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> findByStudentId(Long studentId) {
        return enrollmentDao.findByStudentId(studentId);
    }

    @Override
    @Transactional
    public Enrollment enrollStudent(Long studentId, Long classId) {
        Student student = studentDao.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Học viên với ID: " + studentId));
        Clazz clazz = clazzDao.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Lớp học với ID: " + classId));

        if (!"ACTIVE".equals(student.getStatus().name())) {
            throw new IllegalArgumentException("Học viên không trong trạng thái ACTIVE!");
        }

        // Kiểm tra đã đăng ký
        if (enrollmentDao.existsByStudentIdAndClazzId(studentId, classId)) {
            throw new IllegalArgumentException("Học viên đã đăng ký lớp học này!");
        }

        // Kiểm tra sĩ số
        int currentSize = enrollmentDao.countByClazzId(classId);
        if (currentSize >= clazz.getMaxStudent()) {
            throw new IllegalArgumentException("Lớp học đã đủ sĩ số (" + currentSize + "/" + clazz.getMaxStudent() + ").");
        }

        // Tạo Ghi danh mới, status = PENDING (Chưa nộp tiền)
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(student);
        newEnrollment.setClazz(clazz);
        newEnrollment.setEnrollmentDate(LocalDateTime.now());
        newEnrollment.setStatus("PENDING");
        
        Enrollment savedEnrollment = enrollmentDao.save(newEnrollment);

        // Tự động sinh Hóa đơn (Invoice), status = UNPAID
        Invoice invoice = new Invoice();
        invoice.setStudent(student);
        invoice.setEnrollment(savedEnrollment);
        invoice.setTotalAmount(clazz.getCourse().getFee());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setStatus("UNPAID");
        
        invoiceDao.save(invoice);

        return savedEnrollment;
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentDao.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Ghi danh!"));
        enrollment.setStatus("CANCELLED");
        enrollmentDao.save(enrollment);
    }

    @Override
    @Transactional
    public Enrollment save(Enrollment enrollment) {
        return enrollmentDao.save(enrollment);
    }
}
