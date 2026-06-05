package com.matamichi.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "resonances")
class Resonance(
    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "crossroads_id", nullable = false, updatable = false)
    val crossroadsId: UUID,

    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: UUID,

    @Column(nullable = false, length = 1)
    val chosen: String,

    @Column(name = "after_text", nullable = false, columnDefinition = "TEXT")
    val afterText: String,

    @Column(name = "years_later", nullable = true)
    val yearsLater: Int? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
