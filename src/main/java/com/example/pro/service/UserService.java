package com.example.pro.service;

import com.example.pro.entity.UserEntity;
import com.example.pro.dto.UserDTO;

public interface UserService {
    void registerUser(UserDTO userDTO);
    UserEntity readUser(String username);
    void updateUser(String username, UserDTO userDTO);
    void deleteUser(String username);

    //    void changePassword(Long id, String newPassword);
    void dropUser(Long id);

    default UserEntity dtoToEntity(UserDTO userDTO) {

        UserEntity user = new UserEntity();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());

        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setGender(userDTO.getGender()); // String 타입 그대로 설정
        user.setAddress(userDTO.getAddress());


        return user;
    }

    default UserDTO entityToDto(UserEntity userEntity) {

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setName(userEntity.getName());

        userDTO.setEmail(userEntity.getEmail());
        userDTO.setPhone(userEntity.getPhone());
        userDTO.setGender(userEntity.getGender());
        userDTO.setAddress(userEntity.getAddress());




        return userDTO;
    }




}
