package com.sotti.product.musictrack.repository

import com.sotti.product.musictrack.domain.MusicTrack
import com.sotti.product.musictrack.domain.TrackStatus
import org.springframework.data.mongodb.repository.MongoRepository

interface MusicTrackRepository : MongoRepository<MusicTrack, String> {
    fun existsByYoutubeVideoId(youtubeVideoId: String): Boolean

    fun findAllByStatusAndCategoryContainingIgnoreCase(
        status: TrackStatus,
        category: String,
    ): List<MusicTrack>

    fun findAllByStatus(status: TrackStatus): List<MusicTrack>
}
