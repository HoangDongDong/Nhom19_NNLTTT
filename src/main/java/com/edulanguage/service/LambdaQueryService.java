package com.edulanguage.service;

import com.edulanguage.entity.Attendance;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Course;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Invoice;
import com.edulanguage.entity.Payment;
import com.edulanguage.entity.Student;
import com.edulanguage.entity.Teacher;
import com.edulanguage.entity.enums.AttendanceStatus;
import com.edulanguage.entity.enums.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Lớp service tập hợp sẵn một số ví dụ truy vấn Lambda (Java Stream)
 * trên các entity của hệ thống trung tâm ngoại ngữ.
 *
 * Bạn có thể @Autowired LambdaQueryService và gọi trực tiếp các phương thức này.
 */
@Service
public class LambdaQueryService {

    /**
     * 1. Lọc danh sách học viên đang ACTIVE và có email.
     */
    public List<Student> findActiveStudentsWithEmail(List<Student> students) {
        return students.stream()
                .filter(s -> s.getStatus() == Status.ACTIVE)
                .filter(s -> Objects.nonNull(s.getEmail()) && !s.getEmail().isBlank())
                .toList();
    }

    /**
     * 2. Lấy danh sách khóa học level = "Beginner", sắp xếp tăng dần theo học phí.
     */
    public List<Course> findBeginnerCoursesSortedByFee(List<Course> courses) {
        return courses.stream()
                .filter(c -> "Beginner".equalsIgnoreCase(c.getLevel()))
                .sorted(Comparator.comparing(
                        Course::getFee,
                        Comparator.nullsLast(BigDecimal::compareTo)
                ))
                .toList();
    }

    /**
     * 3. Đếm số lượng buổi điểm danh PRESENT của một học viên trong một lớp.
     */
    public long countPresentAttendanceOfStudentInClass(
            List<Attendance> attendances,
            Long studentId,
            Long classId
    ) {
        return attendances.stream()
                .filter(a -> a.getStudent() != null && a.getStudent().getId().equals(studentId))
                .filter(a -> a.getClazz() != null && a.getClazz().getId().equals(classId))
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();
    }

