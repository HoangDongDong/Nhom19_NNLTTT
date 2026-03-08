package com.edulanguage.runner;

import com.edulanguage.EduLanguageApplication;
import com.edulanguage.entity.*;
import com.edulanguage.repository.*;
import com.edulanguage.service.LambdaQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lớp test: đọc dữ liệu từ SQL Server, gọi LambdaQueryService và in kết quả.
 * Chạy hàm main() của lớp này để test (Run As -> Java Application).
 */
@Component
public class LambdaQueryTestRunner {

    private final LambdaQueryService lambdaQueryService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ClazzRepository clazzRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public LambdaQueryTestRunner(LambdaQueryService lambdaQueryService,
                                  StudentRepository studentRepository,
                                  CourseRepository courseRepository,
                                  ClazzRepository clazzRepository,
                                  AttendanceRepository attendanceRepository,
                                  PaymentRepository paymentRepository,
                                  InvoiceRepository invoiceRepository) {
        this.lambdaQueryService = lambdaQueryService;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.clazzRepository = clazzRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Test tất cả các truy vấn lambda với dữ liệu lấy từ DB.
     */
    @Transactional(readOnly = true)
    public void runTest() {
        System.out.println("\n========== TEST LAMBDA QUERY (dữ liệu từ SQL Server) ==========\n");

        List<Student> students = studentRepository.findAll();
        List<Course> courses = courseRepository.findAll();
        List<Clazz> classes = clazzRepository.findAll();
        List<Attendance> attendances = attendanceRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();
        List<Invoice> invoices = invoiceRepository.findAll();

        // 1. Học viên ACTIVE có email
        System.out.println("=== 1. Học viên ACTIVE có email ===");
        List<Student> activeWithEmail = lambdaQueryService.findActiveStudentsWithEmail(students);
        activeWithEmail.forEach(s -> System.out.println("  - " + s.getFullName() + " | " + s.getEmail()));
        System.out.println("  Tổng: " + activeWithEmail.size() + " học viên\n");

        // 2. Khóa Beginner sắp theo học phí
        System.out.println("=== 2. Khóa Beginner sắp xếp theo học phí ===");
        List<Course> beginnerSorted = lambdaQueryService.findBeginnerCoursesSortedByFee(courses);
        beginnerSorted.forEach(c -> System.out.println("  - " + c.getCourseName() + " | " + c.getFee()));
        System.out.println("  Tổng: " + beginnerSorted.size() + " khóa\n");

        // 3. Đếm PRESENT của học viên trong lớp (lấy id đầu tiên có từ DB)
        if (!students.isEmpty() && !classes.isEmpty() && !attendances.isEmpty()) {
            Long studentId = students.get(0).getId();
            Long classId = classes.get(0).getId();
            long presentCount = lambdaQueryService.countPresentAttendanceOfStudentInClass(attendances, studentId, classId);
            System.out.println("=== 3. Số buổi PRESENT của học viên " + studentId + " trong lớp " + classId + " ===");
            System.out.println("  Kết quả: " + presentCount + " buổi\n");
        }

        // 4. Tổng thanh toán theo học viên
        System.out.println("=== 4. Tổng thanh toán theo học viên ===");
        var totalByStudent = lambdaQueryService.calculateTotalPaymentByStudent(payments);
        totalByStudent.forEach((id, total) -> System.out.println("  Student " + id + " -> " + total));
        System.out.println();

        // 5. Lớp ACTIVE của giáo viên (lấy teacher id từ lớp đầu tiên)
        if (!classes.isEmpty() && classes.get(0).getTeacher() != null) {
            Long teacherId = classes.get(0).getTeacher().getId();
            List<Clazz> activeClasses = lambdaQueryService.findActiveClassesOfTeacher(classes, teacherId);
            System.out.println("=== 5. Lớp ACTIVE của giáo viên " + teacherId + " ===");
            activeClasses.forEach(cl -> System.out.println("  - Lớp ID: " + cl.getId() + " | " + cl.getClassName()));
            System.out.println("  Tổng: " + activeClasses.size() + " lớp\n");
        }

        // 6. Có lớp nào đã đầy?
        System.out.println("=== 6. Có lớp nào đã đầy? ===");
        boolean hasFull = lambdaQueryService.hasAnyFullClass(classes);
        System.out.println("  " + hasFull + "\n");

        // 7. Hóa đơn Paid trong tháng hiện tại
        System.out.println("=== 7. Hóa đơn Paid trong tháng hiện tại ===");
        List<Invoice> paidThisMonth = lambdaQueryService.findPaidInvoicesInCurrentMonth(invoices);
        paidThisMonth.forEach(inv -> System.out.println("  - Student " + inv.getStudent().getId() + " | " + inv.getTotalAmount()));
        System.out.println("  Tổng: " + paidThisMonth.size() + " hóa đơn");

        System.out.println("\n========== KẾT THÚC TEST ==========");
    }

    /**
     * Chạy độc lập: khởi động Spring, kết nối DB, gọi runTest(), rồi thoát.
     * Run As -> Java Application trên lớp này.
     */
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication app = new org.springframework.boot.SpringApplication(EduLanguageApplication.class);
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        try (var ctx = app.run(args)) {
            LambdaQueryTestRunner runner = ctx.getBean(LambdaQueryTestRunner.class);
            runner.runTest();
        }
        System.out.println("Thoát chương trình test.");
    }
}
