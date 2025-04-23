package com.example.pro.service;

import com.example.pro.dto.UserDTO;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.ReviewRepository;
import com.example.pro.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public void registerUser(UserDTO userDTO) {
        UserEntity user  = dtoToEntity(userDTO);

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

        user.setRole("ROLE_USER");

        userRepository.save(user);

    }

    @Override
    public UserEntity readUser(Long id) {
        UserEntity foundUser = userRepository.findById(id).orElse(null);
        return foundUser;

    }

    @Override
    public void updateUser(Long id, UserDTO userDTO) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        userEntity.change(userDTO.getEmail(),userDTO.getAddress(),userDTO.getPhone());

        userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void addPoints(Long Id, int amount) {
        UserEntity user = userRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        user.addPoints(amount);  // UserEntity의 addPoints 메서드 호출
        userRepository.save(user);
    }


//    @Override  패스워드 체인지를 굳이 넣어야 되나 싶어서 비워둠
//    public void changePassword(Long id, String newPassword) {//
//    }

    @Override
    public void dropUser(Long id) {
        userRepository.deleteById(id);
    }
}
