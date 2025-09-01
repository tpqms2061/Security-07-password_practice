package com.ssh.password_practice.service;

import com.ssh.password_practice.model.User;
import com.ssh.password_practice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 패스워드 강도 검증 패턴
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String rawPassword, String email) {
        if(userRepository.exitByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 사용자 명입니다.:" + username);
        }

        if (userRepository.exitByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다: " + email);
        }
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("패스워드는 최소 8자 이상이어야 합니다");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("패스워드는 대소문자, 숫자, 특수문자를 모두 포함해야합니다.");
        }
    }

}
