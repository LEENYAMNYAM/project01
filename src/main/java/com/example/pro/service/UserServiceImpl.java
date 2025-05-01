package com.example.pro.service;

import com.example.pro.entity.UserEntity;
import com.example.pro.repository.UserRepository;
import com.example.pro.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(UserDTO userDTO) {
        UserEntity user  = dtoToEntity(userDTO);

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);

    }

    @Override
    public UserEntity readUser(String username) {
        // Optional을 사용하여 존재하지 않을 경우 처리
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + username));
    }

    @Override
    public void updateUser(String username, UserDTO userDTO) {
        Optional<UserEntity> existingUserOptional = userRepository.findByUsername(username);


        UserEntity userEntity = existingUserOptional.get();

        // 기존 엔티티를 수정하는 방식으로 변경
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPhone(userDTO.getPhone());
        userEntity.setAddress(userDTO.getAddress());

        // 데이터 저장
        userRepository.save(userEntity);

    }

    @Override
    public void deleteUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }


//    @Override
//    public void changePassword(Long id, String newPassword) {
//
//    }

    @Override
    public void dropUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userEntity -> {
                    UserDTO dto = new UserDTO();
                    dto.setUsername(userEntity.getUsername());
                    dto.setName(userEntity.getName());
                    dto.setEmail(userEntity.getEmail());
                    dto.setPhone(userEntity.getPhone());
                    dto.setGender(userEntity.getGender());
                    dto.setAddress(userEntity.getAddress());
                    dto.setRole(userEntity.getRole());
                    dto.setPoint(userEntity.getPoint());
                    dto.setJoinDate(userEntity.getRegDate());
                    return dto;
                })
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

}
