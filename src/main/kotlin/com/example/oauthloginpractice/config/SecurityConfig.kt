package com.example.oauthloginpractice.config

import com.example.oauthloginpractice.application.service.CustomOAuth2UserService
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it
                    // 공통 정적 리소스( /css/**, /js/**, /images/**, /webjars/** 등 ) 허용
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    // 개별 허용 경로
                    .requestMatchers("/", "/index.html", "/favicon.ico",
                        "/webjars/**", "/css/**", "/js/**", "/images/**",
                        "/h2-console/**",
                        "/ping"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .csrf { csrf ->
                csrf.ignoringRequestMatchers(PathRequest.toH2Console())
            }
            .headers { headers ->
                headers.frameOptions { it.sameOrigin() }
            }
            .oauth2Login {
                it.userInfoEndpoint { endpoints ->
                    endpoints.userService(customOAuth2UserService)
                }
            }
        return http.build()
    }
}