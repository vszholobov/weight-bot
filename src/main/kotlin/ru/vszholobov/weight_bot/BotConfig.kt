package ru.vszholobov.weight_bot

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class BotConfig {
    @Bean
    fun telegramBotsApi(bot: WeightBot): TelegramBotsApi =
        TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(bot)
        }
}