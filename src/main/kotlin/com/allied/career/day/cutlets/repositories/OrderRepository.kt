package com.allied.career.day.cutlets.repositories

import com.allied.career.day.cutlets.entities.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderRepository : JpaRepository<Order, Long> {
    fun findTopByOrderByIdDesc() : Optional<Order>
}