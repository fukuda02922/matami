package com.matamichi.repository

import com.matamichi.domain.Resonance
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResonanceRepository : JpaRepository<Resonance, UUID> {
    fun findByCrossroadsId(crossroadsId: UUID, pageable: Pageable): Page<Resonance>
    fun countByCrossroadsIdAndChosen(crossroadsId: UUID, chosen: String): Long
}
