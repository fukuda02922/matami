package com.matamichi.dto

import com.matamichi.domain.Theme
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CrossroadsRequest(
    @field:NotNull
    val theme: Theme,

    @field:NotBlank
    @field:Size(max = 140)
    val body: String,

    @field:NotBlank
    @field:Size(max = 30)
    val choiceA: String,

    @field:NotBlank
    @field:Size(max = 30)
    val choiceB: String,

    @field:NotBlank
    val chosen: String,

    @field:Min(10)
    @field:Max(100)
    val ageAtTime: Int? = null,

    val afterText: String? = null,
)
