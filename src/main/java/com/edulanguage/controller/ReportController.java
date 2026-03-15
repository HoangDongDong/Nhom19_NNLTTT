package com.edulanguage.controller;

import com.edulanguage.entity.Invoice;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Result;
import com.edulanguage.service.FinanceService;
import com.edulanguage.service.EnrollmentService;
import com.edulanguage.repository.ResultRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final FinanceService financeService;
    private final EnrollmentService enrollmentService;
    private final ResultRepository resultRepository;

    public ReportController(FinanceService financeService, EnrollmentService enrollmentService, ResultRepository resultRepository) {
        this.financeService = financeService;
        this.enrollmentService = enrollmentService;
        this.resultRepository = resultRepository;
    }

    @GetMapping("/invoice/{id}")
    public String printInvoice(@PathVariable Long id, Model model) {
        Optional<Invoice> optInvoice = financeService.findInvoiceById(id);
        if (optInvoice.isPresent()) {
            model.addAttribute("invoice", optInvoice.get());
            return "report/invoice-print";
        }
        return "error"; // Nếu không tìm thấy
    }

    @GetMapping("/certificate/{enrollmentId}")
    public String viewCertificate(@PathVariable Long enrollmentId, Model model) {
        Optional<Enrollment> optEnrollment = enrollmentService.findById(enrollmentId);
        if (optEnrollment.isPresent()) {
            Enrollment enrollment = optEnrollment.get();
            
            // Yêu cầu lấy điểm chính thức của Học viên trong Lớp học cụ thể này
            Optional<Result> optResult = resultRepository.findByStudentIdAndClazzId(
                    enrollment.getStudent().getId(), 
                    enrollment.getClazz().getId()
            );
            
            if (optResult.isPresent() && !optResult.get().getGrade().equals("F") && "ACTIVE".equals(enrollment.getStatus())) {
                model.addAttribute("enrollment", enrollment);
                model.addAttribute("result", optResult.get());
                return "report/certificate";
            } else {
                model.addAttribute("error", "Chưa đủ điều kiện nhận chứng chỉ (Cần hoàn tất học phí, có điểm cuối kỳ và điểm khác F).");
            }
        } else {
            model.addAttribute("error", "Không tìm thấy ghi danh.");
        }
        return "error";
    }
}
