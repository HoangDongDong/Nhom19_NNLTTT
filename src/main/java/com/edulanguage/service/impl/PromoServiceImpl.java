package com.edulanguage.service.impl;

import com.edulanguage.entity.PromoCode;
import com.edulanguage.repository.PromoCodeRepository;
import com.edulanguage.service.PromoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromoServiceImpl implements PromoService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoServiceImpl(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCode> findAll() {
        return promoCodeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCode> findActivePromos() {
        return promoCodeRepository.findByIsActiveTrueOrderByCodeAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromoCode> findById(Long id) {
        return promoCodeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromoCode> findByCode(String code) {
        return promoCodeRepository.findByCode(code);
    }

    @Override
    @Transactional
    public PromoCode save(PromoCode promoCode) {
        // Chuyển mã thành chữ IN HOA cho thống nhất
        if (promoCode.getCode() != null) {
            promoCode.setCode(promoCode.getCode().trim().toUpperCase());
        }
        if (promoCode.getUsageCount() == null) {
            promoCode.setUsageCount(0);
        }
        if (promoCode.getIsActive() == null) {
            promoCode.setIsActive(true);
        }
        return promoCodeRepository.save(promoCode);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        promoCodeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(String code, BigDecimal originalAmount) {
        PromoCode promo = promoCodeRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Mã khuyến mại không tồn tại."));

        // Kiểm tra kích hoạt
        if (!Boolean.TRUE.equals(promo.getIsActive())) {
            throw new IllegalArgumentException("Mã khuyến mại đã bị vô hiệu hóa.");
        }

        // Kiểm tra thời hạn
        LocalDate today = LocalDate.now();
        if (promo.getValidFrom() != null && today.isBefore(promo.getValidFrom())) {
            throw new IllegalArgumentException("Mã khuyến mại chưa có hiệu lực (bắt đầu từ " + promo.getValidFrom() + ").");
        }
        if (promo.getValidUntil() != null && today.isAfter(promo.getValidUntil())) {
            throw new IllegalArgumentException("Mã khuyến mại đã hết hạn sử dụng.");
        }

        // Kiểm tra số lượt dùng
        if (promo.getMaxUsages() != null && promo.getUsageCount() >= promo.getMaxUsages()) {
            throw new IllegalArgumentException("Mã khuyến mại đã hết lượt sử dụng.");
        }

        // Tính tiền giảm
        BigDecimal discount = originalAmount
                .multiply(BigDecimal.valueOf(promo.getDiscountPercentage()))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.FLOOR);

        // Giới hạn mức giảm tối đa
        if (promo.getMaxDiscountAmount() != null && discount.compareTo(promo.getMaxDiscountAmount()) > 0) {
            discount = promo.getMaxDiscountAmount();
        }

        return discount;
    }

    @Override
    @Transactional
    public void incrementUsage(String code) {
        promoCodeRepository.findByCode(code.trim().toUpperCase()).ifPresent(promo -> {
            promo.setUsageCount(promo.getUsageCount() + 1);
            promoCodeRepository.save(promo);
        });
    }
}
