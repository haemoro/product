package com.sotti.product.musictrack.service

import com.sotti.product.musictrack.domain.MusicTrack
import com.sotti.product.musictrack.domain.TrackStatus
import com.sotti.product.musictrack.dto.CheckTrackAnswerRequest
import com.sotti.product.musictrack.dto.CheckTrackAnswerResponse
import com.sotti.product.musictrack.dto.ChoiceItem
import com.sotti.product.musictrack.dto.QuestionResponse
import com.sotti.product.musictrack.dto.YoutubeInfo
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service

@Service
class MusicTrackGameService(
    private val mongoTemplate: MongoTemplate,
    private val questionTokenService: QuestionTokenService,
) {
    companion object {
        private const val PREVIEW_SECONDS = 5
        private const val TOTAL_CHOICES = 4
        private const val COLLECTION_NAME = "music_track"
    }

    fun generateQuestion(
        category: String?,
        difficulty: String?,
        excludeTrackIds: List<String>?,
    ): QuestionResponse {
        val correctTrack =
            sampleTracks(1, category, difficulty, excludeTrackIds).firstOrNull()
                ?: throw IllegalArgumentException("문제를 생성하기 위한 트랙이 부족합니다. 최소 ${TOTAL_CHOICES}개의 활성 트랙이 필요합니다.")

        val wrongTracks =
            sampleTracks(
                count = TOTAL_CHOICES - 1,
                category = category,
                difficulty = difficulty,
                excludeTrackIds = (excludeTrackIds.orEmpty()) + correctTrack.id!!,
            )

        val allChoices = (listOf(correctTrack) + wrongTracks)
        if (allChoices.size < TOTAL_CHOICES) {
            throw IllegalArgumentException("문제를 생성하기 위한 트랙이 부족합니다. 최소 ${TOTAL_CHOICES}개의 활성 트랙이 필요합니다.")
        }

        val shuffledChoices = allChoices.shuffled()
        val choiceIds = shuffledChoices.map { it.id!! }

        val token =
            questionTokenService.generate(
                correctTrackId = correctTrack.id,
                choiceTrackIds = choiceIds,
            )

        return QuestionResponse(
            questionToken = token,
            previewSeconds = PREVIEW_SECONDS,
            youtube =
                YoutubeInfo(
                    videoId = correctTrack.youtubeVideoId,
                    startSeconds = correctTrack.startSeconds,
                ),
            choices =
                shuffledChoices.map { track ->
                    ChoiceItem(
                        trackId = track.id!!,
                        thumbnailUrl = track.thumbnailUrl,
                    )
                },
        )
    }

    fun checkAnswer(request: CheckTrackAnswerRequest): CheckTrackAnswerResponse {
        val payload = questionTokenService.decode(request.questionToken)

        require(request.selectedTrackId in payload.choiceTrackIds) {
            "선택한 트랙이 보기에 포함되지 않습니다."
        }

        return CheckTrackAnswerResponse(
            isCorrect = request.selectedTrackId == payload.correctTrackId,
            correctTrackId = payload.correctTrackId,
        )
    }

    private fun sampleTracks(
        count: Int,
        category: String?,
        difficulty: String?,
        excludeTrackIds: List<String>?,
    ): List<MusicTrack> {
        val criteria = Criteria.where("status").`is`(TrackStatus.ACTIVE.name)

        if (!category.isNullOrBlank()) {
            criteria.and("category").`is`(category)
        }
        if (!difficulty.isNullOrBlank()) {
            criteria.and("difficulty").`is`(difficulty)
        }
        if (!excludeTrackIds.isNullOrEmpty()) {
            criteria.and("_id").nin(excludeTrackIds)
        }

        val aggregation =
            Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sample(count.toLong()),
            )

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, MusicTrack::class.java).mappedResults
    }
}
