package com.edulanguage.UI.panels;

import com.edulanguage.entity.Result;
import com.edulanguage.entity.Student;
import com.edulanguage.entity.enums.Gender;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.PlacementTestService;
import com.edulanguage.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * Panel CRUD Học viên + Test đầu vào (Placement Test) cho giao diện Desktop.
 * Theo pattern BranchesPanel / StaffsPanel.
 */
public class StudentsPanel extends JPanel {

    private final StudentService studentService;
    private final PlacementTestService placementTestService;
    private final com.edulanguage.service.EnrollmentService enrollmentService;
    private final com.edulanguage.dao.ClazzDao clazzDao;
    private final String currentRole;
    private final Long currentTeacherId;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private static final String[] COLUMNS = {
            "ID", "Họ tên", "Ngày sinh", "Giới tính", "SĐT", "Email", "Trạng thái"
    };
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public StudentsPanel(StudentService studentService, PlacementTestService placementTestService,
            com.edulanguage.service.EnrollmentService enrollmentService, com.edulanguage.dao.ClazzDao clazzDao,
            String currentRole, Long currentTeacherId) {
        this.studentService = studentService;
        this.placementTestService = placementTestService;
        this.enrollmentService = enrollmentService;
        this.clazzDao = clazzDao;
        this.currentRole = currentRole;
        this.currentTeacherId = currentTeacherId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Tiêu đề ──
        JLabel title = new JLabel("Học viên", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // ── Bảng dữ liệu ──
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Thanh công cụ ──
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd    = new JButton("Thêm");
        JButton btnEdit   = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnTest   = new JButton("Test đầu vào");
        JButton btnEnroll = new JButton("Ghi danh");
        JButton btnViewClasses = new JButton("Lớp đang học");
        btnAdd.addActionListener(e -> doAdd());
        btnEdit.addActionListener(e -> doEdit());
        btnDelete.addActionListener(e -> doDelete());
        btnTest.addActionListener(e -> doPlacementTest());
        btnEnroll.addActionListener(e -> doEnroll());
        btnViewClasses.addActionListener(e -> showClassesOfSelectedStudent());
        // Giáo viên chỉ xem danh sách, không CRUD
        boolean isTeacher = currentRole != null && currentRole.contains("TEACHER");
        btnAdd.setEnabled(!isTeacher);
        btnEdit.setEnabled(!isTeacher);
        btnDelete.setEnabled(!isTeacher);

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnTest);
        toolbar.add(Box.createHorizontalStrut(5));
        toolbar.add(btnEnroll);
        toolbar.add(Box.createHorizontalStrut(5));
        toolbar.add(btnViewClasses);
        add(toolbar, BorderLayout.SOUTH);

        refreshTable();
    }