    /**
     * 4. Tính tổng số tiền đã thanh toán theo từng học viên.
     *    Trả về Map<studentId, totalAmount>.
     */
    public Map<Long, BigDecimal> calculateTotalPaymentByStudent(List<Payment> payments) {
        return payments.stream()
                .filter(p -> p.getStudent() != null && p.getStudent().getId() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getStudent().getId(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Payment::getAmount,
                                BigDecimal::add
                        )
                ));
    }

    /**
     * 5. Lấy danh sách lớp học của một giáo viên còn đang ACTIVE.
     */
    public List<Clazz> findActiveClassesOfTeacher(List<Clazz> classes, Long teacherId) {
        return classes.stream()
                .filter(c -> c.getTeacher() != null && c.getTeacher().getId().equals(teacherId))
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .toList();
    }

    /**
     * 6. Kiểm tra xem có lớp nào đã đầy (số enrollment >= maxStudent) hay không.
     */
    public boolean hasAnyFullClass(List<Clazz> classes) {
        return classes.stream()
                .anyMatch(clazz -> {
                    int enrolled = clazz.getEnrollments() == null ? 0 : clazz.getEnrollments().size();
                    Integer max = clazz.getMaxStudent();
                    return max != null && enrolled >= max;
                });
    }

    /**
     * 7. Lọc các hóa đơn đã được thanh toán (status = "Paid") trong tháng hiện tại.
     */
    public List<Invoice> findPaidInvoicesInCurrentMonth(List<Invoice> invoices) {
        YearMonth currentMonth = YearMonth.now();
        return invoices.stream()
                .filter(inv -> inv.getStatus() != null && "Paid".equalsIgnoreCase(inv.getStatus()))
                .filter(inv -> inv.getIssueDate() != null)
                .filter(inv -> YearMonth.from(inv.getIssueDate()).equals(currentMonth))
                .toList();
    }

    /**
     * Hàm main để chạy thử các truy vấn lambda với dữ liệu mẫu.
     * Chạy: Run As -> Java Application (không cần Spring Boot).
     */
    public static void main(String[] args) {
        LambdaQueryService service = new LambdaQueryService();

        // --- 1. Dữ liệu mẫu Student ---
        Student s1 = new Student();
        s1.setId(1L);
        s1.setFullName("Nguyễn Văn A");
        s1.setStatus(Status.ACTIVE);
        s1.setEmail("a@email.com");
        Student s2 = new Student();
        s2.setId(2L);
        s2.setFullName("Trần Thị B");
        s2.setStatus(Status.ACTIVE);
        s2.setEmail("b@email.com");
        Student s3 = new Student();
        s3.setId(3L);
        s3.setFullName("Lê Văn C");
        s3.setStatus(Status.INACTIVE);
        s3.setEmail(null);
        List<Student> students = List.of(s1, s2, s3);

        System.out.println("=== 1. Học viên ACTIVE có email ===");
        List<Student> activeWithEmail = service.findActiveStudentsWithEmail(students);
        activeWithEmail.forEach(s -> System.out.println("  - " + s.getFullName() + " | " + s.getEmail()));
        System.out.println("  Tổng: " + activeWithEmail.size() + " học viên\n");

        // --- 2. Dữ liệu mẫu Course ---
        Course c1 = new Course();
        c1.setId(1L);
        c1.setCourseName("IELTS Foundation");
        c1.setLevel("Beginner");
        c1.setFee(new BigDecimal("3000000"));
        Course c2 = new Course();
        c2.setId(2L);
        c2.setCourseName("TOEIC Starter");
        c2.setLevel("Beginner");
        c2.setFee(new BigDecimal("2500000"));
        Course c3 = new Course();
        c3.setId(3L);
        c3.setCourseName("Giao tiếp nâng cao");
        c3.setLevel("Advanced");
        c3.setFee(new BigDecimal("4000000"));
        List<Course> courses = List.of(c3, c1, c2); // lộn xộn

        System.out.println("=== 2. Khóa Beginner sắp xếp theo học phí ===");
        List<Course> beginnerSorted = service.findBeginnerCoursesSortedByFee(courses);
        beginnerSorted.forEach(c -> System.out.println("  - " + c.getCourseName() + " | " + c.getFee()));
        System.out.println();

        // --- 3. Attendance (cần Student + Clazz) ---
        Teacher t1 = new Teacher();
        t1.setId(10L);
        Clazz clazz1 = new Clazz();
        clazz1.setId(100L);
        clazz1.setTeacher(t1);
        Attendance a1 = new Attendance();
        a1.setId(201L);
        a1.setStudent(s1);
        a1.setClazz(clazz1);
        a1.setStatus(AttendanceStatus.PRESENT);
        Attendance a2 = new Attendance();
        a2.setId(202L);
        a2.setStudent(s1);
        a2.setClazz(clazz1);
        a2.setStatus(AttendanceStatus.PRESENT);
        Attendance a3 = new Attendance();
        a3.setId(203L);
        a3.setStudent(s1);
        a3.setClazz(clazz1);
        a3.setStatus(AttendanceStatus.ABSENT);
        List<Attendance> attendances = List.of(a1, a2, a3);

        long presentCount = service.countPresentAttendanceOfStudentInClass(attendances, 1L, 100L);
        System.out.println("=== 3. Số buổi PRESENT của học viên 1 trong lớp 100 ===");
        System.out.println("  Kết quả: " + presentCount + " buổi\n");

        // --- 4. Payment ---
        Enrollment e1 = new Enrollment();
        e1.setId(50L);
        e1.setStudent(s1);
        Payment p1 = new Payment();
        p1.setId(301L);
        p1.setStudent(s1);
        p1.setEnrollment(e1);
        p1.setAmount(new BigDecimal("1500000"));
        Payment p2 = new Payment();
        p2.setId(302L);
        p2.setStudent(s1);
        p2.setEnrollment(e1);
        p2.setAmount(new BigDecimal("1500000"));
        Payment p3 = new Payment();
        p3.setId(303L);
        p3.setStudent(s2);
        p3.setEnrollment(e1);
        p3.setAmount(new BigDecimal("500000"));
        List<Payment> payments = List.of(p1, p2, p3);

        System.out.println("=== 4. Tổng thanh toán theo học viên ===");
        Map<Long, BigDecimal> totalByStudent = service.calculateTotalPaymentByStudent(payments);
        totalByStudent.forEach((id, total) -> System.out.println("  Student " + id + " -> " + total));
        System.out.println();

        // --- 5. Lớp của giáo viên ACTIVE ---
        Clazz clazz2 = new Clazz();
        clazz2.setId(101L);
        clazz2.setTeacher(t1);
        clazz2.setStatus(Status.ACTIVE);
        Clazz clazz3 = new Clazz();
        clazz3.setId(102L);
        clazz3.setTeacher(t1);
        clazz3.setStatus(Status.INACTIVE);
        List<Clazz> classList = List.of(clazz2, clazz3);

        System.out.println("=== 5. Lớp ACTIVE của giáo viên 10 ===");
        List<Clazz> activeClasses = service.findActiveClassesOfTeacher(classList, 10L);
        activeClasses.forEach(cl -> System.out.println("  - Lớp ID: " + cl.getId()));
        System.out.println();

        // --- 6. Lớp đầy ---
        Enrollment e2 = new Enrollment();
        e2.setId(51L);
        Enrollment e3 = new Enrollment();
        e3.setId(52L);
        Clazz fullClazz = new Clazz();
        fullClazz.setId(103L);
        fullClazz.setMaxStudent(2);
        fullClazz.setEnrollments(List.of(e2, e3));
        List<Clazz> classesForFull = List.of(clazz1, fullClazz);

        System.out.println("=== 6. Có lớp nào đã đầy? ===");
        System.out.println("  " + service.hasAnyFullClass(classesForFull) + "\n");

        // --- 7. Hóa đơn Paid tháng hiện tại ---
        Invoice inv1 = new Invoice();
        inv1.setId(401L);
        inv1.setStudent(s1);
        inv1.setTotalAmount(new BigDecimal("3000000"));
        inv1.setStatus("Paid");
        inv1.setIssueDate(LocalDateTime.now());
        Invoice inv2 = new Invoice();
        inv2.setId(402L);
        inv2.setStudent(s2);
        inv2.setTotalAmount(new BigDecimal("2000000"));
        inv2.setStatus("Pending");
        inv2.setIssueDate(LocalDateTime.now());
        List<Invoice> invoices = List.of(inv1, inv2);

        System.out.println("=== 7. Hóa đơn Paid trong tháng hiện tại ===");
        List<Invoice> paidThisMonth = service.findPaidInvoicesInCurrentMonth(invoices);
        paidThisMonth.forEach(inv -> System.out.println("  - Student " + inv.getStudent().getId() + " | " + inv.getTotalAmount()));
        System.out.println("  Tổng: " + paidThisMonth.size() + " hóa đơn");

        System.out.println("\n--- Kết thúc chạy thử LambdaQueryService ---");
    }
}
