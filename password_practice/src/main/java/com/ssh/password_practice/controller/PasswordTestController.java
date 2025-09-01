package com.ssh.password_practice.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/password-test")
public class PasswordTestController {
    private static final Logger log = LoggerFactory.getLogger(PasswordTestController.class);
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordTestController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/encode")
    public ResponseEntity<Map<String, Object>> encodePassword(@RequestParam String password) {
        long StartTime = System.currentTimeMillis();

        String encoded = passwordEncoder.encode(password);

        long endTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();  //자바에서 쓰는 JSon 형태
        response.put("original", password);
        response.put("encoded", encoded);
        response.put("length", encoded.length());
        response.put("encodingTime", endTime - StartTime);

        if (encoded.startsWith("$2b$")) {
            String[] parts = encoded.split("\\$");  //$ 기준으로 구분되어있으니까 이렇게 설정
            response.put("algorithm", parts[1]);
            response.put("cost", parts[2]);
            response.put("salt", parts[3].substring(0, 22));
            response.put("hash", parts[3].substring(22));
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String ,Object>> verifyPassword(@RequestParam String password, @RequestParam String encoded) {

        long startTime = System.currentTimeMillis();

        boolean matches = passwordEncoder.matches(password, encoded);

        long endTime = System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("password", password);
        response.put("encoded", encoded);
        response.put("matches", matches);
        response.put("verificationTime", endTime - startTime);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/multiple-encode")
    public ResponseEntity<Map<String, Object>> multipleEncode(@RequestParam String password, @RequestParam(defaultValue = "3") int count) {
        Map<String, Object> response = new HashMap<>();
        response.put("original", password);

        Map<Integer, String> results = new HashMap<>(); //Ingeger = > 몇회 실행했는지

        for (int i = 1; i < count; i++) {
            String encoded = passwordEncoder.encode(password);
            results.put(i, encoded);
        }
        response.put("encodedVersions", results);
        response.put("note", "같은 패스워드라도 Salt 때문에 매번 다른 해시값이 생성됩니다");

        return ResponseEntity.ok(response);
    }
}
