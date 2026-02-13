package com.sotti.product.musictrack.service

import com.sotti.product.musictrack.domain.MusicTrack
import com.sotti.product.musictrack.domain.TrackStatus
import com.sotti.product.musictrack.dto.CreateMusicTrackRequest
import com.sotti.product.musictrack.dto.MusicTrackResponse
import com.sotti.product.musictrack.dto.UpdateMusicTrackRequest
import com.sotti.product.musictrack.repository.MusicTrackRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MusicTrackService(
    private val musicTrackRepository: MusicTrackRepository,
) {
    companion object {
        private const val THUMBNAIL_URL_TEMPLATE = "https://img.youtube.com/vi/%s/hqdefault.jpg"

        fun thumbnailUrlOf(youtubeVideoId: String): String = THUMBNAIL_URL_TEMPLATE.format(youtubeVideoId)
    }

    @Transactional
    fun createTrack(request: CreateMusicTrackRequest): MusicTrackResponse {
        require(!musicTrackRepository.existsByYoutubeVideoId(request.youtubeVideoId)) {
            "이미 등록된 YouTube 동영상입니다."
        }

        val track =
            MusicTrack(
                youtubeVideoId = request.youtubeVideoId,
                startSeconds = request.startSeconds,
                thumbnailUrl = thumbnailUrlOf(request.youtubeVideoId),
                title = request.title,
                imageUrl = request.imageUrl,
                category = request.category,
                difficulty = request.difficulty,
            )
        val saved = musicTrackRepository.save(track)
        return MusicTrackResponse.from(saved)
    }

    fun getTracks(
        status: TrackStatus?,
        category: String?,
    ): List<MusicTrackResponse> {
        val tracks =
            when {
                status != null && !category.isNullOrBlank() ->
                    musicTrackRepository.findAllByStatusAndCategoryContainingIgnoreCase(status, category)
                status != null ->
                    musicTrackRepository.findAllByStatus(status)
                else ->
                    musicTrackRepository.findAll()
            }
        return tracks.map { MusicTrackResponse.from(it) }
    }

    @Transactional
    fun updateTrack(
        trackId: String,
        request: UpdateMusicTrackRequest,
    ): MusicTrackResponse {
        val track =
            musicTrackRepository.findById(trackId).orElseThrow {
                NoSuchElementException("트랙을 찾을 수 없습니다. ID: $trackId")
            }

        if (request.youtubeVideoId != null && request.youtubeVideoId != track.youtubeVideoId) {
            require(!musicTrackRepository.existsByYoutubeVideoId(request.youtubeVideoId)) {
                "이미 등록된 YouTube 동영상입니다."
            }
        }

        val newVideoId = request.youtubeVideoId ?: track.youtubeVideoId
        val updated =
            track.copy(
                youtubeVideoId = newVideoId,
                startSeconds = request.startSeconds ?: track.startSeconds,
                thumbnailUrl = request.thumbnailUrl ?: thumbnailUrlOf(newVideoId),
                status = request.status ?: track.status,
                title = request.title ?: track.title,
                imageUrl = request.imageUrl ?: track.imageUrl,
                category = request.category ?: track.category,
                difficulty = request.difficulty ?: track.difficulty,
            )
        val saved = musicTrackRepository.save(updated)
        return MusicTrackResponse.from(saved)
    }
}
