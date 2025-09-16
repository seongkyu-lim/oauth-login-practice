package com.example.oauthloginpractice.application.service

import com.example.oauthloginpractice.application.port.outbound.AccountRepositoryPort
import com.example.oauthloginpractice.domain.entity.Account
import com.example.oauthloginpractice.presentation.outbound.AccountRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomOAuth2UserService (
    private val accountRepository: AccountRepositoryPort
): DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        // 획득한 accessToken으로 사용자 정보 받아옴.
        val oAuth2User = super.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId // e.g., google, github
        val attributes = oAuth2User.attributes

        // Map provider-specific attributes to common fields
        val mapped = mapAttributes(registrationId, attributes)

        // Upsert Account
        val account = accountRepository
            .findByProviderAndProviderId(mapped.provider, mapped.providerId)
            .map { existing ->
                existing.apply {
                    // Update mutable fields
                    this.email = mapped.email
                    this.name = mapped.name
                    this.picture = mapped.picture
                }
            }
            .orElseGet {
                Account(
                    provider = mapped.provider,
                    providerId = mapped.providerId,
                    email = mapped.email,
                    name = mapped.name,
                    picture = mapped.picture
                )
            }
            .let { accountRepository.save(it) }

        // Build principal to return
        val authorities = oAuth2User.authorities.ifEmpty { setOf(SimpleGrantedAuthority("ROLE_USER")) }
        val principalAttributes = attributes.toMutableMap().apply {
            put("accountId", account.id)
            put("provider", account.provider)
            put("providerId", account.providerId)
        }
        val nameAttributeKey = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        return DefaultOAuth2User(authorities, principalAttributes, nameAttributeKey)
    }

    private data class MappedAttributes(
        val provider: String,
        val providerId: String,
        val email: String?,
        val name: String?,
        val picture: String?
    )

    private fun mapAttributes(registrationId: String, attributes: Map<String, Any>): MappedAttributes {
        return when (registrationId.lowercase()) {
            "google" -> {
                val sub = attributes["sub"]?.toString() ?: error("Google attribute 'sub' missing")
                val email = attributes["email"]?.toString()
                val name = attributes["name"]?.toString()
                val picture = attributes["picture"]?.toString()
                MappedAttributes("google", sub, email, name, picture)
            }
            "github" -> {
                val id = attributes["id"]?.toString() ?: error("GitHub attribute 'id' missing")
                val email = attributes["email"]?.toString() // can be null
                val name = (attributes["name"] ?: attributes["login"])?.toString()
                val picture = attributes["avatar_url"]?.toString()
                MappedAttributes("github", id, email, name, picture)
            }
            else -> {
                // Fallback: try common keys
                val id = (attributes["id"] ?: attributes["sub"] ?: attributes["uid"])?.toString()
                    ?: error("Unsupported provider: $registrationId - cannot determine user id")
                val email = attributes["email"]?.toString()
                val name = (attributes["name"] ?: attributes["login"] ?: attributes["username"])?.toString()
                val picture = (attributes["picture"] ?: attributes["avatar_url"])?.toString()
                MappedAttributes(registrationId.lowercase(), id, email, name, picture)
            }
        }
    }
}