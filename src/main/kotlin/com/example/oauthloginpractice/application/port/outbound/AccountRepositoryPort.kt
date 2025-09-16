package com.example.oauthloginpractice.application.port.outbound

import com.example.oauthloginpractice.domain.entity.Account
import java.util.Optional

interface AccountRepositoryPort {
    fun save(account: Account): Account
    fun findByProviderAndProviderId(provider: String, providerId: String): Optional<Account>
}