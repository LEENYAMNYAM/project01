package com.example.pro.service;

import com.example.pro.dto.UserDTO;
import com.example.pro.entity.UserEntity;

public interface UserService {
    void registerUser(UserDTO userDTO);
    UserEntity readUser(Long id);
    void updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    void addPoints(Long Id, int amount);

//    void changePassword(Long id, String newPassword);
    void dropUser(Long id);

    default UserEntity dtoToEntity(UserDTO userDTO) {

        UserEntity user = new UserEntity();
        user.setUsername(userDTO.getUsername());

        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setGender(userDTO.getGender()); // String 타입 그대로 설정
        user.setAddress(userDTO.getAddress());
        user.setPoints(0);


        return user;
    }

    default UserDTO entityToDto(UserEntity userEntity) {

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userEntity.getUsername());

        userDTO.setEmail(userEntity.getEmail());
        userDTO.setPhone(userEntity.getPhone());
        userDTO.setGender(userEntity.getGender());
        userDTO.setAddress(userEntity.getAddress());
        userDTO.setPoints(userEntity.getPoints());




        return userDTO;
    }




}
