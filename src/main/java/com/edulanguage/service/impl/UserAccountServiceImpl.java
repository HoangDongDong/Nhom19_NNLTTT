package com.edulanguage.service.impl;

import com.edulanguage.entity.UserAccount;
import com.edulanguage.repository.UserAccountRepository;
import com.edulanguage.service.UserAccountService;
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

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> findByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }
}
