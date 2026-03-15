package com.edulanguage.UI.panels;

import com.edulanguage.entity.Invoice;
import com.edulanguage.service.FinanceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoicesPanel extends JPanel {

    private final FinanceService financeService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private static final String[] COLUMNS = {
            "Mã Hóa Đơn", "Học viên", "Khóa học", "Tổng tiền", "Ngày tạo", "Trạng thái"
    };
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public InvoicesPanel(FinanceService financeService) {
        this.financeService = financeService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tiêu đề
        JLabel title = new JLabel("Quản lý Hóa Đơn & Thanh Toán", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // Bảng
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Thanh công cụ
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnPayment = new JButton("Thanh toán");
        JButton btnPrint = new JButton("In Hóa Đơn");

        btnRefresh.addActionListener(e -> refreshTable());
        btnPayment.addActionListener(e -> doPayment());
        btnPrint.addActionListener(e -> doPrint());

        toolbar.add(btnRefresh);
        toolbar.add(btnPayment);
        toolbar.add(btnPrint);
        add(toolbar, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Invoice> invoices = financeService.findAllInvoices();
        for (Invoice i : invoices) {
            String courseName = "N/A";
            if (i.getEnrollment() != null && i.getEnrollment().getClazz() != null && i.getEnrollment().getClazz().getCourse() != null) {
                courseName = i.getEnrollment().getClazz().getCourse().getCourseName();
            }

            tableModel.addRow(new Object[]{
                    i.getId(),
                    i.getStudent() != null ? i.getStudent().getFullName() : "N/A",
                    courseName,
                    String.format("%,.0f", i.getTotalAmount()),
                    i.getIssueDate() != null ? i.getIssueDate().format(DATE_FMT) : "",
                    i.getStatus()
            });
        }
    }

    private void doPayment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Hóa đơn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long invoiceId = (Long) tableModel.getValueAt(row, 0);
        Invoice invoice = financeService.findInvoiceById(invoiceId).orElse(null);

        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("PAID".equals(invoice.getStatus())) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán đày đủ!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        PaymentDialog dialog = new PaymentDialog((JFrame) SwingUtilities.getWindowAncestor(this), invoice, financeService, this::refreshTable);
        dialog.setVisible(true);
    }

    private void doPrint() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Hóa đơn để in!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long invoiceId = (Long) tableModel.getValueAt(row, 0);
        String url = "http://localhost:8080/report/invoice/" + invoiceId;

        try {
            Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể mở URL trình duyệt: " + url + "\nChi tiết: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
