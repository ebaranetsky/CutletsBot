package com.allied.career.day.cutlets

import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.Serializable
import java.util.*

@Component
class CutletsBot(
        private val updateListeners: List<UpdateListener>
) : TelegramLongPollingBot() {
    override fun getBotToken(): String {
        return "5743892381:AAEZ2Lt0YtGrRyV7pK7lLSLjDgiOlR3NDdw"
    }

    override fun getBotUsername(): String {
        return "ed_test_quest_hola_bot"
    }

    override fun onUpdateReceived(update: Update) {
        val isCallback = update.message == null && update.callbackQuery != null
        val telegramId = if (isCallback) update.callbackQuery.from.id else update.message.from.id
        val chatId = if (isCallback) update.callbackQuery.message.chatId else update.message.chatId
        updateListeners.forEach { listener ->
            if (listener.processUpdate(
                            update = update, bot = this,
                            isCallback = isCallback,
                            telegramId = telegramId,
                            chatId = chatId)) return
        }
    }
}