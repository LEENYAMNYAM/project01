package com.example.pro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class UserEntity  extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;
    @Column(nullable = false, unique=true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String name;
    private String gender;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
    private  String address;

    @Column(nullable = false)
    private int point;

    public void addPoints(int amount) {
        point += amount;
    }
    public void change(String email, String phone,String address) {
        this.email = email;
        this.phone = phone;
        this.address = address;
    }



}
