package com.example.oauthloginpractice.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(name = "account", indexes = [
    Index(name = "idx_account_provider_providerId", columnList = "provider, provider_id", unique = true),
    Index(name = "idx_account_email", columnList = "email")
])
class Account(
    @Column(nullable = false)
    var provider: String,

    @Column(name = "provider_id", nullable = false)
    var providerId: String,

    @Column(nullable = true)
    var email: String? = null,

    @Column(nullable = true)
    var name: String? = null,

    @Column(nullable = true)
    var picture: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}