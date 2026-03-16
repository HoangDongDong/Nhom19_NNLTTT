package com.edulanguage.UI.panels;

import com.edulanguage.entity.Room;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD Phòng học — chỉ hiển thị khi đăng nhập ADMIN (Desktop).
 */
public class RoomsPanel extends JPanel {

    private final RoomService roomService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private static final String[] COLUMNS = { "ID", "Tên phòng", "Sức chứa", "Vị trí", "Trạng thái" };

    public RoomsPanel(RoomService roomService) {
        this.roomService = roomService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Phòng học", SwingConstants.LEFT);
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
        List<Room> list = roomService.findAll();
        list.stream()
                .forEach(r -> tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getRoomName(),
                        r.getCapacity() != null ? r.getCapacity() : "",
                        r.getLocation() != null ? r.getLocation() : "",
                        r.getStatus() != null ? r.getStatus().name() : ""
                }));
    }

    private void doAdd() {
        Room room = showDialog(null);
        if (room != null) {
            roomService.save(room);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Đã thêm phòng học.");
        }
    }

    private void doEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để sửa.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        roomService.findById(id).ifPresent(r -> {
            Room updated = showDialog(r);
            if (updated != null) {
                updated.setId(r.getId());
                roomService.save(updated);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Đã cập nhật phòng học.");
            }
        });
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để xóa.", "Chú ý", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa phòng học này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        Long id = (Long) tableModel.getValueAt(row, 0);
        roomService.deleteById(id);
        refreshTable();
        JOptionPane.showMessageDialog(this, "Đã xóa phòng học.");
    }

    private Room showDialog(Room existing) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), existing != null ? "Sửa phòng học" : "Thêm phòng học", true);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.weightx = 0; gbc.gridy = 0;
        p.add(new JLabel("Tên phòng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfName = new JTextField(20);
        if (existing != null) tfName.setText(existing.getRoomName());
        p.add(tfName, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        p.add(new JLabel("Sức chứa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfCapacity = new JTextField(10);
        if (existing != null && existing.getCapacity() != null) tfCapacity.setText(String.valueOf(existing.getCapacity()));
        p.add(tfCapacity, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        p.add(new JLabel("Vị trí:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField tfLocation = new JTextField(20);
        if (existing != null && existing.getLocation() != null) tfLocation.setText(existing.getLocation());
        p.add(tfLocation, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        p.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Status> comboStatus = new JComboBox<>(Status.values());
        if (existing != null && existing.getStatus() != null) comboStatus.setSelectedItem(existing.getStatus());
        p.add(comboStatus, gbc);

        final Room[] result = new Room[1];
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Lưu");
        JButton cancel = new JButton("Hủy");
        ok.addActionListener(e -> {
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nhập tên phòng.");
                return;
            }
            Integer cap = null;
            String capStr = tfCapacity.getText().trim();
            if (!capStr.isEmpty()) {
                try {
                    cap = Integer.parseInt(capStr);
                    if (cap < 1) { JOptionPane.showMessageDialog(dlg, "Sức chứa phải >= 1."); return; }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dlg, "Sức chứa phải là số.");
                    return;
                }
            }
            Room room = existing != null ? existing : new Room();
            room.setRoomName(name);
            room.setCapacity(cap);
            room.setLocation(tfLocation.getText().trim().isEmpty() ? null : tfLocation.getText().trim());
            room.setStatus((Status) comboStatus.getSelectedItem());
            result[0] = room;
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
