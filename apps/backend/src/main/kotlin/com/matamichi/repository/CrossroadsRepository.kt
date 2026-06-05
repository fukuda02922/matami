package com.matamichi.repository

import com.matamichi.domain.Crossroads
import com.matamichi.domain.Theme
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CrossroadsRepository : JpaRepository<Crossroads, UUID> {
    fun findByTheme(theme: Theme, pageable: Pageable): Page<Crossroads>
    fun findByUserId(userId: UUID): List<Crossroads>
}
