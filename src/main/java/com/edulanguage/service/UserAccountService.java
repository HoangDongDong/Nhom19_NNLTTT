package com.edulanguage.service;

import com.edulanguage.entity.UserAccount;
import com.edulanguage.entity.enums.Role;

import java.util.Optional;

/**
 * Tầng nghiệp vụ: dịch vụ tài khoản người dùng.
 * Presentation chỉ gọi Service, không gọi trực tiếp Repository.
 */
public interface UserAccountService {

    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByRoleAndRelatedId(Role role, Long relatedId);

    void createOrUpdateAccount(Role role, Long relatedId, String username, String rawPassword);

    void changePassword(String username, String oldPassword, String newPassword);

    void deleteByRoleAndRelatedId(Role role, Long relatedId);
}
