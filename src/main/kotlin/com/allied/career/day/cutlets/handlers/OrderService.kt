package com.allied.career.day.cutlets.handlers

import com.allied.career.day.cutlets.CutletsBot
import com.allied.career.day.cutlets.UpdateListener
import com.allied.career.day.cutlets.entities.Order
import com.allied.career.day.cutlets.entities.User
import com.allied.career.day.cutlets.repositories.OrderRepository
import com.allied.career.day.cutlets.repositories.UserRepository
import com.allied.career.day.cutlets.util.Command
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

@Component
class OrderService(
        private val userRepository: UserRepository,
        private val orderRepository: OrderRepository,
        private val cartService: CartService
) : UpdateListener {

    override fun processUpdate(update: Update, bot: CutletsBot, isCallback: Boolean, telegramId: Long, chatId: Long): Boolean {
        if (!Command.ORDER.equals(update.message.text) && update.message.contact == null) {
            return false
        }

        if (update.message.contact != null) {
            val username = update.message.from.firstName + " " + update.message.from.lastName + " (${update.message.from.userName})"
            userRepository.save(
                    User(
                            name = username.trim(),
                            phoneNumber = update.message.contact.phoneNumber,
                            telegramId = telegramId,
                    ))
        }

        val userOp = userRepository.findByTelegramId(telegramId)
        if (userOp.isEmpty) {
            val output = SendMessage()
            output.text = "In order to continue we need your phone number, please share it"
            output.setChatId(chatId)
            requestPhoneNumber(output)
            bot.execute(output)
            return true
        }

        val userProducts = cartService.getUserCartProducts(telegramId)
        if (userProducts.isEmpty()) {
            val output = SendMessage()
            output.text = "Sorry your cart is empty, please go to /menu to choose products"
            output.setChatId(chatId)
            bot.execute(output)
            return true
        }

        val lastOrderOp = orderRepository.findTopByOrderByIdDesc()
        val userOrderId = if (lastOrderOp.isPresent) lastOrderOp.get().userOrderId + 1 else 1

        userProducts.forEach {
            (product, count) ->
            run {
                orderRepository.save(Order(user = userOp.get(), product = product, productCount = count,
                        userOrderId = userOrderId))
            }
        }

        cartService.printUserCart(bot, telegramId, chatId)

        val output = SendMessage()
        output.setChatId(chatId)
        output.text = "Thank you for the order, our operator will call you soon for confirmation!"
        bot.execute(output)

        return true
    }

    private fun requestPhoneNumber(output: SendMessage) {
        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        output.replyMarkup = replyKeyboardMarkup
        replyKeyboardMarkup.selective = true
        replyKeyboardMarkup.resizeKeyboard = true
        replyKeyboardMarkup.oneTimeKeyboard = true

        // new list
        val keyboard: MutableList<KeyboardRow> = ArrayList()

        // first keyboard line
        val keyboardFirstRow = KeyboardRow()
        val keyboardButton = KeyboardButton()
        keyboardButton.text = "Share your number >"
        keyboardButton.requestContact = true
        keyboardFirstRow.add(keyboardButton)

        // add array to list
        keyboard.add(keyboardFirstRow)

        // add list to our keyboard
        replyKeyboardMarkup.keyboard = keyboard
    }
}