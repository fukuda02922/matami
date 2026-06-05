package com.matamichi.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import com.matamichi.config.SecurityConfig

@WebMvcTest(HealthController::class)
@Import(SecurityConfig::class)
class HealthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `GET v1 health returns ok status and version`() {
        mockMvc.get("/v1/health")
            .andExpect {
                status { isOk() }
                content {
                    jsonPath("$.status") { value("ok") }
                    jsonPath("$.version") { value("1.0.0") }
                }
            }
    }

    @Test
    fun `GET v1 health is accessible without authentication`() {
        mockMvc.get("/v1/health")
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    @WithMockUser
    fun `GET v1 health returns ok when authenticated`() {
        mockMvc.get("/v1/health")
            .andExpect {
                status { isOk() }
                content {
                    json("""{"status":"ok","version":"1.0.0"}""")
                }
            }
    }
}
