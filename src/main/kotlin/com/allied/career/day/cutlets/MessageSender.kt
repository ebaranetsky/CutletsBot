package com.allied.career.day.cutlets

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import java.io.Serializable
import java.util.*

interface MessageSender {
    fun <T : Serializable, Method : BotApiMethod<T>> send(method: Method) : Optional<T>
}
