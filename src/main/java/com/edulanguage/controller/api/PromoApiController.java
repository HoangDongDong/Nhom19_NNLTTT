package com.edulanguage.controller.api;

import com.edulanguage.service.PromoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API endpoint để kiểm tra mã khuyến mại (AJAX từ trang thanh toán).
 */
@RestController
@RequestMapping("/api/promos")
public class PromoApiController {

    private final PromoService promoService;

    public PromoApiController(PromoService promoService) {
        this.promoService = promoService;
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkPromo(@RequestParam String code,
                                                          @RequestParam BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();
        try {
            BigDecimal discountAmount = promoService.calculateDiscount(code, amount);
            BigDecimal finalAmount = amount.subtract(discountAmount);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }

            // Lấy thêm thông tin phần trăm
            int percentage = promoService.findByCode(code.trim().toUpperCase())
                    .map(p -> p.getDiscountPercentage())
                    .orElse(0);

            result.put("valid", true);
            result.put("discountAmount", discountAmount);
            result.put("finalAmount", finalAmount);
            result.put("percentage", percentage);
            result.put("message", "Mã khuyến mại hợp lệ!");
        } catch (IllegalArgumentException e) {
            result.put("valid", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
