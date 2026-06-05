package com.matamichi.dto

import java.time.Instant
import java.util.UUID

data class ResonanceResponse(
    val id: UUID,
    val crossroadsId: UUID,
    val chosen: String,
    val afterText: String,
    val yearsLater: Int?,
    val createdAt: Instant,
)
