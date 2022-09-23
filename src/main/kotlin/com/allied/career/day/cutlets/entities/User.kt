package com.allied.career.day.cutlets.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val telegramId: Long,
        val name: String,
        val phoneNumber: String
)
