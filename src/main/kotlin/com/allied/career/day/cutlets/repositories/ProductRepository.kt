package com.allied.career.day.cutlets.repositories

import com.allied.career.day.cutlets.entities.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>
