package com.edulanguage.runner;

import com.edulanguage.EduLanguageApplication;
import com.edulanguage.entity.*;
import com.edulanguage.entity.enums.AttendanceStatus;
import com.edulanguage.entity.enums.Gender;
import com.edulanguage.entity.enums.PaymentMethod;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lớp thêm dữ liệu mẫu vào SQL Server.
 * Chạy hàm main() của lớp này để seed DB (Run As -> Java Application).
 */
@Component
public class SeedDataRunner {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final RoomRepository roomRepository;
    private final ClazzRepository clazzRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final SeedDataClearHelper clearHelper;

    public SeedDataRunner(StudentRepository studentRepository,
                          TeacherRepository teacherRepository,
                          CourseRepository courseRepository,
                          RoomRepository roomRepository,
                          ClazzRepository clazzRepository,
                          EnrollmentRepository enrollmentRepository,
                          AttendanceRepository attendanceRepository,
                          PaymentRepository paymentRepository,
                          InvoiceRepository invoiceRepository,
                          SeedDataClearHelper clearHelper) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.roomRepository = roomRepository;
        this.clazzRepository = clazzRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.clearHelper = clearHelper;
    }

    /**
     * Xóa toàn bộ dữ liệu mẫu (gọi helper để chạy transaction riêng, commit trước khi insert).
     */
    public void clearAll() {
        clearHelper.clearAll();
    }

    /**
     * Thêm dữ liệu mẫu vào database (thứ tự đúng quan hệ FK).
     * Mỗi lần chạy sẽ xóa dữ liệu cũ trước rồi chèn lại.
     */
    @Transactional
    public void seed() {
        System.out.println("========== BẮT ĐẦU SEED DỮ LIỆU ==========");
        clearAll();
        Room room1 = new Room();
        room1.setRoomName("P101");
        room1.setCapacity(20);
        room1.setLocation("Tầng 1");
        room1.setStatus(Status.ACTIVE);
        room1 = roomRepository.save(room1);

        Room room2 = new Room();
        room2.setRoomName("P202");
        room2.setCapacity(15);
        room2.setLocation("Tầng 2");
        room2.setStatus(Status.ACTIVE);
        room2 = roomRepository.save(room2);

        Course course1 = new Course();
        course1.setCourseName("IELTS Foundation");
        course1.setDescription("Khóa nền tảng IELTS");
        course1.setLevel("Beginner");
        course1.setDuration(60);
        course1.setFee(new BigDecimal("3500000"));
        course1.setStatus(Status.ACTIVE);
        course1 = courseRepository.save(course1);

        Course course2 = new Course();
        course2.setCourseName("TOEIC 4 kỹ năng");
        course2.setLevel("Intermediate");
        course2.setDuration(48);
        course2.setFee(new BigDecimal("2800000"));
        course2.setStatus(Status.ACTIVE);
        course2 = courseRepository.save(course2);

        Teacher teacher1 = new Teacher();
        teacher1.setFullName("Nguyễn Thị Mai");
        teacher1.setPhone("0901234567");
        teacher1.setEmail("mai.nguyen@edulanguage.com");
        teacher1.setSpecialty("IELTS, Giao tiếp");
        teacher1.setHireDate(LocalDateTime.now().minusMonths(12));
        teacher1.setStatus(Status.ACTIVE);
        teacher1 = teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFullName("Trần Văn Nam");
        teacher2.setEmail("nam.tran@edulanguage.com");
        teacher2.setSpecialty("TOEIC");
        teacher2.setStatus(Status.ACTIVE);
        teacher2 = teacherRepository.save(teacher2);

        // 2. Clazz (phụ thuộc Course, Teacher, Room)
        Clazz clazz1 = new Clazz();
        clazz1.setClassName("IELTS-A1-2025");
        clazz1.setCourse(course1);
        clazz1.setTeacher(teacher1);
        clazz1.setRoom(room1);
        clazz1.setStartDate(LocalDate.now().minusWeeks(2));
        clazz1.setEndDate(LocalDate.now().plusMonths(3));
        clazz1.setMaxStudent(20);
        clazz1.setStatus(Status.ACTIVE);
        clazz1 = clazzRepository.save(clazz1);

        Clazz clazz2 = new Clazz();
        clazz2.setClassName("TOEIC-B2-2025");
        clazz2.setCourse(course2);
        clazz2.setTeacher(teacher2);
        clazz2.setRoom(room2);
        clazz2.setMaxStudent(15);
        clazz2.setStatus(Status.ACTIVE);
        clazz2 = clazzRepository.save(clazz2);

        // 3. Student
        Student s1 = new Student();
        s1.setFullName("Lê Văn An");
        s1.setDateOfBirth(LocalDate.of(2002, 5, 15));
        s1.setGender(Gender.MALE);
        s1.setPhone("0911111111");
        s1.setEmail("an.le@email.com");
        s1.setAddress("Quận 1, TP.HCM");
        s1.setRegistrationDate(LocalDateTime.now().minusMonths(1));
        s1.setStatus(Status.ACTIVE);
        s1 = studentRepository.save(s1);

        Student s2 = new Student();
        s2.setFullName("Phạm Thị Bình");
        s2.setGender(Gender.FEMALE);
        s2.setEmail("binh.pham@email.com");
        s2.setRegistrationDate(LocalDateTime.now().minusWeeks(2));
        s2.setStatus(Status.ACTIVE);
        s2 = studentRepository.save(s2);

        Student s3 = new Student();
        s3.setFullName("Hoàng Văn Cường");
        s3.setStatus(Status.INACTIVE);
        s3.setEmail("cuong.hoang@email.com");
        s3 = studentRepository.save(s3);

        // 4. Enrollment (Student, Clazz)
        Enrollment enr1 = new Enrollment();
        enr1.setStudent(s1);
        enr1.setClazz(clazz1);
        enr1.setEnrollmentDate(LocalDateTime.now().minusWeeks(2));
        enr1.setStatus("Approved");
        enr1.setResult(null);
        enr1 = enrollmentRepository.save(enr1);

        Enrollment enr2 = new Enrollment();
        enr2.setStudent(s2);
        enr2.setClazz(clazz1);
        enr2.setEnrollmentDate(LocalDateTime.now().minusWeeks(1));
        enr2.setStatus("Approved");
        enr2 = enrollmentRepository.save(enr2);

        Enrollment enr3 = new Enrollment();
        enr3.setStudent(s1);
        enr3.setClazz(clazz2);
        enr3.setStatus("Approved");
        enr3 = enrollmentRepository.save(enr3);

        // 5. Attendance
        for (int i = 0; i < 3; i++) {
            Attendance att = new Attendance();
            att.setStudent(s1);
            att.setClazz(clazz1);
            att.setDate(LocalDate.now().minusWeeks(i));
            att.setStatus(i == 0 ? AttendanceStatus.LATE : AttendanceStatus.PRESENT);
            attendanceRepository.save(att);
        }

        Attendance att2 = new Attendance();
        att2.setStudent(s2);
        att2.setClazz(clazz1);
        att2.setDate(LocalDate.now());
        att2.setStatus(AttendanceStatus.PRESENT);
        attendanceRepository.save(att2);

        // 6. Payment
        Payment pay1 = new Payment();
        pay1.setStudent(s1);
        pay1.setEnrollment(enr1);
        pay1.setAmount(new BigDecimal("1750000"));
        pay1.setPaymentDate(LocalDateTime.now().minusWeeks(2));
        pay1.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        pay1.setStatus("Paid");
        paymentRepository.save(pay1);

        Payment pay2 = new Payment();
        pay2.setStudent(s1);
        pay2.setEnrollment(enr1);
        pay2.setAmount(new BigDecimal("1750000"));
        pay2.setPaymentDate(LocalDateTime.now().minusWeeks(1));
        pay2.setPaymentMethod(PaymentMethod.CASH);
        pay2.setStatus("Paid");
        paymentRepository.save(pay2);

        Payment pay3 = new Payment();
        pay3.setStudent(s2);
        pay3.setEnrollment(enr2);
        pay3.setAmount(new BigDecimal("3500000"));
        pay3.setPaymentMethod(PaymentMethod.MOMO);
        pay3.setStatus("Paid");
        paymentRepository.save(pay3);

        // 7. Invoice
        Invoice inv1 = new Invoice();
        inv1.setStudent(s1);
        inv1.setEnrollment(enr1);
        inv1.setTotalAmount(new BigDecimal("3500000"));
        inv1.setIssueDate(LocalDateTime.now().minusWeeks(2));
        inv1.setStatus("Paid");
        invoiceRepository.save(inv1);

        Invoice inv2 = new Invoice();
        inv2.setStudent(s2);
        inv2.setEnrollment(enr2);
        inv2.setTotalAmount(new BigDecimal("3500000"));
        inv2.setIssueDate(LocalDateTime.now());
        inv2.setStatus("Paid");
        invoiceRepository.save(inv2);

        Invoice inv3 = new Invoice();
        inv3.setStudent(s1);
        inv3.setEnrollment(enr3);
        inv3.setTotalAmount(new BigDecimal("2800000"));
        inv3.setIssueDate(LocalDateTime.now());
        inv3.setStatus("Pending");
        invoiceRepository.save(inv3);

        System.out.println("========== SEED DỮ LIỆU XONG ==========");
        System.out.println("  - Rooms: 2 | Courses: 2 | Teachers: 2");
        System.out.println("  - Classes: 2 | Students: 3 | Enrollments: 3");
        System.out.println("  - Attendances: 4 | Payments: 3 | Invoices: 3");
    }

    /**
     * Chạy độc lập: khởi động Spring, gọi seed(), rồi thoát.
     * Run As -> Java Application trên lớp này.
     */
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication app = new org.springframework.boot.SpringApplication(EduLanguageApplication.class);
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        try (var ctx = app.run(args)) {
            SeedDataRunner runner = ctx.getBean(SeedDataRunner.class);
            runner.seed();
        }
        System.out.println("Thoát chương trình seed.");
    }
}
