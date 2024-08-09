package ru.vszholobov.weight_bot

import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class StartCommand : BotCommand(CommandName.START.text, "") {

    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        absSender.execute(createMessage(chat.id.toString(), "Добро пожаловать!"))
    }

    fun createMessage(chatId: String, text: String) =
        SendMessage(chatId, text)
            .apply { enableMarkdown(true) }
            .apply { disableWebPagePreview() }
}