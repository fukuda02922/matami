package com.matamichi.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "crossroads")
class Crossroads(
    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val theme: Theme,

    @Column(nullable = false, columnDefinition = "TEXT")
    val body: String,

    @Column(name = "choice_a", nullable = false, columnDefinition = "TEXT")
    val choiceA: String,

    @Column(name = "choice_b", nullable = false, columnDefinition = "TEXT")
    val choiceB: String,

    @Column(nullable = false, length = 1)
    val chosen: String,

    @Column(name = "age_at_time", nullable = true)
    val ageAtTime: Int? = null,

    @Column(name = "is_moderated", nullable = false)
    val isModerated: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
