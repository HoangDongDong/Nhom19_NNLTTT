package com.edulanguage.UI.panels;

import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Result;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Dialog xuất/in chứng chỉ hoàn thành khóa học trực tiếp trên Desktop (không cần web).
 */
public class CertificatePrintDialog extends JDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CertificatePrintDialog(Frame owner, Enrollment enrollment, Result result) {
        super(owner, "Chứng chỉ - " + (enrollment.getStudent() != null ? enrollment.getStudent().getFullName() : "N/A"), true);
        setLayout(new BorderLayout(10, 10));
        setSize(280, 220);
        setLocationRelativeTo(owner);

        JEditorPane editor = new JEditorPane();
        editor.setContentType("text/html");
        editor.setEditable(false);
        editor.setText(buildCertificateHtml(enrollment, result));
        editor.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(editor);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("🖨️ In chứng chỉ");
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

    private String buildCertificateHtml(Enrollment enr, Result res) {
        String studentName = enr.getStudent() != null ? escape(enr.getStudent().getFullName()) : "N/A";
        String courseName = enr.getClazz() != null && enr.getClazz().getCourse() != null
                ? escape(enr.getClazz().getCourse().getCourseName()).toUpperCase() : "N/A";
        String grade = res != null && res.getGrade() != null ? escape(res.getGrade()) : "-";
        String dateStr = res != null && res.getCreatedAt() != null ? res.getCreatedAt().format(DATE_FMT) : "-";

        return "<html><head><meta charset='UTF-8'><style>" +
            "body { font-family: Arial, sans-serif; background: #f5f5f5; padding: 10px; margin: 0; }" +
            ".cert-container {" +
            "  width: 195px; min-height: 135px; margin: 0 auto; background: white;" +
            "  border: 5px solid #2c3e50; padding: 7px; text-align: center;" +
            "  box-shadow: 0 3px 10px rgba(0,0,0,0.15); box-sizing: border-box;" +
            "}" +
            ".cert-inner { border: 1px dashed #bdc3c7; padding: 7px; min-height: 110px; position: relative; box-sizing: border-box; }" +
            "h1 { color: #f39c12; font-size: 12px; margin: 0 0 3px 0; font-family: Georgia, serif; }" +
            "h2 { color: #2c3e50; font-size: 8px; letter-spacing: 1px; margin: 0 0 6px 0; }" +
            ".student-name { font-size: 11px; color: #34495e; font-weight: bold; border-bottom: 1px solid #bdc3c7;" +
            "  display: inline-block; padding-bottom: 2px; margin: 4px 0; font-family: Georgia, serif; max-width: 95%; }" +
            ".course-name { font-size: 10px; color: #2c3e50; font-weight: bold; margin: 5px 0; }" +
            ".grade-badge { background: #f39c12; color: white; padding: 2px 6px; border-radius: 8px;" +
            "  display: inline-block; font-size: 9px; margin-top: 3px; }" +
            ".footer { margin-top: 12px; display: table; width: 100%; font-size: 8px; font-weight: bold; color: #2c3e50; }" +
            ".footer > div { display: table-cell; text-align: center; width: 50%; }" +
            ".signature { border-top: 1px solid #7f8c8d; padding-top: 2px; min-width: 40px; font-size: 7px; }" +
            "p { color: #7f8c8d; font-size: 9px; margin: 2px 0; }" +
            "@media print {" +
            "  body { background: white; padding: 0; margin: 0; }" +
            "  .cert-container { width: 65mm; max-width: 65mm; margin: 0 auto; box-shadow: none; " +
            "    border: 4px solid #2c3e50; padding: 5px; page-break-inside: avoid; }" +
            "  .cert-inner { min-height: auto; padding: 5px; }" +
            "}" +
            "</style></head><body>" +
            "<div class='cert-container'>" +
            "<div class='cert-inner'>" +
            "<h2>TRUNG TÂM NGOẠI NGỮ EDULANGUAGE</h2>" +
            "<h1>Certificate of Completion</h1>" +
            "<p>This is to proudly certify that</p>" +
            "<div class='student-name'>" + studentName + "</div>" +
            "<p>has successfully completed the course</p>" +
            "<div class='course-name'>" + courseName + "</div>" +
            "<p>with an outstanding performance grade:</p>" +
            "<div class='grade-badge'>Grade: " + grade + "</div>" +
            "<div class='footer'>" +
            "<div><span>" + dateStr + "</span><br><div class='signature'>Date</div></div>" +
            "<div><span style='font-size:9px'>Director</span><br><div class='signature'>Center Director</div></div>" +
            "</div>" +
            "</div>" +
            "</div>" +
            "</body></html>";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
