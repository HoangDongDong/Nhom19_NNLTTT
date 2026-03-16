package com.edulanguage.UI.panels;

import com.edulanguage.dao.ClazzDao;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Result;
import com.edulanguage.entity.Student;
import com.edulanguage.repository.EnrollmentRepository;
import com.edulanguage.service.ResultService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel Nhập điểm cho giáo viên (Desktop).
 */
public class ResultPanel extends JPanel {

    private final ConfigurableApplicationContext context;
    private final Long teacherId;
    private final JComboBox<ClazzItem> classCombo;
    private final JPanel studentListPanel;
    private final Map<Long, JTextField> scoreFieldMap = new HashMap<>();
    private final Map<Long, JComboBox<String>> gradeComboMap = new HashMap<>();
    private final Map<Long, JTextField> commentFieldMap = new HashMap<>();

    private static final String[] GRADES = {"", "Xuất sắc", "Giỏi", "Khá", "Trung bình", "Không đạt"};

    public ResultPanel(ConfigurableApplicationContext context, Long teacherId) {
        this.context = context;
        this.teacherId = teacherId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Nhập Điểm Cuối Kỳ", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Chọn lớp:"));
        classCombo = new JComboBox<>();
        classCombo.setPreferredSize(new Dimension(280, 28));
        filterPanel.add(classCombo);

        JButton btnLoad = new JButton("Tải danh sách");
        btnLoad.addActionListener(e -> loadEnrollments());
        filterPanel.add(btnLoad);

        add(filterPanel, BorderLayout.NORTH);

        studentListPanel = new JPanel();
        studentListPanel.setLayout(new BoxLayout(studentListPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(studentListPanel), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Lưu Kết Quả");
        btnSubmit.addActionListener(e -> submitResults());
        south.add(btnSubmit);
        add(south, BorderLayout.SOUTH);

        loadClasses();
    }

    private void loadClasses() {
        classCombo.removeAllItems();
        if (teacherId == null) return;
        ClazzDao clazzDao = context.getBean(ClazzDao.class);
        List<Clazz> classes = clazzDao.findByTeacherId(teacherId);
        classes.stream()
                .forEach(c -> classCombo.addItem(new ClazzItem(c)));
    }

    private void loadEnrollments() {
        ClazzItem selected = (ClazzItem) classCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        studentListPanel.removeAll();
        scoreFieldMap.clear();
        gradeComboMap.clear();
        commentFieldMap.clear();

        EnrollmentRepository enrollmentRepo = context.getBean(EnrollmentRepository.class);
        ResultService resultService = context.getBean(ResultService.class);
        List<Enrollment> enrollments = enrollmentRepo.findByClazzId(selected.clazz.getId());
        List<Result> existingResults = resultService.findByClazzId(selected.clazz.getId());
        Map<Long, Result> existingMap = new HashMap<>();
        existingResults.stream()
                .filter(r -> r.getStudent() != null && r.getStudent().getId() != null)
                .forEach(r -> existingMap.put(r.getStudent().getId(), r));

        JPanel header = new JPanel(new GridLayout(1, 4, 5, 5));
        header.add(new JLabel("Học viên", SwingConstants.LEFT));
        header.add(new JLabel("Điểm (0-10)", SwingConstants.LEFT));
        header.add(new JLabel("Xếp loại", SwingConstants.LEFT));
        header.add(new JLabel("Nhận xét", SwingConstants.LEFT));
        studentListPanel.add(header);

        enrollments.stream()
                .map(Enrollment::getStudent)
                .filter(s -> s != null && s.getId() != null)
                .forEach(s -> {
                    Result res = existingMap.get(s.getId());
                    String scoreStr = res != null && res.getScore() != null ? res.getScore().toString() : "";
                    String grade = res != null && res.getGrade() != null ? res.getGrade() : "";
                    String comment = res != null && res.getComment() != null ? res.getComment() : "";

                    JTextField scoreField = new JTextField(scoreStr, 6);
                    JComboBox<String> gradeCombo = new JComboBox<>(GRADES);
                    gradeCombo.setSelectedItem(grade.isEmpty() ? "" : grade);
                    JTextField commentField = new JTextField(comment, 25);

                    scoreFieldMap.put(s.getId(), scoreField);
                    gradeComboMap.put(s.getId(), gradeCombo);
                    commentFieldMap.put(s.getId(), commentField);

                    JPanel row = new JPanel(new GridLayout(1, 4, 5, 5));
                    row.add(new JLabel(s.getFullName()));
                    row.add(scoreField);
                    row.add(gradeCombo);
                    row.add(commentField);
                    studentListPanel.add(row);
                });

        studentListPanel.revalidate();
        studentListPanel.repaint();

        if (enrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lớp này chưa có học viên ghi danh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void submitResults() {
        ClazzItem selected = (ClazzItem) classCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<Long, ResultService.ResultRecord> studentData = new HashMap<>();
        for (Long studentId : scoreFieldMap.keySet()) {
            String scoreStr = scoreFieldMap.get(studentId).getText().trim();
            if (scoreStr.isEmpty()) continue;

            try {
                BigDecimal score = new BigDecimal(scoreStr);
                if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0) {
                    JOptionPane.showMessageDialog(this, "Điểm phải từ 0 đến 10.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String grade = (String) gradeComboMap.get(studentId).getSelectedItem();
                String comment = commentFieldMap.get(studentId).getText();
                studentData.put(studentId, new ResultService.ResultRecord(
                        score,
                        grade != null && !grade.isEmpty() ? grade : null,
                        comment != null && !comment.isEmpty() ? comment : null
                ));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm không hợp lệ: " + scoreStr, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (studentData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu hoặc chưa nhập điểm. Vui lòng nhấn 'Tải danh sách' và nhập điểm.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            context.getBean(ResultService.class).submitClassResults(selected.clazz.getId(), studentData);
            JOptionPane.showMessageDialog(this, "Nhập điểm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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
