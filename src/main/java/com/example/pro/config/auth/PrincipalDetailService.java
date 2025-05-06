package com.example.pro.config.auth;


import com.example.pro.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {
    private final com.example.pro.repository.UserRepository userRepository;
    private final com.example.pro.service.UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername");
        UserEntity user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("xx"));
        if (user == null) {
            return null;
        }
        PrincipalDetail principalDetails = new PrincipalDetail(user);
        log.info(principalDetails);
        return principalDetails;
    }
}