package com.allied.career.day.cutlets.handlers

import com.allied.career.day.cutlets.CutletsBot
import com.allied.career.day.cutlets.UpdateListener
import com.allied.career.day.cutlets.entities.Product
import com.allied.career.day.cutlets.repositories.ProductRepository
import com.allied.career.day.cutlets.util.Command
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.nio.file.Paths

@Component
class MenuMessageListener(
        private val productRepository: ProductRepository
) : UpdateListener {

    override fun processUpdate(update: Update, bot: CutletsBot, isCallback: Boolean, telegramId: Long, chatId: Long): Boolean {
        val isCallback = update.message == null && update.callbackQuery != null
        val telegramId = if (isCallback) update.callbackQuery.from.id else update.message.from.id
        val chatId = if (isCallback) update.callbackQuery.message.chatId else update.message.chatId
        if (isCallback || !Command.MENU.equals(update.message.text)) {
            return false
        }

        val output = SendMessage()
        output.setChatId(chatId)
        output.text = "Here is our menu:"
        bot.execute(output)

        productRepository.findAll().forEach {
            product -> bot.execute(prepareMenuItem(product, chatId))
        }

        return true
    }

    private fun prepareMenuItem(product: Product, chatId: Long): SendPhoto {
        val sendPhoto = SendPhoto()
        sendPhoto.setChatId(chatId)
        sendPhoto.photo = InputFile(Paths.get(product.photo).toFile())
        sendPhoto.caption = "${product.name} price - ${product.price}"
        val buyButton = InlineKeyboardButton.builder().text("add").callbackData("${product.id}").build()
        val inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(listOf(buyButton)).build()
        sendPhoto.replyMarkup = inlineKeyboardMarkup
        return sendPhoto
    }
}