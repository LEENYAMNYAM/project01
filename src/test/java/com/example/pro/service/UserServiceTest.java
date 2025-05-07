package com.example.pro.service;

import com.example.pro.dto.UserDTO;
import com.example.pro.entity.UserEntity;
import com.example.pro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUserEntity;
    private UserDTO testUserDTO;
    private List<UserEntity> testUserEntities;

    @BeforeEach
    void setUp() {
        // 테스트용 UserEntity 설정
        testUserEntity = new UserEntity();
        testUserEntity.setUsername("testUser");
        testUserEntity.setName("테스트 사용자");
        testUserEntity.setEmail("test@example.com");
        testUserEntity.setPhone("010-1234-5678");
        testUserEntity.setGender("남성");
        testUserEntity.setAddress("서울시 강남구");
        testUserEntity.setPoint(1000);
        testUserEntity.setRole("ROLE_USER");

        // 테스트용 UserDTO 설정
        testUserDTO = new UserDTO();
        testUserDTO.setUsername("testUser");
        testUserDTO.setPassword("password123");
        testUserDTO.setName("테스트 사용자");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setPhone("010-1234-5678");
        testUserDTO.setGender("남성");
        testUserDTO.setAddress("서울시 강남구");
        testUserDTO.setPoint(1000);

        // 테스트용 UserEntity 리스트 설정
        testUserEntities = new ArrayList<>();
        testUserEntities.add(testUserEntity);
    }

    @Test
    @DisplayName("사용자 등록 테스트")
    void testRegisterUser() {
        // given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        // when
        userService.registerUser(testUserDTO);

        // then
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("사용자 조회 테스트")
    void testReadUser() {
        // given
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUserEntity));

        // when
        UserEntity result = userService.readUser(username);

        // then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(testUserEntity.getName(), result.getName());
        assertEquals(testUserEntity.getEmail(), result.getEmail());
        
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("사용자 조회 실패 테스트")
    void testReadUserNotFound() {
        // given
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.readUser(username);
        });
        
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 테스트")
    void testUpdateUser() {
        // given
        String username = "testUser";
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setEmail("updated@example.com");
        updatedUserDTO.setPhone("010-9876-5432");
        updatedUserDTO.setAddress("서울시 서초구");
        
        UserEntity existingUser = new UserEntity();
        existingUser.setUsername(username);
        existingUser.setEmail("old@example.com");
        existingUser.setPhone("010-1234-5678");
        existingUser.setAddress("서울시 강남구");
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);

        // when
        userService.updateUser(username, updatedUserDTO);

        // then
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(any(UserEntity.class));
        
        // 업데이트된 필드 확인
        assertEquals(updatedUserDTO.getEmail(), existingUser.getEmail());
        assertEquals(updatedUserDTO.getPhone(), existingUser.getPhone());
        assertEquals(updatedUserDTO.getAddress(), existingUser.getAddress());
    }

    @Test
    @DisplayName("모든 사용자 조회 테스트")
    void testFindAll() {
        // given
        when(userRepository.findAll()).thenReturn(testUserEntities);

        // when
        List<UserEntity> results = userService.findAll();

        // then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testUserEntity.getUsername(), results.get(0).getUsername());
        
        verify(userRepository, times(1)).findAll();
    }
}