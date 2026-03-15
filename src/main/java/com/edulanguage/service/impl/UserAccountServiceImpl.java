package com.edulanguage.service.impl;

import com.edulanguage.entity.UserAccount;
import com.edulanguage.entity.enums.Role;
import com.edulanguage.repository.UserAccountRepository;
import com.edulanguage.service.UserAccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Tầng nghiệp vụ: triển khai xử lý tài khoản.
 * Chỉ tầng Service gọi Repository (Data Access).
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> findByRoleAndRelatedId(Role role, Long relatedId) {
        return userAccountRepository.findByRoleAndRelatedId(role, relatedId);
    }

    @Override
    @Transactional
    public void createOrUpdateAccount(Role role, Long relatedId, String username, String rawPassword) {
        if (username == null || username.isBlank()) return;

        Optional<UserAccount> existingOpt = userAccountRepository.findByRoleAndRelatedId(role, relatedId);
        
        if (existingOpt.isPresent()) {
            UserAccount account = existingOpt.get();
            account.setUsername(username.trim());
            // Only update password if a new one is provided
            if (rawPassword != null && !rawPassword.isBlank()) {
                account.setPasswordHash(passwordEncoder.encode(rawPassword));
            }
            userAccountRepository.save(account);
        } else {
            // Check if username is already taken by someone else
            if (userAccountRepository.findByUsername(username.trim()).isPresent()) {
                throw new IllegalArgumentException("Username đã tồn tại trên hệ thống.");
            }
            UserAccount newAccount = new UserAccount();
            newAccount.setRole(role);
            newAccount.setRelatedId(relatedId);
            newAccount.setUsername(username.trim());
            
            String passToSet = (rawPassword != null && !rawPassword.isBlank()) ? rawPassword : "Password@123";
            newAccount.setPasswordHash(passwordEncoder.encode(passToSet));
            
            userAccountRepository.save(newAccount);
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản."));
                
        if (!passwordEncoder.matches(oldPassword, account.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác.");
        }
        
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới không hợp lệ (ít nhất 6 ký tự).");
        }
        
        account.setPasswordHash(passwordEncoder.encode(newPassword));
        userAccountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteByRoleAndRelatedId(Role role, Long relatedId) {
        userAccountRepository.findByRoleAndRelatedId(role, relatedId)
                .ifPresent(userAccountRepository::delete);
    }
}
