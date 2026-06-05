package com.matamichi.service

import com.matamichi.domain.Crossroads
import com.matamichi.domain.Theme
import com.matamichi.dto.CrossroadsRequest
import com.matamichi.dto.CrossroadsResponse
import com.matamichi.dto.PagedResponse
import com.matamichi.exception.ResourceNotFoundException
import com.matamichi.repository.CrossroadsRepository
import com.matamichi.repository.ResonanceRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Base64
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CrossroadsService(
    private val crossroadsRepository: CrossroadsRepository,
    private val resonanceRepository: ResonanceRepository,
) {

    fun getFeed(theme: Theme?, cursor: String?, limit: Int): PagedResponse<CrossroadsResponse> {
        val pageable = PageRequest.of(0, limit + 1, Sort.by(Sort.Direction.DESC, "createdAt", "id"))

        val page = if (theme != null) {
            crossroadsRepository.findByTheme(theme, pageable)
        } else {
            crossroadsRepository.findAll(pageable)
        }

        val items = page.content
        // cursor-based filtering: skip items older-or-equal to cursor position
        val filtered = if (cursor != null) {
            val (cursorAt, cursorId) = decodeCursor(cursor)
            items.filter { it.createdAt < cursorAt || (it.createdAt == cursorAt && it.id.toString() < cursorId) }
        } else {
            items
        }

        val hasMore = filtered.size > limit
        val result = if (hasMore) filtered.dropLast(1) else filtered

        val nextCursor = if (hasMore) {
            val last = result.last()
            encodeCursor(last.createdAt, last.id)
        } else null

        return PagedResponse(
            items = result.map { it.toResponse() },
            nextCursor = nextCursor,
        )
    }

    fun getById(id: UUID): CrossroadsResponse {
        val crossroads = crossroadsRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Crossroads not found: $id") }
        return crossroads.toResponse()
    }

    @Transactional
    fun create(userId: UUID, request: CrossroadsRequest): CrossroadsResponse {
        val crossroads = Crossroads(
            userId = userId,
            theme = request.theme,
            body = request.body,
            choiceA = request.choiceA,
            choiceB = request.choiceB,
            chosen = request.chosen,
            ageAtTime = request.ageAtTime,
        )
        return crossroadsRepository.save(crossroads).toResponse()
    }

    fun getMyCrossroads(userId: UUID): List<CrossroadsResponse> {
        return crossroadsRepository.findByUserId(userId).map { it.toResponse() }
    }

    private fun Crossroads.toResponse(): CrossroadsResponse {
        val countA = resonanceRepository.countByCrossroadsIdAndChosen(id, "A")
        val countB = resonanceRepository.countByCrossroadsIdAndChosen(id, "B")
        return CrossroadsResponse(
            id = id,
            theme = theme,
            body = body,
            choiceA = choiceA,
            choiceB = choiceB,
            chosen = chosen,
            ageAtTime = ageAtTime,
            countA = countA,
            countB = countB,
            resonanceCount = countA + countB,
            createdAt = createdAt,
        )
    }

    private fun encodeCursor(createdAt: Instant, id: UUID): String {
        val raw = "${createdAt.epochSecond}.${createdAt.nano}:$id"
        return Base64.getUrlEncoder().encodeToString(raw.toByteArray())
    }

    private fun decodeCursor(cursor: String): Pair<Instant, String> {
        val raw = String(Base64.getUrlDecoder().decode(cursor))
        val colonIdx = raw.indexOf(':')
        val tsPart = raw.substring(0, colonIdx)
        val id = raw.substring(colonIdx + 1)
        val dotIdx = tsPart.indexOf('.')
        val epochSecond = tsPart.substring(0, dotIdx).toLong()
        val nano = tsPart.substring(dotIdx + 1).toInt()
        return Pair(Instant.ofEpochSecond(epochSecond, nano.toLong()), id)
    }
}
