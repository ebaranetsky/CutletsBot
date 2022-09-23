package com.allied.career.day.cutlets

import org.telegram.telegrambots.meta.api.objects.Update

interface UpdateListener {
    fun processUpdate(update: Update, bot: CutletsBot, isCallback: Boolean, telegramId: Long, chatId: Long) : Boolean
}