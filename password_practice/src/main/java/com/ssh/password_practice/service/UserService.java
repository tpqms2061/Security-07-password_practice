package com.ssh.password_practice.service;

import com.ssh.password_practice.model.User;
import com.ssh.password_practice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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

    /**
     * 회원가입 - 패스워드 해싱 적용
     */
    public User register(String username, String rawPassword, String email) {
        if (userRepository.exitByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 사용자 명입니다.:" + username);
        }

        if (userRepository.exitByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다: " + email);
        }

        // 2. 패스워드 강도 검증
        validatePasswordStrength(rawPassword);
//        rawPassword: 입력한 패스워드

        // 3. 패스워드 해싱 (핵심!)
        System.out.println("🔐 패스워드 해싱 시작");
        System.out.println("📝 원본 패스워드: " + rawPassword);

        long startTime = System.currentTimeMillis();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        long endTime = System.currentTimeMillis();

        System.out.println("🔒 해시 결과: " + encodedPassword);
        System.out.println("⏱️ 해싱 소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("📏 해시 길이: " + encodedPassword.length() + " characters");

        User user = new User(username, encodedPassword, email);
        User savedUser = userRepository.save(user);

        System.out.println("✅ 사용자 등록 완료: " + savedUser);

        return savedUser;
    }

    /**
     * 패스워드 강도 검증
     * - 최소 8자 이상
     * - 대문자 1개 이상
     * - 소문자 1개 이상
     * - 숫자 1개 이상
     * - 특수문자 1개 이상
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("패스워드는 최소 8자 이상이어야 합니다");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("패스워드는 대소문자, 숫자, 특수문자를 모두 포함해야합니다.");
        }

        String[] commonPasswords = {"password", "123456789", "qwerty", "admin"};
        String lowerPassword = password.toLowerCase();

        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                throw new IllegalArgumentException("너무 일반적인 패스워드 입니다.");
            }
        }
        System.out.println("✅ 패스워드 강도 검증 통과");
    }

    /**
     * 로그인 검증 - 해시 매칭
     */
    public boolean validateLogin(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            System.out.println("❌ 사용자를 찾을 수 없음: " + username);
            return false;
        }

        User user = userOpt.get();

        System.out.println("🔍 로그인 검증 시작");
        System.out.println("👤 사용자: " + username);
        System.out.println("📝 입력된 패스워드: " + rawPassword);
        System.out.println("🔒 저장된 해시: " + user.getPassword());

        long startTime = System.currentTimeMillis();
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        long endTime = System.currentTimeMillis();

        System.out.println("⏱️ 검증 소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("🎯 검증 결과: " + (matches ? "성공" : "실패"));

        return matches;
    }

    private void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("기존 패스워드가 일치하지않습니다.");
        }

        validatePasswordStrength(newPassword);


        String enCodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(enCodedPassword);

        userRepository.save(user);
        System.out.println("✅ 패스워드 변경 완료: " + username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


}
