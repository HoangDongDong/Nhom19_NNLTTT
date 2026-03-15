package com.edulanguage.UI.panels;

import com.edulanguage.dao.ClazzDao;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Student;
import com.edulanguage.repository.EnrollmentRepository;
import com.edulanguage.service.AttendanceService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel Điểm danh cho giáo viên (Desktop).
 */
public class AttendancePanel extends JPanel {

    private final ConfigurableApplicationContext context;
    private final Long teacherId;
    private final JComboBox<ClazzItem> classCombo;
    private final JSpinner dateSpinner;
    private final JPanel studentListPanel;
    private final Map<Long, JComboBox<String>> statusComboMap = new HashMap<>();
    private final Map<Long, JTextField> noteFieldMap = new HashMap<>();

    public AttendancePanel(ConfigurableApplicationContext context, Long teacherId) {
        this.context = context;
        this.teacherId = teacherId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Điểm danh Học viên", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Chọn lớp:"));
        classCombo = new JComboBox<>();
        classCombo.setPreferredSize(new Dimension(280, 28));
        filterPanel.add(classCombo);

        filterPanel.add(new JLabel("Ngày:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(editor);
        filterPanel.add(dateSpinner);

        JButton btnLoad = new JButton("Lấy danh sách");
        btnLoad.addActionListener(e -> loadEnrollments());
        filterPanel.add(btnLoad);

        add(filterPanel, BorderLayout.NORTH);

        studentListPanel = new JPanel();
        studentListPanel.setLayout(new BoxLayout(studentListPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(studentListPanel), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Lưu Điểm Danh");
        btnSubmit.addActionListener(e -> submitAttendance());
        south.add(btnSubmit);
        add(south, BorderLayout.SOUTH);

        loadClasses();
    }

    private void loadClasses() {
        classCombo.removeAllItems();
        if (teacherId == null) return;
        ClazzDao clazzDao = context.getBean(ClazzDao.class);
        List<Clazz> classes = clazzDao.findByTeacherId(teacherId);
        for (Clazz c : classes) {
            classCombo.addItem(new ClazzItem(c));
        }
    }

    private void loadEnrollments() {
        ClazzItem selected = (ClazzItem) classCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        studentListPanel.removeAll();
        statusComboMap.clear();
        noteFieldMap.clear();

        EnrollmentRepository enrollmentRepo = context.getBean(EnrollmentRepository.class);
        AttendanceService attendanceService = context.getBean(AttendanceService.class);
        List<Enrollment> enrollments = enrollmentRepo.findByClazzId(selected.clazz.getId());

        java.util.Date d = (java.util.Date) dateSpinner.getValue();
        LocalDate date = new java.sql.Date(d.getTime()).toLocalDate();
        List<com.edulanguage.entity.Attendance> existing = attendanceService.findByClazzIdAndDate(selected.clazz.getId(), date);
        Map<Long, com.edulanguage.entity.Attendance> existingMap = new HashMap<>();
        for (com.edulanguage.entity.Attendance a : existing) {
            existingMap.put(a.getStudent().getId(), a);
        }

        JPanel header = new JPanel(new GridLayout(1, 5, 5, 5));
        header.add(new JLabel("ID", SwingConstants.LEFT));
        header.add(new JLabel("Học viên", SwingConstants.LEFT));
        header.add(new JLabel("Trạng thái", SwingConstants.LEFT));
        header.add(new JLabel("Ghi chú", SwingConstants.LEFT));
        header.add(new JLabel(""));
        studentListPanel.add(header);

        for (Enrollment en : enrollments) {
            Student s = en.getStudent();
            com.edulanguage.entity.Attendance att = existingMap.get(s.getId());
            String statusStr = att != null ? att.getStatus().name() : "PRESENT";
            String note = att != null && att.getNote() != null ? att.getNote() : "";

            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"PRESENT", "ABSENT", "LATE", "EXCUSED"});
            statusCombo.setSelectedItem(statusStr);
            JTextField noteField = new JTextField(note, 25);

            statusComboMap.put(s.getId(), statusCombo);
            noteFieldMap.put(s.getId(), noteField);

            JPanel row = new JPanel(new GridLayout(1, 5, 5, 5));
            row.add(new JLabel(String.valueOf(s.getId())));
            row.add(new JLabel(s.getFullName()));
            row.add(statusCombo);
            row.add(noteField);
            row.add(new JLabel(""));
            studentListPanel.add(row);
        }

        studentListPanel.revalidate();
        studentListPanel.repaint();

        if (enrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lớp này chưa có học viên ghi danh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void submitAttendance() {
        ClazzItem selected = (ClazzItem) classCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.util.Date d = (java.util.Date) dateSpinner.getValue();
        LocalDate date = new java.sql.Date(d.getTime()).toLocalDate();

        Map<Long, String> statuses = new HashMap<>();
        Map<Long, String> notes = new HashMap<>();
        for (Long studentId : statusComboMap.keySet()) {
            statuses.put(studentId, (String) statusComboMap.get(studentId).getSelectedItem());
            notes.put(studentId, noteFieldMap.get(studentId).getText());
        }

        if (statuses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu. Vui lòng nhấn 'Lấy danh sách' trước.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Clazz clazz = new Clazz();
            clazz.setId(selected.clazz.getId());
            context.getBean(AttendanceService.class).submitAttendance(clazz, date, statuses, notes);
            JOptionPane.showMessageDialog(this, "Điểm danh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadEnrollments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private record ClazzItem(Clazz clazz) {
        @Override
        public String toString() {
            return clazz.getClassName() + " - " + (clazz.getCourse() != null ? clazz.getCourse().getCourseName() : "");
        }
    }
}
