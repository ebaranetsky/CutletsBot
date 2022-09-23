package com.allied.career.day.cutlets.repositories

import com.allied.career.day.cutlets.entities.Product
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DbInitializer(
        private val productRepository: ProductRepository
) {

    @PostConstruct
    private fun init() {
        val products = productRepository.findAll()
        if (products.isNotEmpty()) {
            return
        }

        productRepository.save(Product(name = "cutlets", photo = "photo/cutlets.jpg", price = 100))
        productRepository.save(Product(name = "juice", photo = "photo/juice.jpg", price = 20))
    }
}