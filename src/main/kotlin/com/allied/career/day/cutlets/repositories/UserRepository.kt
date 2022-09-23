package com.allied.career.day.cutlets.repositories

import com.allied.career.day.cutlets.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByTelegramId(telegramId: Long) : Optional<User>
}
