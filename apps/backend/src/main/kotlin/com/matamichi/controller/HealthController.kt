package com.matamichi.controller

import com.matamichi.dto.HealthResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class HealthController {

    @GetMapping("/health")
    fun health(): ResponseEntity<HealthResponse> =
        ResponseEntity.ok(HealthResponse(status = "ok", version = "1.0.0"))
}
