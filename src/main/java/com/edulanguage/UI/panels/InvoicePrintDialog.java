package com.edulanguage.UI.panels;

import com.edulanguage.entity.Invoice;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Dialog xuất/in hóa đơn trực tiếp trên Desktop (không cần web).
 */
public class InvoicePrintDialog extends JDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public InvoicePrintDialog(Frame owner, Invoice invoice) {
        super(owner, "Hóa đơn #" + invoice.getId(), true);
        setLayout(new BorderLayout(10, 10));
        setSize(700, 650);
        setLocationRelativeTo(owner);

        JEditorPane editor = new JEditorPane();
        editor.setContentType("text/html");
        editor.setEditable(false);
        editor.setText(buildInvoiceHtml(invoice));
        editor.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(editor);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("🖨️ In hóa đơn");
        JButton btnClose = new JButton("Đóng");
        btnPrint.addActionListener(e -> doPrint(editor));
        btnClose.addActionListener(e -> dispose());
        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void doPrint(JEditorPane editor) {
        try {
            editor.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildInvoiceHtml(Invoice inv) {
        String studentName = inv.getStudent() != null ? escape(inv.getStudent().getFullName()) : "N/A";
        String phone = inv.getStudent() != null && inv.getStudent().getPhone() != null ? escape(inv.getStudent().getPhone()) : "-";
        String email = inv.getStudent() != null && inv.getStudent().getEmail() != null ? escape(inv.getStudent().getEmail()) : "-";

        String courseInfo = "N/A";
        String feeStr = "-";
        if (inv.getEnrollment() != null && inv.getEnrollment().getClazz() != null) {
            String className = inv.getEnrollment().getClazz().getClassName();
            String courseName = inv.getEnrollment().getClazz().getCourse() != null
                    ? inv.getEnrollment().getClazz().getCourse().getCourseName() : "N/A";
            courseInfo = escape(className + " - " + courseName);
            if (inv.getEnrollment().getClazz().getCourse() != null && inv.getEnrollment().getClazz().getCourse().getFee() != null) {
                feeStr = formatVnd(inv.getEnrollment().getClazz().getCourse().getFee());
            }
        }

        BigDecimal discount = inv.getPromoDiscountAmount() != null ? inv.getPromoDiscountAmount() : BigDecimal.ZERO;
        BigDecimal finalTotal = inv.getTotalAmount() != null ? inv.getTotalAmount().subtract(discount) : BigDecimal.ZERO;
        String statusBadge = getStatusBadge(inv.getStatus());
        String issueDate = inv.getIssueDate() != null ? inv.getIssueDate().format(DATE_FMT) : "-";

        StringBuilder promoRow = new StringBuilder();
        if (inv.getAppliedPromoCode() != null && !inv.getAppliedPromoCode().isEmpty()) {
            promoRow.append("<tr class='item'><td>Mã KM: ").append(escape(inv.getAppliedPromoCode()))
                    .append("</td><td style='color:#28a745'>- ").append(formatVnd(discount)).append(" đ</td></tr>");
        }

        String finalTotalStr = formatVnd(finalTotal);
        return "<html><head><meta charset='UTF-8'><style>" +
            "body { font-family: Arial, sans-serif; font-size: 14px; color: #555; padding: 20px; }" +
            ".invoice-box { max-width: 650px; margin: 0 auto; }" +
            "table { width: 100%; border-collapse: collapse; }" +
            "td { padding: 6px; vertical-align: top; }" +
            ".title { font-size: 28px; color: #0d6efd; font-weight: bold; }" +
            ".heading td { background: #eee; font-weight: bold; border-bottom: 1px solid #ddd; }" +
            ".item td { border-bottom: 1px solid #eee; }" +
            ".total td { border-top: 2px solid #eee; font-weight: bold; font-size: 1.1em; color: #d9534f; }" +
            ".info-block { padding: 15px 0; }" +
            "@media print { body { padding: 10px; } }" +
            "</style></head><body>" +
            "<div class='invoice-box'>" +
            "<table><tr><td colspan='2'>" +
            "<table><tr>" +
            "<td class='title'>EduLanguage</td>" +
            "<td style='text-align:right'>" +
            "<b>Hóa đơn số:</b> #" + inv.getId() + "<br><b>Ngày lập:</b> " + issueDate + "<br><b>Trạng thái:</b> " + statusBadge +
            "</td></tr></table>" +
            "</td></tr>" +
            "<tr><td colspan='2' class='info-block'>" +
            "<table><tr>" +
            "<td><b>Trung tâm Ngoại ngữ EduLanguage</b><br>123 Đường Học Tập, Quận 1<br>TP. Hồ Chí Minh</td>" +
            "<td style='text-align:right'><b>Người nhận:</b><br>" + studentName + "<br>" + phone + "<br>" + email + "</td>" +
            "</tr></table>" +
            "</td></tr>" +
            "<tr class='heading'><td>Nội dung Khóa học</td><td style='text-align:right'>Học phí VND</td></tr>" +
            "<tr class='item'><td>" + courseInfo + "</td><td style='text-align:right'>" + feeStr + " đ</td></tr>" +
            promoRow +
            "<tr class='item'><td>Tổng cần thanh toán:</td><td style='text-align:right'>" + finalTotalStr + " đ</td></tr>" +
            "<tr class='total'><td></td><td style='text-align:right'>TỔNG CỘNG: " + finalTotalStr + " đ</td></tr>" +
            "</table>" +
            "<p style='margin-top:30px; font-size:12px; color:#888; text-align:center'>" +
            "Cảm ơn bạn đã tin tưởng lựa chọn khóa học của EduLanguage.<br>" +
            "<i>Hóa đơn được tạo tự động bởi hệ thống quản lý.</i>" +
            "</p>" +
            "</div></body></html>";
    }

    private String getStatusBadge(String status) {
        if (status == null) return "<span style='color:#666'>-</span>";
        return switch (status) {
            case "PAID" -> "<span style='background:#28a745;color:white;padding:2px 8px;border-radius:4px'>ĐÃ THANH TOÁN</span>";
            case "PARTIAL" -> "<span style='background:#ffc107;color:#333;padding:2px 8px;border-radius:4px'>TRẢ GÓP</span>";
            case "PENDING_CONFIRM" -> "<span style='background:#17a2b8;color:white;padding:2px 8px;border-radius:4px'>CHỜ XÁC NHẬN</span>";
            default -> "<span style='background:#dc3545;color:white;padding:2px 8px;border-radius:4px'>CHƯA THANH TOÁN</span>";
        };
    }

    private String formatVnd(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.0f", amount).replace(',', '.');
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
