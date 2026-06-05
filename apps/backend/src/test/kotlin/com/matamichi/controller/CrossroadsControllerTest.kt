package com.matamichi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.matamichi.config.SecurityConfig
import com.matamichi.domain.Theme
import com.matamichi.dto.CrossroadsRequest
import com.matamichi.dto.CrossroadsResponse
import com.matamichi.dto.PagedResponse
import com.matamichi.security.JwtAuthFilter
import com.matamichi.service.CrossroadsService
import io.mockk.every
import io.mockk.mockk
import com.matamichi.security.WithMockUuidUser
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant
import java.util.UUID

@WebMvcTest(CrossroadsController::class)
@Import(SecurityConfig::class, CrossroadsControllerTest.MockConfig::class)
class CrossroadsControllerTest {

    @TestConfiguration
    class MockConfig {
        @Bean
        fun crossroadsService(): CrossroadsService = mockk()

        @Bean
        fun jwtAuthFilter(): JwtAuthFilter = JwtAuthFilter("")
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var crossroadsService: CrossroadsService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private fun sampleResponse(id: UUID = UUID.randomUUID()) = CrossroadsResponse(
        id = id,
        theme = Theme.CAREER,
        body = "転職すべきか悩んだ",
        choiceA = "転職する",
        choiceB = "残る",
        chosen = "A",
        ageAtTime = 28,
        countA = 5,
        countB = 3,
        resonanceCount = 8,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
    )

    @Test
    @DisplayName("GET /v1/crossroads は認証なしでフィードを返す")
    fun getFeed_withoutAuth_returnsOk() {
        val response = PagedResponse(items = listOf(sampleResponse()), nextCursor = null)
        every { crossroadsService.getFeed(null, null, 20) } returns response

        mockMvc.get("/v1/crossroads")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.items") { isArray() }
                jsonPath("$.items.length()") { value(1) }
            }
    }

    @Test
    @DisplayName("テーマフィルター指定でサービスに伝わる")
    fun getFeed_withThemeFilter_passesThemeToService() {
        val response = PagedResponse(items = listOf(sampleResponse()), nextCursor = null)
        every { crossroadsService.getFeed(Theme.CAREER, null, 20) } returns response

        mockMvc.get("/v1/crossroads?theme=CAREER")
            .andExpect {
                status { isOk() }
                jsonPath("$.items[0].theme") { value("CAREER") }
            }
    }

    @Test
    @DisplayName("カーソルと件数指定が機能する")
    fun getFeed_withCursorAndLimit_returnsOk() {
        val cursor = "dGVzdA=="
        val response = PagedResponse(items = emptyList<CrossroadsResponse>(), nextCursor = null)
        every { crossroadsService.getFeed(null, cursor, 5) } returns response

        mockMvc.get("/v1/crossroads?cursor=$cursor&limit=5")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    @DisplayName("GET /v1/crossroads/{id} は認証なしで詳細を返す")
    fun getById_withoutAuth_returnsOk() {
        val id = UUID.randomUUID()
        every { crossroadsService.getById(id) } returns sampleResponse(id)

        mockMvc.get("/v1/crossroads/$id")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(id.toString()) }
                jsonPath("$.body") { value("転職すべきか悩んだ") }
            }
    }

    @Test
    @DisplayName("存在しない ID は 404 を返す")
    fun getById_nonExistent_returns404() {
        val id = UUID.randomUUID()
        every { crossroadsService.getById(id) } throws
            com.matamichi.exception.ResourceNotFoundException("Not found")

        mockMvc.get("/v1/crossroads/$id")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @DisplayName("POST /v1/crossroads 認証なしは 403")
    fun createCrossroads_withoutAuth_returns403() {
        val request = CrossroadsRequest(
            theme = Theme.CAREER,
            body = "転職すべきか",
            choiceA = "転職",
            choiceB = "残留",
            chosen = "A",
        )

        mockMvc.post("/v1/crossroads") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isForbidden() }
        }
    }

    @Test
    @WithMockUuidUser
    @DisplayName("POST /v1/crossroads 認証済みで投稿が成功する")
    fun createCrossroads_withAuth_returns201() {
        val id = UUID.randomUUID()
        val request = CrossroadsRequest(
            theme = Theme.CAREER,
            body = "転職すべきか悩んだ経緯を書く",
            choiceA = "転職する",
            choiceB = "残留する",
            chosen = "A",
            ageAtTime = 30,
        )
        every { crossroadsService.create(any(), any()) } returns sampleResponse(id)

        mockMvc.post("/v1/crossroads") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            with(csrf())
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(id.toString()) }
        }
    }

    @Test
    @WithMockUuidUser
    @DisplayName("バリデーションエラーは 400 を返す")
    fun createCrossroads_validationError_returns400() {
        val invalidRequest = mapOf(
            "theme" to "CAREER",
            "body" to "x".repeat(141), // 140文字超過
            "choiceA" to "A",
            "choiceB" to "B",
            "chosen" to "A",
        )

        mockMvc.post("/v1/crossroads") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidRequest)
            with(csrf())
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    @DisplayName("GET /v1/me/crossroads 認証なしは 403")
    fun getMyCrossroads_withoutAuth_returns403() {
        mockMvc.get("/v1/me/crossroads")
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    @WithMockUuidUser
    @DisplayName("GET /v1/me/crossroads 認証済みでマイ岐路一覧を返す")
    fun getMyCrossroads_withAuth_returnsOk() {
        every { crossroadsService.getMyCrossroads(any()) } returns listOf(sampleResponse())

        mockMvc.get("/v1/me/crossroads")
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                jsonPath("$.length()") { value(1) }
            }
    }
}
