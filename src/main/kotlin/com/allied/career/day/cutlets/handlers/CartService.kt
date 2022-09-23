package com.allied.career.day.cutlets.handlers

import com.allied.career.day.cutlets.CutletsBot
import com.allied.career.day.cutlets.UpdateListener
import com.allied.career.day.cutlets.entities.Product
import com.allied.career.day.cutlets.repositories.ProductRepository
import com.allied.career.day.cutlets.util.Command
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.Timer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Component
class CartService(
        private val productRepository: ProductRepository
) : UpdateListener {

    private val sheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val cartCache: MutableMap<Long, MutableMap<Long, Int>> = HashMap()

    override fun processUpdate(update: Update, bot: CutletsBot, isCallback: Boolean, telegramId: Long, chatId: Long): Boolean {
        if (!isCallback && !Command.SHOW_CART.equals(update.message.text)) {
            return false
        }

        if (!isCallback) {
            printUserCart(bot, telegramId, chatId)
            return true
        }

        val productId: Long = update.callbackQuery.data.toLong()
        val userCart = cartCache.computeIfAbsent(telegramId) { mutableMapOf() }
        val productCount = userCart.getOrDefault(productId, 0)
        userCart[productId] = productCount.inc()

        val output = SendMessage()
        output.setChatId(chatId)
        output.text = "Added product to cart"
        val response = bot.execute(output)
        val answerCallbackQuery = AnswerCallbackQuery()
        answerCallbackQuery.callbackQueryId = update.callbackQuery.id
        bot.execute(answerCallbackQuery)

        sheduler.schedule({
            val deleteMessage = DeleteMessage()
            deleteMessage.setChatId(chatId)
            deleteMessage.messageId = response.messageId
            bot.execute(deleteMessage)
        }, 1, TimeUnit.SECONDS)

        return true
    }

    fun getUserCartProducts(telegramId: Long): Map<Product, Int> {
        if (!cartCache.containsKey(telegramId)) {
            return emptyMap()
        }
        val products = mutableMapOf<Product, Int>()
        cartCache[telegramId]!!.forEach { (productId, count) ->
            productRepository.findById(productId).ifPresent { product -> products[product] = count }
        }
        return products
    }

    fun printUserCart(bot: CutletsBot, telegramId: Long, chatId: Long) {
        val userCart = cartCache[telegramId]
        if (userCart != null) {
            val strBuilder = java.lang.StringBuilder()
            strBuilder.append("Your cart:\n")
            var totalPrice = 0L
            userCart.forEach { productInfo ->
                run {
                    val productOp = productRepository.findById(productInfo.key)
                    productOp.ifPresent { product ->
                        run {
                            val price = productInfo.value * product.price
                            strBuilder.append("${product.name}, count: ${productInfo.value}, price: $price\n")
                            totalPrice += price
                        }
                    }
                }
            }
            strBuilder.append("Total price = $totalPrice")
            val output = SendMessage()
            output.text = strBuilder.toString()
            output.setChatId(chatId)
            bot.execute(output)
        }
    }
}