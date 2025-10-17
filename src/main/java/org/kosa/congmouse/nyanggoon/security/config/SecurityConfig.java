package org.kosa.congmouse.nyanggoon.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.security.jwt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    // 인증 설정 객체
    private final AuthenticationConfiguration authenticationConfiguration;
    // JWT 토큰 생성 및 검증 유틸리티
    private final JwtUtil jwtUtil;
    // 인증 실패시 엔드포인트
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    // 권한 부족시 핸들러
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Spring Security가 주입되면 내부적으로 글로벌 영역에 AuthenticationManger는 자동으로 주입
     * @param configuration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        log.info("SecurityConfig SecurityFilterChain 인증, 인가 설정");
        // CSRF(Cross-Site Request Forgery) 보호를 비활성화한다.
        // JWT와 같은 REST API에서는 보통 STATELESS 세션이므로 CSRF 공격으로부터 안전하여 비활성화한다
        http.csrf(auth -> auth.disable());

        // 폼 로그인 방식과 HTTP Basic 인증 비활성화(JWT 사용한 커스텀 방식 로그인)
        http.formLogin(auth -> auth.disable());
        http.httpBasic(auth -> auth.disable());

        // 인증 인가 설정
        http.authorizeHttpRequests(auth -> auth
                // 로그인 허용
                .requestMatchers("/auth/login").permitAll()
                // POST 방식의 회원 가입은 인증 없이 허용
                .requestMatchers(HttpMethod.POST, "/api/members").permitAll()
                // 관리자 모드는 인증과 ROLE_ADMIN 권한이 필요
                //ROLE_은 자동삽입
                .requestMatchers("/admin").hasRole("ADMIN")
                // GET 방식, 전체 게시글 조회는 인증 없이 접근을 모두 허용
                .requestMatchers(HttpMethod.GET, "/explorations").permitAll()
                .requestMatchers(HttpMethod.GET, "/explorations/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/explorations/images/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/talks").permitAll()
                .requestMatchers(HttpMethod.GET, "/talks/detail/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/photobox").permitAll()
                .requestMatchers(HttpMethod.GET, "/photobox/{id}").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                // 관리자 기능
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 참고 /api/product/ , /api/products/** 경로에 대한 접근을 모두 허용
                .requestMatchers("/swagger-ui").permitAll()
                .requestMatchers("/api-docs").permitAll()
                .requestMatchers("/heritages/*").permitAll()

                // 챗봇 API는 인증 필요
                .requestMatchers("/api/chat/**").authenticated()

                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated());

        // 예외 처리 핸들러 등록
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 인증 실패
                .accessDeniedHandler(customAccessDeniedHandler)        // 권한 부족
        );

        // 세션 관리 정책을 STATELESS(무상태)로 설정
        // JWT를 사용하기 때문에 서버 사용자 상태(세션)를 저장하지 않습니다.
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWTFilter를 LoginFilter 이전에 추가합니다.
        // 이 필터가 먼저 실행되어 요청 헤더의 JWT 토큰을 검증하고 인증 정보를 설정합니다.
        http.addFilterBefore(new JwtFilter(jwtUtil), JsonLoginFilter.class);

        // Spring Security의 UsernamePasswordAuthenticationFilter 자리에 커스텀 JsonLoginFilter 추가합니다.
        // 이 필터가 로그인 요청을 가로채서 로그인 검증 및 JWT 토큰을 생성하고 응답 헤더에 담아 보냅니다.
        http.addFilterAt(new JsonLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        // 설정된 HttpSecurity 객체를 기반으로 SecurityFilterChain을 빌드하여 반환합니다.
        return http.build();
    }

    /**
     * *CORS(Cross-Origin Resource Sharing) 설정
     * *
     * * CORS란?
     * * - 다른 도메인에서 API를 호출할 때 필요한 보안 정책
     * * - React(localhost:3000) → Spring Boot(localhost:8080) 통신 허용
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173"); // 리액트 앱의 출처
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        //이 부분을 추가하면 브라우저 콘솔창에 토큰 정보를 직접 확인할 있다
        config.addExposedHeader("Authorization");

        source.registerCorsConfiguration("/**", config);
        return source;

    }
}
