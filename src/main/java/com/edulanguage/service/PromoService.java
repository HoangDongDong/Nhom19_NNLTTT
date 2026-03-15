package com.edulanguage.service;

import com.edulanguage.entity.PromoCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Tầng nghiệp vụ: Quản lý mã khuyến mại.
 */
public interface PromoService {

    List<PromoCode> findAll();

    /** Lấy danh sách mã đang kích hoạt (dùng cho dropdown khi thanh toán). */
    List<PromoCode> findActivePromos();

    Optional<PromoCode> findById(Long id);

    Optional<PromoCode> findByCode(String code);

    PromoCode save(PromoCode promoCode);

    void deleteById(Long id);

    /**
     * Kiểm tra tính hợp lệ của mã khuyến mại và trả về số tiền được giảm.
     * @param code Mã khuyến mại
     * @param originalAmount Số tiền gốc trước khi giảm
     * @return Số tiền được giảm
     * @throws IllegalArgumentException nếu mã không hợp lệ
     */
    BigDecimal calculateDiscount(String code, BigDecimal originalAmount);

    /**
     * Tăng usage count lên 1 sau khi thanh toán thành công.
     */
    void incrementUsage(String code);
}
