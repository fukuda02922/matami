package com.matamichi.dto

data class PagedResponse<T>(
    val items: List<T>,
    val nextCursor: String?,
)
