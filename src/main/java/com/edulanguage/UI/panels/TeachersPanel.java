package com.edulanguage.UI.panels;

import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Teacher;
import com.edulanguage.entity.UserAccount;
import com.edulanguage.entity.enums.Role;
import com.edulanguage.service.EnrollmentService;
import com.edulanguage.service.TeacherService;
import com.edulanguage.service.UserAccountService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel danh sách giáo viên.
 * - Với STUDENT: chỉ hiển thị giáo viên dạy các lớp mà sinh viên đó đã ghi danh.
 * - Với role khác: hiển thị toàn bộ giáo viên.
 */
public class TeachersPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable table;

    public TeachersPanel(ConfigurableApplicationContext context, String username, String role) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Giáo viên", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Họ tên", "Số điện thoại", "Email", "Chuyên môn", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData(context, username, role);

        // Nút: xem các lớp giáo viên đang dạy (cho ADMIN/STAFF)
        boolean canViewClasses = role != null && (role.contains("ADMIN") || role.contains("STAFF"));
        if (canViewClasses) {
            JButton btnViewClasses = new JButton("Lớp đang dạy");
            btnViewClasses.addActionListener(e -> showClassesOfSelectedTeacher(context));
            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            south.add(btnViewClasses);
            add(south, BorderLayout.SOUTH);
        }
    }

    private void loadData(ConfigurableApplicationContext context, String username, String roleString) {
        tableModel.setRowCount(0);
        try {
            UserAccountService userAccountService = context.getBean(UserAccountService.class);
            EnrollmentService enrollmentService = context.getBean(EnrollmentService.class);
            TeacherService teacherService = context.getBean(TeacherService.class);

            boolean isStudent = roleString != null && roleString.contains("STUDENT");

            if (isStudent) {
                UserAccount account = userAccountService.findByUsername(username)
                        .orElse(null);
                if (account == null || account.getRelatedId() == null || account.getRole() != Role.STUDENT) {
                    return;
                }
                Long studentId = account.getRelatedId();
                List<Enrollment> enrollments = enrollmentService.findByStudentId(studentId);
                Set<Long> addedIds = new LinkedHashSet<>();
                enrollments.stream()
                        .map(Enrollment::getClazz)
                        .filter(clazz -> clazz != null && clazz.getTeacher() != null && clazz.getTeacher().getId() != null)
                        .map(Clazz::getTeacher)
                        .filter(t -> addedIds.add(t.getId()))
                        .forEach(t -> tableModel.addRow(new Object[]{
                                t.getId(),
                                t.getFullName(),
                                t.getPhone(),
                                t.getEmail(),
                                t.getSpecialty(),
                                t.getStatus() != null ? t.getStatus().name() : ""
                        }));
            } else {
                List<Teacher> teachers = teacherService.findAll();
                teachers.stream()
                        .forEach(t -> tableModel.addRow(new Object[]{
                                t.getId(),
                                t.getFullName(),
                                t.getPhone(),
                                t.getEmail(),
                                t.getSpecialty(),
                                t.getStatus() != null ? t.getStatus().name() : ""
                        }));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải danh sách giáo viên: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Hiển thị danh sách lớp mà giáo viên đang dạy — dùng cho ADMIN/STAFF. */
    private void showClassesOfSelectedTeacher(ConfigurableApplicationContext context) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giáo viên.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long teacherId = (Long) tableModel.getValueAt(row, 0);
        String teacherName = (String) tableModel.getValueAt(row, 1);

        com.edulanguage.dao.ClazzDao clazzDao = context.getBean(com.edulanguage.dao.ClazzDao.class);
        java.util.List<Clazz> classes = clazzDao.findByTeacherId(teacherId);
        if (classes == null || classes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giáo viên hiện chưa dạy lớp nào.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] cols = {"ID lớp", "Tên lớp", "Khóa học", "Phòng", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        classes.stream()
                .forEach(c -> {
                    String courseName = c.getCourse() != null ? c.getCourse().getCourseName() : "";
                    String roomName = c.getRoom() != null ? c.getRoom().getRoomName() : "";
                    model.addRow(new Object[]{
                            c.getId(),
                            c.getClassName(),
                            courseName,
                            roomName,
                            c.getStatus() != null ? c.getStatus().name() : ""
                    });
                });

        JTable tbl = new JTable(model);
        tbl.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(this, scroll,
                "Lớp học của giáo viên: " + teacherName,
                JOptionPane.PLAIN_MESSAGE);
    }
}
