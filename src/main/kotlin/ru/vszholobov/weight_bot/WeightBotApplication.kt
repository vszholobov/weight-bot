package ru.vszholobov.weight_bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WeightBotApplication

fun main(args: Array<String>) {
	runApplication<WeightBotApplication>(*args)
}
