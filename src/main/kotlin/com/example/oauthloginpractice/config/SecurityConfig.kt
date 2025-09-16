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
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    // 공통 정적 리소스( /css/**, /js/**, /images/**, /webjars/** 등 ) 허용
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    // 개별 허용 경로
                    .requestMatchers(
                        "/", "/error", "/favicon.ico",
                        "/webjars/**"       // 필요시 유지
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .oauth2Login { /* 기본 설정이면 비워도 OK */ }

        return http.build()
    }
}