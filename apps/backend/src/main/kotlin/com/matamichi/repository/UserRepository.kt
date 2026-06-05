package com.matamichi.repository

import com.matamichi.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findBySupabaseUid(supabaseUid: UUID): User?
}
