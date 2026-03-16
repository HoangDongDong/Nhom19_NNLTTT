package com.edulanguage.UI.panels;

import com.edulanguage.entity.Staff;
import com.edulanguage.entity.enums.Role;
import com.edulanguage.service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD Nhân viên — chỉ hiển thị khi đăng nhập ADMIN (Desktop).
 */
public class StaffsPanel extends JPanel {

    private final StaffService staffService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private static final String[] COLUMNS = { "ID", "Họ tên", "Email", "Điện thoại", "Vai trò" };

    public StaffsPanel(StaffService staffService) {
        this.staffService = staffService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Nhân viên", SwingConstants.LEFT);
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
        List<Staff> list = staffService.findAll();
        list.stream()
                .forEach(s -> tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getFullName(),
                        s.getEmail() != null ? s.getEmail() : "",
                        s.getPhone() != null ? s.getPhone() : "",
                        s.getRole() != null ? s.getRole().name() : ""
                }));
    }

    private void doAdd() {
        Staff staff = showDialog(null);
        if (staff != null) {
            staffService.save(staff);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Đã thêm nhân viên.");
        }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để sửa.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        staffService.findById(id).ifPresent(s -> {
            Staff updated = showDialog(s);
            if (updated != null) {
                updated.setId(s.getId());
                staffService.save(updated);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Đã cập nhật nhân viên.");
            }
        });
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để xóa.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        staffService.deleteById(id);
        refreshTable();
        JOptionPane.showMessageDialog(this, "Đã xóa nhân viên.");
    }

    private Staff showDialog(Staff existing) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existing != null ? "Sửa nhân viên" : "Thêm nhân viên", true);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridy = 0;
        p.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfName = new JTextField(20);
        if (existing != null) tfName.setText(existing.getFullName());
        p.add(tfName, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        p.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfEmail = new JTextField(20);
        if (existing != null && existing.getEmail() != null) tfEmail.setText(existing.getEmail());
        p.add(tfEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        p.add(new JLabel("Điện thoại:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfPhone = new JTextField(20);
        if (existing != null && existing.getPhone() != null) tfPhone.setText(existing.getPhone());
        p.add(tfPhone, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        p.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Role> comboRole = new JComboBox<>(Role.values());
        if (existing != null && existing.getRole() != null) comboRole.setSelectedItem(existing.getRole());
        p.add(comboRole, gbc);

        final Staff[] result = new Staff[1];
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Lưu");
        JButton cancel = new JButton("Hủy");
        ok.addActionListener(e -> {
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nhập họ tên.");
                return;
            }
            Staff staff = existing != null ? existing : new Staff();
            staff.setFullName(name);
            staff.setEmail(tfEmail.getText().trim().isEmpty() ? null : tfEmail.getText().trim());
            staff.setPhone(tfPhone.getText().trim().isEmpty() ? null : tfPhone.getText().trim());
            staff.setRole((Role) comboRole.getSelectedItem());
            result[0] = staff;
            dlg.dispose();
        });
        cancel.addActionListener(ev -> dlg.dispose());
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
