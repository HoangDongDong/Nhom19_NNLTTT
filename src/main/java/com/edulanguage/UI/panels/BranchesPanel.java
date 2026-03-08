package com.edulanguage.UI.panels;

import com.edulanguage.entity.Branch;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.BranchService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD Chi nhánh — chỉ hiển thị khi đăng nhập ADMIN (Desktop).
 */
public class BranchesPanel extends JPanel {

    private final BranchService branchService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private static final String[] COLUMNS = { "ID", "Tên chi nhánh", "Địa chỉ", "Điện thoại", "Trạng thái" };

    public BranchesPanel(BranchService branchService) {
        this.branchService = branchService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Chi nhánh", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        btnAdd.addActionListener(e -> doAdd());
        btnEdit.addActionListener(e -> doEdit());
        btnDelete.addActionListener(e -> doDelete());
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        add(toolbar, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Branch> list = branchService.findAll();
        for (Branch b : list) {
            tableModel.addRow(new Object[]{
                    b.getId(),
                    b.getBranchName(),
                    b.getAddress() != null ? b.getAddress() : "",
                    b.getPhone() != null ? b.getPhone() : "",
                    b.getStatus() != null ? b.getStatus().name() : ""
            });
        }
    }

    private void doAdd() {
        Branch branch = showDialog(null);
        if (branch != null) {
            branchService.save(branch);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Đã thêm chi nhánh.");
        }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để sửa.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        branchService.findById(id).ifPresent(b -> {
            Branch updated = showDialog(b);
            if (updated != null) {
                updated.setId(b.getId());
                branchService.save(updated);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Đã cập nhật chi nhánh.");
            }
        });
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để xóa.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa chi nhánh này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        branchService.deleteById(id);
        refreshTable();
        JOptionPane.showMessageDialog(this, "Đã xóa chi nhánh.");
    }

    private Branch showDialog(Branch existing) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existing != null ? "Sửa chi nhánh" : "Thêm chi nhánh", true);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy = 0;
        p.add(new JLabel("Tên chi nhánh:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField tfName = new JTextField(20);
        if (existing != null) tfName.setText(existing.getBranchName());
        p.add(tfName, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        p.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfAddress = new JTextField(20);
        if (existing != null && existing.getAddress() != null) tfAddress.setText(existing.getAddress());
        p.add(tfAddress, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        p.add(new JLabel("Điện thoại:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfPhone = new JTextField(20);
        if (existing != null && existing.getPhone() != null) tfPhone.setText(existing.getPhone());
        p.add(tfPhone, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        p.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Status> comboStatus = new JComboBox<>(Status.values());
        if (existing != null && existing.getStatus() != null) comboStatus.setSelectedItem(existing.getStatus());
        p.add(comboStatus, gbc);

        final Branch[] result = new Branch[1];
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Lưu");
        JButton cancel = new JButton("Hủy");
        ok.addActionListener(e -> {
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nhập tên chi nhánh.");
                return;
            }
            Branch b = existing != null ? existing : new Branch();
            b.setBranchName(name);
            b.setAddress(tfAddress.getText().trim().isEmpty() ? null : tfAddress.getText().trim());
            b.setPhone(tfPhone.getText().trim().isEmpty() ? null : tfPhone.getText().trim());
            b.setStatus((Status) comboStatus.getSelectedItem());
            result[0] = b;
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
}
