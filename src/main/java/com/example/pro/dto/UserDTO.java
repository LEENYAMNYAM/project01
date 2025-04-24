package com.example.pro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String password;
    private String passwordConfirmation; // 비밀번호 확인 필드 (엔티티에 없지만 DTO에 포함)
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String address;
    private String role;
    private int point;
    private LocalDateTime joinDate;

    public int getPoints() {
        return point;
    }

    public void setPoints(int points) {
        this.point = points;
    }

}