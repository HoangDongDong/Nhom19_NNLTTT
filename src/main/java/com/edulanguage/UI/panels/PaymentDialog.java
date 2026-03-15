package com.edulanguage.UI.panels;

import com.edulanguage.entity.Invoice;
import com.edulanguage.entity.enums.PaymentMethod;
import com.edulanguage.service.FinanceService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class PaymentDialog extends JDialog {

    private final Invoice invoice;
    private final FinanceService financeService;
    private final Runnable onSuccess;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PaymentDialog(Frame owner, Invoice invoice, FinanceService financeService, Runnable onSuccess) {
        super(owner, "Thanh Toán Hóa Đơn", true);
        this.invoice = invoice;
        this.financeService = financeService;
        this.onSuccess = onSuccess;

        initUI();
        pack();
        setMinimumSize(new Dimension(500, 400));
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Invoice Info
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Mã Hóa Đơn:"), gbc);
        gbc.gridx = 1; p.add(new JLabel(String.valueOf(invoice.getId())), gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Học viên:"), gbc);
        gbc.gridx = 1; p.add(new JLabel(invoice.getStudent().getFullName()), gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Khóa học:"), gbc);
        gbc.gridx = 1; p.add(new JLabel(invoice.getEnrollment().getClazz().getCourse().getCourseName()), gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Học phí gốc:"), gbc);
        gbc.gridx = 1; 
        JLabel lblOriginalFee = new JLabel(String.format("%,.0f VNĐ", invoice.getTotalAmount()));
        lblOriginalFee.setFont(lblOriginalFee.getFont().deriveFont(Font.BOLD));
        p.add(lblOriginalFee, gbc);
        row++;

        // Discount section
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Mã giảm giá (nếu có):"), gbc);
        gbc.gridx = 1; 
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JTextField tfDiscount = new JTextField(10);
        JButton btnApply = new JButton("Áp dụng");
        discountPanel.add(tfDiscount);
        discountPanel.add(Box.createHorizontalStrut(10));
        discountPanel.add(btnApply);
        p.add(discountPanel, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Phải thanh toán:"), gbc);
        gbc.gridx = 1; 
        JLabel lblFinalFee = new JLabel(String.format("%,.0f VNĐ", invoice.getTotalAmount()));
        lblFinalFee.setForeground(Color.RED);
        lblFinalFee.setFont(lblFinalFee.getFont().deriveFont(Font.BOLD, 14f));
        p.add(lblFinalFee, gbc);
        row++;

        // Apply discount action
        btnApply.addActionListener(e -> {
            String code = tfDiscount.getText().trim();
            BigDecimal discount = financeService.calculateDiscountAmount(invoice.getTotalAmount(), code);
            BigDecimal finalFee = invoice.getTotalAmount().subtract(discount);
            lblFinalFee.setText(String.format("%,.0f VNĐ (Đã giảm %,.0f)", finalFee, discount));
            // Save final fee to a client property for retrieval later
            lblFinalFee.putClientProperty("finalAmount", finalFee);
        });

        // Payment input
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Phương thức:"), gbc);
        gbc.gridx = 1; 
        JComboBox<PaymentMethod> cbMethod = new JComboBox<>(PaymentMethod.values());
        p.add(cbMethod, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel("Số tiền nộp (VNĐ):"), gbc);
        gbc.gridx = 1; 
        JTextField tfAmount = new JTextField(invoice.getTotalAmount().toPlainString(), 15);
        p.add(tfAmount, gbc);
        row++;

        // Buttons
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Xác nhận");
        JButton btnCancel = new JButton("Hủy");

        btnSubmit.addActionListener(e -> {
            try {
                BigDecimal paidAmount = new BigDecimal(tfAmount.getText().replaceAll("[,.]", "").trim());
                String discountCode = tfDiscount.getText().trim();
                PaymentMethod method = (PaymentMethod) cbMethod.getSelectedItem();
                
                financeService.processPayment(invoice.getId(), paidAmount, method, discountCode);
                
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nHóa đơn và Trạng thái lớp của tự động được cập nhật.", "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
                if (onSuccess != null) {
                    onSuccess.run();
                }
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);
        p.add(btnPanel, gbc);

        setContentPane(p);
    }
}
