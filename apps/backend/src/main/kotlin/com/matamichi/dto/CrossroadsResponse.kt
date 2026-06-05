package com.matamichi.dto

import com.matamichi.domain.Theme
import java.time.Instant
import java.util.UUID

data class CrossroadsResponse(
    val id: UUID,
    val theme: Theme,
    val body: String,
    val choiceA: String,
    val choiceB: String,
    val chosen: String,
    val ageAtTime: Int?,
    val countA: Long,
    val countB: Long,
    val resonanceCount: Long,
    val createdAt: Instant,
)
