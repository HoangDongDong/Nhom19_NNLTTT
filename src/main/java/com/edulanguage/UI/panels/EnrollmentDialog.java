package com.edulanguage.UI.panels;

import com.edulanguage.dao.ClazzDao;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Student;
import com.edulanguage.service.EnrollmentService;
import com.edulanguage.entity.enums.Status;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EnrollmentDialog extends JDialog {

    private final Student student;
    private final EnrollmentService enrollmentService;
    private final ClazzDao clazzDao;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EnrollmentDialog(Frame owner, Student student, EnrollmentService enrollmentService, ClazzDao clazzDao) {
        super(owner, "Ghi danh học viên", true);
        this.student = student;
        this.enrollmentService = enrollmentService;
        this.clazzDao = clazzDao;

        initUI();
        pack();
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        infoPanel.add(new JLabel("Mã học viên:"));
        infoPanel.add(new JLabel(String.valueOf(student.getId())));
        infoPanel.add(new JLabel("Họ tên:"));
        infoPanel.add(new JLabel(student.getFullName()));
        contentPane.add(infoPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Clazz> availableClasses = clazzDao.findByStatus(Status.ACTIVE); // Có thể filter status khác (vd: OPEN)

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Chọn Lớp học:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JComboBox<ClazzValueItem> cbClasses = new JComboBox<>();
        for (Clazz clazz : availableClasses) {
            String label = String.format("%s - %s (Học phí: %s)", 
                    clazz.getClassName(), 
                    clazz.getCourse().getCourseName(), 
                    clazz.getCourse().getFee());
            cbClasses.addItem(new ClazzValueItem(clazz.getId(), label));
        }
        formPanel.add(cbClasses, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);

        // Filter out button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEnroll = new JButton("Xác nhận Ghi danh");
        JButton btnCancel = new JButton("Đóng");

        btnEnroll.addActionListener(e -> {
            ClazzValueItem selectedItem = (ClazzValueItem) cbClasses.getSelectedItem();
            if (selectedItem == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                enrollmentService.enrollStudent(student.getId(), selectedItem.id);
                JOptionPane.showMessageDialog(this, "Ghi danh thành công! Trạng thái: Chờ thanh toán.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi ghi danh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnEnroll);
        buttonPanel.add(btnCancel);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
    }

    private static class ClazzValueItem {
        Long id;
        String label;

        public ClazzValueItem(Long id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
