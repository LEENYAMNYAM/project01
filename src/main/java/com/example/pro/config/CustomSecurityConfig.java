package com.example.pro.config;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring()
                /* 웹시큐리티 설정중에서 무시(시큐리티를 적용되지 않아야되는 부분)받아야되는 부분을 작성 */
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        /* 스태틱 로케이션(resources/static 폴더안에 있는 내용)의 모든 요소들은 security의 제한을 받지않도록 해줌.(security에서 제외시킴)
         *  로그인하면 적용되지 않아야하는 것들(프론트부분이라거나) 처리 함  */
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication)
                    throws IOException, ServletException {
                // 예: 이전 페이지로 리다이렉트
                String redirectUrl = (String) request.getSession().getAttribute("prevPage");
                if (redirectUrl != null) {
                    request.getSession().removeAttribute("prevPage");
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect("/");
                }
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                AuthenticationException exception)
                    throws IOException, ServletException {
                String errorMessage = "아이디 또는 비밀번호가 잘못되었습니다.";

                if (exception instanceof LockedException) {
                    errorMessage = "계정이 잠겼습니다.";
                } else if (exception instanceof DisabledException) {
                    errorMessage = "계정이 비활성화되어 있습니다.";
                } else if (exception instanceof CredentialsExpiredException) {
                    errorMessage = "비밀번호가 만료되었습니다.";
                }

                request.getSession().setAttribute("errorMessage", errorMessage);
                response.sendRedirect("/userinfo/login?error=true");

            }
        };
    }




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.disable())
                .authorizeHttpRequests(authorizeHttpRequestsConfigurer -> authorizeHttpRequestsConfigurer
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers( "/", "/login","/static/**", "/assets/**","/recipe/list/**").permitAll()
                        .requestMatchers("/userinfo/**", "/recipe/view/**", "/csboard/**", "/notice/**", "/review/**", "/cart/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/",
                                "/home",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/style.css",
                                "/favicon.ico",
                                "/error").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .maximumSessions(1) // 동시에 하나의 세션만 허용
                        .maxSessionsPreventsLogin(false) // 기존 세션 만료 후 새 로그인 허용
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 요청 시 세션 생성
                )
                .formLogin(formLoginConfigurer -> formLoginConfigurer
                        .loginPage("/userinfo/login")
                        .loginProcessingUrl("/loginProcess")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .failureUrl("/userinfo/login?error=true")
                        .defaultSuccessUrl("/", true)
                        .successHandler(authenticationSuccessHandler()) // 성공 처리 핸들러
                        .failureHandler(authenticationFailureHandler()) // 실패 처리 핸들러

                        .permitAll())
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true))

                .build();  //리턴 왜 하는지 모르겠음
    }





    @Bean // password 암호화 빈
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring() // 모든 스태틱 자원 필터 제외
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        //.requestMatchers("/static/**");
    }
}
