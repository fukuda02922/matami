package com.matamichi.service

import com.matamichi.domain.Crossroads
import com.matamichi.domain.Theme
import com.matamichi.dto.CrossroadsRequest
import com.matamichi.exception.ResourceNotFoundException
import com.matamichi.repository.CrossroadsRepository
import com.matamichi.repository.ResonanceRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.Optional
import java.util.UUID

class CrossroadsServiceTest {

    private val crossroadsRepository: CrossroadsRepository = mockk()
    private val resonanceRepository: ResonanceRepository = mockk()
    private val service = CrossroadsService(crossroadsRepository, resonanceRepository)

    private fun crossroads(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        theme: Theme = Theme.CAREER,
        createdAt: Instant = Instant.now(),
    ) = Crossroads(
        id = id,
        userId = userId,
        theme = theme,
        body = "転職するか悩んだ",
        choiceA = "転職する",
        choiceB = "残る",
        chosen = "A",
        ageAtTime = 28,
        createdAt = createdAt,
    )

    private fun stubCounts(id: UUID, countA: Long = 3, countB: Long = 2) {
        every { resonanceRepository.countByCrossroadsIdAndChosen(id, "A") } returns countA
        every { resonanceRepository.countByCrossroadsIdAndChosen(id, "B") } returns countB
    }

    @Test
    @DisplayName("テーマフィルター指定時は対象のみ返す")
    fun getFeed_withTheme_returnsFilteredItems() {
        val c = crossroads(theme = Theme.CAREER)
        stubCounts(c.id)
        every { crossroadsRepository.findByTheme(Theme.CAREER, any()) } returns PageImpl(listOf(c))

        val result = service.getFeed(Theme.CAREER, null, 20)

        assertEquals(1, result.items.size)
        assertEquals(Theme.CAREER, result.items[0].theme)
    }

    @Test
    @DisplayName("テーマ未指定時は全件返す")
    fun getFeed_withoutTheme_returnsAllItems() {
        val c1 = crossroads()
        val c2 = crossroads(theme = Theme.LOVE)
        stubCounts(c1.id)
        stubCounts(c2.id)
        every { crossroadsRepository.findAll(any<Pageable>()) } returns PageImpl(listOf(c1, c2))

        val result = service.getFeed(null, null, 20)

        assertEquals(2, result.items.size)
    }

    @Test
    @DisplayName("limit+1件取得時に nextCursor が設定される")
    fun getFeed_withMoreThanLimit_setsNextCursor() {
        // limit=2 で3件返ってきた場合、hasMore=true になり nextCursor が設定される
        val items = (1..3).map { crossroads(createdAt = Instant.ofEpochSecond(1000L - it)) }
        items.forEach { stubCounts(it.id) }
        every { crossroadsRepository.findAll(any<Pageable>()) } returns PageImpl(items)

        val result = service.getFeed(null, null, 2)

        assertEquals(2, result.items.size)
        assertNotNull(result.nextCursor)
    }

    @Test
    @DisplayName("最終ページでは nextCursor が null")
    fun getFeed_lastPage_nextCursorIsNull() {
        val c = crossroads()
        stubCounts(c.id)
        every { crossroadsRepository.findAll(any<Pageable>()) } returns PageImpl(listOf(c))

        val result = service.getFeed(null, null, 20)

        assertNull(result.nextCursor)
    }

    @Test
    @DisplayName("getById 存在する ID で CrossroadsResponse を返す")
    fun getById_existingId_returnsCrossroadsResponse() {
        val c = crossroads()
        stubCounts(c.id)
        every { crossroadsRepository.findById(c.id) } returns Optional.of(c)

        val result = service.getById(c.id)

        assertEquals(c.id, result.id)
        assertEquals(3L, result.countA)
        assertEquals(2L, result.countB)
        assertEquals(5L, result.resonanceCount)
    }

    @Test
    @DisplayName("getById 存在しない ID で ResourceNotFoundException をスローする")
    fun getById_nonExistentId_throwsResourceNotFoundException() {
        val id = UUID.randomUUID()
        every { crossroadsRepository.findById(id) } returns Optional.empty()

        assertThrows<ResourceNotFoundException> {
            service.getById(id)
        }
    }

    @Test
    @DisplayName("create でリポジトリに保存され CrossroadsResponse を返す")
    fun create_validRequest_savesAndReturnsCrossroadsResponse() {
        val userId = UUID.randomUUID()
        val request = CrossroadsRequest(
            theme = Theme.CAREER,
            body = "転職すべきか",
            choiceA = "転職",
            choiceB = "残留",
            chosen = "A",
            ageAtTime = 30,
        )
        val saved = crossroads(userId = userId, theme = Theme.CAREER)
        every { crossroadsRepository.save(any()) } returns saved
        stubCounts(saved.id, 0, 0)

        val result = service.create(userId, request)

        assertEquals(saved.id, result.id)
        assertEquals(Theme.CAREER, result.theme)
        verify { crossroadsRepository.save(any()) }
    }

    @Test
    @DisplayName("getMyCrossroads はユーザーの投稿一覧を返す")
    fun getMyCrossroads_returnsUserCrossroadsList() {
        val userId = UUID.randomUUID()
        val c1 = crossroads(userId = userId)
        val c2 = crossroads(userId = userId)
        stubCounts(c1.id)
        stubCounts(c2.id)
        every { crossroadsRepository.findByUserId(userId) } returns listOf(c1, c2)

        val result = service.getMyCrossroads(userId)

        assertEquals(2, result.size)
    }
}
