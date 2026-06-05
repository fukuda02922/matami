package com.matamichi.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.util.UUID

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockUuidUserSecurityContextFactory::class)
annotation class WithMockUuidUser(
    val uuid: String = "00000000-0000-0000-0000-000000000001",
)

class WithMockUuidUserSecurityContextFactory : WithSecurityContextFactory<WithMockUuidUser> {
    override fun createSecurityContext(annotation: WithMockUuidUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()
        val auth = UsernamePasswordAuthenticationToken(
            UUID.fromString(annotation.uuid),
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER")),
        )
        context.authentication = auth
        return context
    }
}
