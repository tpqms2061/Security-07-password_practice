package com.ssh.password_practice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true , nullable = false )
    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 3, max = 20, message = "사용자명은 3~20자 여야 합니다.")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "패스워드는 필수입니다")
    private String password; // 해시된 패스워드가 저장됨

    @Column(unique = true, nullable = false)
    @Email(message = "올바른 이메일형식이여야합니다.")
    private String email;

    //활성화 여부 .
    @Column(nullable = false)
    private Boolean enabled = true;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    //기본 생성자
    protected User() {}

    //생성자
    public User( String email, String password,  String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.createdAt = LocalDateTime.now();
        this.passwordChangedAt = LocalDateTime.now();
    }

//getter
    public LocalDateTime getCreatedAt() {return createdAt; }

    public String getEmail() {return email;}

    public Boolean getEnabled() {return enabled;}

    public Long getId() {return id;}

    public String getPassword() {return password;}

    public LocalDateTime getPasswordChangedAt() {return passwordChangedAt;}

    public String getUsername() {return username;}

    //setter
    public void setPassword(String password) {
        this.password = password;
        this.passwordChangedAt = LocalDateTime.now();
    }

    public void setUsername(String username) {this.username = username;}

    public void setEmail(String email) {this.email = email;}

    public void setEnabled(Boolean enabled) {this.enabled = enabled;}

    @Override
    public String toString() {
        return "User{" +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                '}';
    }
}

