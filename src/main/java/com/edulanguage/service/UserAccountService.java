package com.edulanguage.service;

import com.edulanguage.entity.UserAccount;

import java.util.Optional;

/**
 * Tầng nghiệp vụ: dịch vụ tài khoản người dùng.
 * Presentation chỉ gọi Service, không gọi trực tiếp Repository.
 */
public interface UserAccountService {

    Optional<UserAccount> findByUsername(String username);
}
