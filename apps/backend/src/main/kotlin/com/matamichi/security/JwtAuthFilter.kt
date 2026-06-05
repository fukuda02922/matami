package com.matamichi.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class JwtAuthFilter(
    @Value("\${supabase.jwt-secret:}") private val jwtSecret: String,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ") && jwtSecret.isNotBlank()) {
            val token = authHeader.removePrefix("Bearer ")
            runCatching { verifyAndExtract(token) }
                .onSuccess { uid ->
                    val auth = UsernamePasswordAuthenticationToken(
                        uid,
                        null,
                        listOf(SimpleGrantedAuthority("ROLE_USER")),
                    )
                    SecurityContextHolder.getContext().authentication = auth
                }
        }
        filterChain.doFilter(request, response)
    }

    private fun verifyAndExtract(token: String): UUID {
        val secretBytes = jwtSecret.toByteArray(Charsets.UTF_8)
        val processor = DefaultJWTProcessor<SecurityContext>()
        val keySelector = JWSVerificationKeySelector(
            JWSAlgorithm.HS256,
            ImmutableSecret(secretBytes),
        )
        processor.jwsKeySelector = keySelector
        val claims = processor.process(token, null)
        val sub = claims.getStringClaim("sub")
        return UUID.fromString(sub)
    }
}