    /* ════════════════════════════════════════════
     *  Refresh bảng
     * ════════════════════════════════════════════ */
    private void refreshTable() {
        tableModel.setRowCount(0);

        boolean isTeacher = currentRole != null && currentRole.contains("TEACHER") && currentTeacherId != null;
        if (isTeacher) {
            // Lấy tất cả lớp mà giáo viên này dạy, rồi gom học viên từ các ghi danh
            java.util.List<com.edulanguage.entity.Clazz> classes =
                    clazzDao.findByTeacherId(currentTeacherId);
            java.util.Set<Long> added = new java.util.HashSet<>();
            for (com.edulanguage.entity.Clazz c : classes) {
                if (c.getEnrollments() == null) continue;
                for (com.edulanguage.entity.Enrollment e : c.getEnrollments()) {
                    Student s = e.getStudent();
                    if (s == null || s.getId() == null || !added.add(s.getId())) continue;
                    tableModel.addRow(new Object[]{
                            s.getId(),
                            s.getFullName(),
                            s.getDateOfBirth() != null ? s.getDateOfBirth().format(DATE_FMT) : "",
                            s.getGender() != null ? genderLabel(s.getGender()) : "",
                            s.getPhone() != null ? s.getPhone() : "",
                            s.getEmail() != null ? s.getEmail() : "",
                            s.getStatus() != null ? s.getStatus().name() : ""
                    });
                }
            }
        } else {
            List<Student> list = studentService.findAll();
            for (Student s : list) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getFullName(),
                        s.getDateOfBirth() != null ? s.getDateOfBirth().format(DATE_FMT) : "",
                        s.getGender() != null ? genderLabel(s.getGender()) : "",
                        s.getPhone() != null ? s.getPhone() : "",
                        s.getEmail() != null ? s.getEmail() : "",
                        s.getStatus() != null ? s.getStatus().name() : ""
                });
            }
        }
    }

    private String genderLabel(Gender g) {
        return switch (g) {
            case MALE   -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER  -> "Khác";
        };
    }

    /* ════════════════════════════════════════════
     *  CRUD — Thêm
     * ════════════════════════════════════════════ */
    private void doAdd() {
        Student student = showStudentDialog(null);
        if (student != null) {
            try {
                studentService.save(student);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Đã thêm học viên.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ════════════════════════════════════════════
     *  CRUD — Sửa
     * ════════════════════════════════════════════ */
    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để sửa.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        studentService.findById(id).ifPresent(s -> {
            Student updated = showStudentDialog(s);
            if (updated != null) {
                updated.setId(s.getId());
                updated.setRegistrationDate(s.getRegistrationDate());
                try {
                    studentService.save(updated);
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Đã cập nhật học viên.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /* ════════════════════════════════════════════
     *  CRUD — Xóa
     * ════════════════════════════════════════════ */
    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để xóa.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa học viên này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            studentService.deleteById(id);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Đã xóa học viên.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ════════════════════════════════════════════
     *  Placement Test
     * ════════════════════════════════════════════ */
    private void doPlacementTest() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một học viên để làm Test đầu vào.",
                    "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long studentId = (Long) tableModel.getValueAt(row, 0);
        String studentName = (String) tableModel.getValueAt(row, 1);
        studentService.findById(studentId).ifPresent(student ->
                showPlacementTestDialog(student, studentName));
    }

    /* ════════════════════════════════════════════
     *  Dialog — Thêm/Sửa Học viên
     * ════════════════════════════════════════════ */
    private Student showStudentDialog(Student existing) {
        boolean isNew = (existing == null);
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isNew ? "Thêm học viên" : "Sửa học viên", true);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Họ tên
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Họ tên (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfName = new JTextField(25);
        if (!isNew) tfName.setText(existing.getFullName());
        p.add(tfName, gbc);
        row++;

        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Ngày sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfDob = new JTextField(25);
        if (!isNew && existing.getDateOfBirth() != null)
            tfDob.setText(existing.getDateOfBirth().format(DATE_FMT));
        p.add(tfDob, gbc);
        row++;

        // Giới tính
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<String> comboGender = new JComboBox<>(new String[]{"-- Chọn --", "Nam", "Nữ", "Khác"});
        if (!isNew && existing.getGender() != null) {
            comboGender.setSelectedItem(genderLabel(existing.getGender()));
        }
        p.add(comboGender, gbc);
        row++;

        // SĐT
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfPhone = new JTextField(25);
        if (!isNew && existing.getPhone() != null) tfPhone.setText(existing.getPhone());
        p.add(tfPhone, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfEmail = new JTextField(25);
        if (!isNew && existing.getEmail() != null) tfEmail.setText(existing.getEmail());
        p.add(tfEmail, gbc);
        row++;

        // Địa chỉ
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfAddress = new JTextField(25);
        if (!isNew && existing.getAddress() != null) tfAddress.setText(existing.getAddress());
        p.add(tfAddress, gbc);
        row++;

        // Trạng thái
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Status> comboStatus = new JComboBox<>(Status.values());
        if (!isNew && existing.getStatus() != null) comboStatus.setSelectedItem(existing.getStatus());
        p.add(comboStatus, gbc);
        row++;

        // Buttons
        final Student[] result = new Student[1];
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Lưu");
        JButton cancel = new JButton("Hủy");

        ok.addActionListener(e -> {
            // === Validate ===
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập họ tên.");
                return;
            }

            String email = tfEmail.getText().trim().isEmpty() ? null : tfEmail.getText().trim();
            String phone = tfPhone.getText().trim().isEmpty() ? null : tfPhone.getText().trim();

            // Kiểm tra trùng email
            if (email != null) {
                boolean dup = isNew
                        ? studentService.existsByEmail(email)
                        : studentService.existsByEmailExcluding(email, existing.getId());
                if (dup) {
                    JOptionPane.showMessageDialog(dlg, "Email \"" + email + "\" đã tồn tại!",
                            "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Kiểm tra trùng phone
            if (phone != null) {
                boolean dup = isNew
                        ? studentService.existsByPhone(phone)
                        : studentService.existsByPhoneExcluding(phone, existing.getId());
                if (dup) {
                    JOptionPane.showMessageDialog(dlg, "Số điện thoại \"" + phone + "\" đã tồn tại!",
                            "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Parse ngày sinh
            LocalDate dob = null;
            String dobText = tfDob.getText().trim();
            if (!dobText.isEmpty()) {
                try {
                    dob = LocalDate.parse(dobText, DATE_FMT);
                    if (!dob.isBefore(LocalDate.now())) {
                        JOptionPane.showMessageDialog(dlg, "Ngày sinh phải là ngày trong quá khứ.");
                        return;
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dlg, "Ngày sinh không hợp lệ (dd/MM/yyyy).");
                    return;
                }
            }

            // Parse giới tính
            Gender gender = null;
            String genderSel = (String) comboGender.getSelectedItem();
            if ("Nam".equals(genderSel)) gender = Gender.MALE;
            else if ("Nữ".equals(genderSel)) gender = Gender.FEMALE;
            else if ("Khác".equals(genderSel)) gender = Gender.OTHER;

            // Build entity
            Student s = isNew ? new Student() : existing;
            s.setFullName(name);
            s.setDateOfBirth(dob);
            s.setGender(gender);
            s.setPhone(phone);
            s.setEmail(email);
            s.setAddress(tfAddress.getText().trim().isEmpty() ? null : tfAddress.getText().trim());
            s.setStatus((Status) comboStatus.getSelectedItem());

            if (isNew) {
                s.setRegistrationDate(LocalDateTime.now());
            }

            result[0] = s;
            dlg.dispose();
        });

        cancel.addActionListener(e -> dlg.dispose());
        btnPanel.add(ok);
        btnPanel.add(cancel);
        p.add(btnPanel, gbc);

        dlg.getContentPane().add(p);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        return result[0];
    }

    /* ════════════════════════════════════════════
     *  Dialog — Placement Test (Test đầu vào)
     * ════════════════════════════════════════════ */
    private void showPlacementTestDialog(Student student, String studentName) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Test đầu vào — " + studentName, true);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Thông tin học viên
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("Học viên: " + studentName + " (ID: " + student.getId() + ")");
        lblInfo.setFont(lblInfo.getFont().deriveFont(Font.BOLD, 13f));
        p.add(lblInfo, gbc);
        row++;

        // Hiển thị kết quả test cũ (nếu có)
        gbc.gridy = row; gbc.gridwidth = 2;
        Optional<Result> lastResult = placementTestService.getLatestResult(student.getId());
        if (lastResult.isPresent()) {
            Result lr = lastResult.get();
            String level = placementTestService.determineLevel(lr.getGrade());
            JLabel lblLast = new JLabel(String.format(
                    "Kết quả test gần nhất: Điểm = %s | Xếp loại = %s | Trình độ = %s",
                    lr.getScore(), lr.getGrade(), level));
            lblLast.setForeground(new Color(0, 100, 0));
            p.add(lblLast, gbc);
        } else {
            JLabel lblNo = new JLabel("Chưa có kết quả test đầu vào.");
            lblNo.setForeground(Color.GRAY);
            p.add(lblNo, gbc);
        }
        row++;

        // Separator
        gbc.gridy = row; gbc.gridwidth = 2;
        p.add(new JSeparator(), gbc);
        row++;

        // Nhập điểm
        gbc.gridy = row; gbc.gridwidth = 1; gbc.gridx = 0; gbc.weightx = 0;
        p.add(new JLabel("Điểm (0-100):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfScore = new JTextField(10);
        p.add(tfScore, gbc);
        row++;

        // Nhận xét
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel("Nhận xét:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextArea taComment = new JTextArea(3, 25);
        taComment.setLineWrap(true);
        taComment.setWrapStyleWord(true);
        p.add(new JScrollPane(taComment), gbc);
        row++;

        // Auto-grade preview
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel lblPreview = new JLabel(" ");
        lblPreview.setFont(lblPreview.getFont().deriveFont(Font.ITALIC, 12f));
        lblPreview.setForeground(new Color(70, 70, 160));
        p.add(lblPreview, gbc);
        row++;

        // Khi nhập điểm → preview grade + level
        tfScore.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                try {
                    BigDecimal score = new BigDecimal(tfScore.getText().trim());
                    if (score.compareTo(BigDecimal.ZERO) >= 0 && score.compareTo(new BigDecimal("100")) <= 0) {
                        String grade = placementTestService.determineGrade(score);
                        String level = placementTestService.determineLevel(grade);
                        lblPreview.setText("→ Xếp loại: " + grade + " | Trình độ: " + level);
                    } else {
                        lblPreview.setText("Điểm phải từ 0 đến 100.");
                    }
                } catch (NumberFormatException ex) {
                    lblPreview.setText(" ");
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        // Buttons
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Lưu kết quả");
        JButton btnCancel = new JButton("Đóng");

        btnSubmit.addActionListener(e -> {
            String scoreText = tfScore.getText().trim();
            if (scoreText.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập điểm.");
                return;
            }
            try {
                BigDecimal score = new BigDecimal(scoreText);
                if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("100")) > 0) {
                    JOptionPane.showMessageDialog(dlg, "Điểm phải từ 0 đến 100.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String comment = taComment.getText().trim().isEmpty() ? null : taComment.getText().trim();

                Result saved = placementTestService.submitTest(student.getId(), score, comment);
                String level = placementTestService.determineLevel(saved.getGrade());

                JOptionPane.showMessageDialog(dlg,
                        String.format("Kết quả đã lưu!\n\nĐiểm: %s\nXếp loại: %s\nTrình độ: %s",
                                saved.getScore(), saved.getGrade(), level),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Điểm không hợp lệ (nhập số).",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dlg.dispose());
        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);
        p.add(btnPanel, gbc);

        dlg.getContentPane().add(p);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(480, 350));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void doEnroll() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên cần ghi danh!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long studentId = (Long) tableModel.getValueAt(row, 0);
        Student student = studentService.findById(studentId).orElse(null);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy học viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EnrollmentDialog dialog = new EnrollmentDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                student,
                enrollmentService,
                clazzDao
        );
        dialog.setVisible(true);
    }

    /** Hiển thị các lớp mà học viên đang/đã ghi danh (dùng cho ADMIN/STAFF). */
    private void showClassesOfSelectedStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một học viên.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long studentId = (Long) tableModel.getValueAt(row, 0);
        String studentName = (String) tableModel.getValueAt(row, 1);

        java.util.List<com.edulanguage.entity.Enrollment> enrollments =
                enrollmentService.findByStudentId(studentId);
        if (enrollments == null || enrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Học viên hiện chưa được ghi danh lớp nào.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] cols = {"ID lớp", "Tên lớp", "Khóa học", "Trạng thái ghi danh"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (com.edulanguage.entity.Enrollment e : enrollments) {
            com.edulanguage.entity.Clazz clazz = e.getClazz();
            if (clazz == null) continue;
            String courseName = clazz.getCourse() != null ? clazz.getCourse().getCourseName() : "";
            model.addRow(new Object[]{
                    clazz.getId(),
                    clazz.getClassName(),
                    courseName,
                    e.getStatus()
            });
        }

        JTable tbl = new JTable(model);
        tbl.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(this, scroll,
                "Lớp học của học viên: " + studentName,
                JOptionPane.PLAIN_MESSAGE);
    }
}
