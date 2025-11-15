package com.sotti.product.repository

import com.sotti.product.domain.Category
import com.sotti.product.domain.MusicQuiz
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MusicQuizRepository : MongoRepository<MusicQuiz, String> {
    // 카테고리별 조회
    fun findByCategory(category: Category): List<MusicQuiz>

    // 제목으로 검색
    fun findByTitleContainingIgnoreCase(title: String): List<MusicQuiz>
}
