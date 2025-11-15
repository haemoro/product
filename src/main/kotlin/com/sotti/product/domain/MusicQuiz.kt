package com.sotti.product.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "music_quiz")
data class MusicQuiz(
    @Id
    val id: String? = null,
    // 핵심 필드
    val musicUrl: String, // 음원 URL
    val answer: String, // 정답 텍스트
    val imageUrl: String? = null, // 정답 이미지 URL (앨범커버, 캐릭터 등)
    // 게임 정보
    val title: String, // 문제 제목
    val category: Category, // 카테고리 (동요, 애니OST 등)
)

enum class Category {
    NURSERY_RHYME, // 동요
    ANIMATION_OST, // 애니메이션 OST
    DISNEY, // 디즈니
    KIDS_POP, // 키즈 팝
    EDUCATION, // 교육용 노래
    CHARACTER_SONG, // 캐릭터 송 (뽀로로, 타요 등)
}
