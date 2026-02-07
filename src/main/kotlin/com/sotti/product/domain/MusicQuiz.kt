package com.sotti.product.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "music_quiz")
data class MusicQuiz(
    @Id
    val id: String? = null,
    val musicUrl: String,
    val answer: String,
    val imageUrl: String? = null,
    val title: String,
    @Indexed
    val category: Category,
)

enum class Category {
    NURSERY_RHYME, // 동요
    ANIMATION_OST, // 애니메이션 OST
    DISNEY, // 디즈니
    KIDS_POP, // 키즈 팝
    EDUCATION, // 교육용 노래
    CHARACTER_SONG, // 캐릭터 송 (뽀로로, 타요 등)
}
