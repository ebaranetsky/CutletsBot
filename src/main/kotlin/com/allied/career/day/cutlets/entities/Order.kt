package com.allied.career.day.cutlets.entities

import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "orders")
data class Order(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        val userOrderId: Long,

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

        @ManyToOne
        @JoinColumn(name = "product_id", nullable = false)
        val product: Product,

        val productCount: Int,

        @CreationTimestamp
        val timestamp: Timestamp = Timestamp.from(Instant.now())
)
