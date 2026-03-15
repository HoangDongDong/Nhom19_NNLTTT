package com.edulanguage.UI.panels;

import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Course;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.UserAccount;
import com.edulanguage.entity.enums.Role;
import com.edulanguage.service.CourseService;
import com.edulanguage.service.EnrollmentService;
import com.edulanguage.service.UserAccountService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel danh sách khóa học.
 * - Với STUDENT: chỉ hiển thị các khóa học mà sinh viên đó đang/đã học (theo ghi danh).
 * - Với role khác: hiển thị toàn bộ khóa học.
 */
public class CoursesPanel extends JPanel {

    private final DefaultTableModel tableModel;

    private final Long currentTeacherId;

    public CoursesPanel(ConfigurableApplicationContext context, String username, String role, Long teacherId) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.currentTeacherId = teacherId;

        JLabel title = new JLabel("Khóa học", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Tên khóa học", "Trình độ", "Số giờ", "Học phí", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData(context, username, role);
    }

    private void loadData(ConfigurableApplicationContext context, String username, String roleString) {
        tableModel.setRowCount(0);
        try {
            UserAccountService userAccountService = context.getBean(UserAccountService.class);
            EnrollmentService enrollmentService = context.getBean(EnrollmentService.class);
            CourseService courseService = context.getBean(CourseService.class);
            com.edulanguage.dao.ClazzDao clazzDao = context.getBean(com.edulanguage.dao.ClazzDao.class);

            boolean isStudent = roleString != null && roleString.contains("STUDENT");
            boolean isTeacher = roleString != null && roleString.contains("TEACHER") && currentTeacherId != null;

            if (isStudent) {
                UserAccount account = userAccountService.findByUsername(username)
                        .orElse(null);
                if (account == null || account.getRelatedId() == null || account.getRole() != Role.STUDENT) {
                    return;
                }
                Long studentId = account.getRelatedId();
                List<Enrollment> enrollments = enrollmentService.findByStudentId(studentId);
                Set<Long> addedIds = new LinkedHashSet<>();
                for (Enrollment e : enrollments) {
                    Clazz clazz = e.getClazz();
                    if (clazz == null || clazz.getCourse() == null || clazz.getCourse().getId() == null) continue;
                    Course c = clazz.getCourse();
                    if (!addedIds.add(c.getId())) continue;
                    addCourseRow(c);
                }
            } else if (isTeacher) {
                java.util.List<com.edulanguage.entity.Clazz> classes =
                        clazzDao.findByTeacherId(currentTeacherId);
                Set<Long> addedIds = new LinkedHashSet<>();
                for (com.edulanguage.entity.Clazz clazz : classes) {
                    if (clazz.getCourse() == null || clazz.getCourse().getId() == null) continue;
                    Course c = clazz.getCourse();
                    if (!addedIds.add(c.getId())) continue;
                    addCourseRow(c);
                }
            } else {
                List<Course> courses = courseService.findAll();
                for (Course c : courses) {
                    addCourseRow(c);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải danh sách khóa học: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCourseRow(Course c) {
        BigDecimal fee = c.getFee();
        tableModel.addRow(new Object[]{
                c.getId(),
                c.getCourseName(),
                c.getLevel(),
                c.getDuration(),
                fee != null ? fee : "",
                c.getStatus() != null ? c.getStatus().name() : ""
        });
    }
}
