package com.example.oauthloginpractice.presentation.outbound

import com.example.oauthloginpractice.application.port.outbound.AccountRepositoryPort
import com.example.oauthloginpractice.domain.entity.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long>, AccountRepositoryPort {
}