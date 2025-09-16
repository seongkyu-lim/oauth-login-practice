package com.example.oauthloginpractice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@SpringBootApplication
@RestController
class OauthLoginPracticeApplication {

    @GetMapping("/user")
    fun user(@AuthenticationPrincipal principal: OAuth2User): MutableMap<String?, Any?> {
        return Collections.singletonMap("name", principal.getAttribute("name"))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<OauthLoginPracticeApplication>(*args)
        }
    }
}
