package com.allied.career.day.cutlets

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.annotation.PostConstruct

@Component
class BotStarter(
        private val cutletsBot: CutletsBot
) {

    @PostConstruct
    private fun init() {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        telegramBotsApi.registerBot(cutletsBot)
    }
}