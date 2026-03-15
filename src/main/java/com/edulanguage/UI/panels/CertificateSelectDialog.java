package com.edulanguage.UI.panels;

import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Result;
import com.edulanguage.service.EnrollmentService;
import com.edulanguage.service.ResultService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Dialog chọn ghi danh đủ điều kiện để in chứng chỉ (ACTIVE, có điểm, grade != F).
 */
public class CertificateSelectDialog extends JDialog {

    private final EnrollmentService enrollmentService;
    private final ResultService resultService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<EligibleEnrollment> eligibleList = new ArrayList<>();

    private record EligibleEnrollment(Enrollment enrollment, Result result) {}

    public CertificateSelectDialog(Frame owner, EnrollmentService enrollmentService, ResultService resultService) {
        super(owner, "In Chứng chỉ - Chọn ghi danh", true);
        this.enrollmentService = enrollmentService;
        this.resultService = resultService;
        setLayout(new BorderLayout(10, 10));
        setSize(700, 450);
        setLocationRelativeTo(owner);

        String[] columns = {"ID", "Học viên", "Lớp", "Khóa học", "Điểm"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("🖨️ In chứng chỉ");
        JButton btnClose = new JButton("Đóng");
        btnPrint.addActionListener(e -> doPrint());
        btnClose.addActionListener(e -> dispose());
        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hint = new JLabel("Chỉ hiển thị ghi danh ACTIVE có điểm cuối kỳ (khác F)");
        hint.setForeground(Color.GRAY);
        topPanel.add(hint);
        add(topPanel, BorderLayout.NORTH);

        loadEligibleEnrollments();
    }

    private void loadEligibleEnrollments() {
        tableModel.setRowCount(0);
        eligibleList.clear();
        try {
            List<Enrollment> all = enrollmentService.findAll();
            for (Enrollment enr : all) {
                if (!"ACTIVE".equals(enr.getStatus())) continue;
                if (enr.getStudent() == null || enr.getClazz() == null) continue;
                Optional<Result> optRes = resultService.findByStudentIdAndClazzId(
                        enr.getStudent().getId(), enr.getClazz().getId());
                if (optRes.isEmpty()) continue;
                String grade = optRes.get().getGrade();
                if (grade == null || grade.equals("F") || grade.equals("Không đạt")) continue;
                eligibleList.add(new EligibleEnrollment(enr, optRes.get()));
                String courseName = enr.getClazz().getCourse() != null ? enr.getClazz().getCourse().getCourseName() : "N/A";
                tableModel.addRow(new Object[]{
                        enr.getId(),
                        enr.getStudent().getFullName(),
                        enr.getClazz().getClassName(),
                        courseName,
                        grade
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doPrint() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một ghi danh để in chứng chỉ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (row >= eligibleList.size()) return;
        EligibleEnrollment ee = eligibleList.get(row);
        Frame ownerFrame = (Frame) getOwner();
        dispose();
        CertificatePrintDialog dialog = new CertificatePrintDialog(ownerFrame, ee.enrollment(), ee.result());
        dialog.setVisible(true);
    }
}
