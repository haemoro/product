package com.sotti.product.repository

import com.sotti.product.domain.AppUser
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository : MongoRepository<AppUser, String> {
    fun findByApiKey(apiKey: String): AppUser?

    fun findByDeviceId(deviceId: String): AppUser?

    fun findByActiveTrue(): List<AppUser>

    fun findAllByOrderByUpdatedAtDesc(): List<AppUser>
}
