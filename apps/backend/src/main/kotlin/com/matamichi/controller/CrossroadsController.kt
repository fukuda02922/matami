package com.matamichi.controller

import com.matamichi.domain.Theme
import com.matamichi.dto.CrossroadsRequest
import com.matamichi.dto.CrossroadsResponse
import com.matamichi.dto.PagedResponse
import com.matamichi.service.CrossroadsService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v1")
class CrossroadsController(
    private val crossroadsService: CrossroadsService,
) {

    @GetMapping("/crossroads")
    fun getFeed(
        @RequestParam theme: Theme? = null,
        @RequestParam cursor: String? = null,
        @RequestParam(defaultValue = "20") limit: Int,
    ): PagedResponse<CrossroadsResponse> {
        return crossroadsService.getFeed(theme, cursor, limit.coerceIn(1, 100))
    }

    @PostMapping("/crossroads")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CrossroadsRequest,
        authentication: Authentication,
    ): CrossroadsResponse {
        val userId = authentication.principal as UUID
        return crossroadsService.create(userId, request)
    }

    @GetMapping("/crossroads/{id}")
    fun getById(@PathVariable id: UUID): CrossroadsResponse {
        return crossroadsService.getById(id)
    }

    @GetMapping("/me/crossroads")
    fun getMyCrossroads(authentication: Authentication): List<CrossroadsResponse> {
        val userId = authentication.principal as UUID
        return crossroadsService.getMyCrossroads(userId)
    }
}
