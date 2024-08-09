package ru.vszholobov.weight_bot

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtils
import org.jfree.chart.JFreeChart
import org.jfree.chart.StandardChartTheme
import org.jfree.chart.renderer.category.BarRenderer
import org.jfree.chart.renderer.category.StandardBarPainter
import org.jfree.chart.renderer.xy.StandardXYBarPainter
import org.jfree.chart.ui.RectangleInsets
import org.jfree.data.category.DefaultCategoryDataset
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import java.awt.Color
import java.awt.Font
import java.io.ByteArrayInputStream
import java.io.File

private val logger = KotlinLogging.logger {}

@Component
class WeightBot(
    commands: Set<BotCommand>,
    @Value("\${telegram.token}")
    token: String,
) : TelegramLongPollingCommandBot(token) {

    init {
        registerAll(*commands.toTypedArray())
    }

    @Value("\${telegram.botName}")
    private val botName: String = ""

    override fun getBotUsername(): String = botName

    override fun processNonCommandUpdate(update: Update) {
        if (update.hasMessage()) {
            logger.info { "Получено сообщение $update" }
            val chatId = update.message.chatId.toString()

            if (update.message.hasDocument()) {
                val getFile = GetFile(update.message.document.fileId)
                val file = execute(getFile)
                downloadFile(file, File("./download/" + file.filePath))

                val defaultCategoryDataset = DefaultCategoryDataset()
                defaultCategoryDataset.addValue(400, "Вес", "Январь")
                defaultCategoryDataset.addValue(200, "Вес", "Февраль")
                defaultCategoryDataset.addValue(300, "Вес", "Март")
                defaultCategoryDataset.addValue(100, "Вес", "Апрель")
                val chart = ChartFactory.createBarChart(
                    "График веса",
                    "Месяц",
                    "Вес",
                    defaultCategoryDataset
                )

                val chartPng = generateChartPng(chart)
                val sendDocument = SendDocument()
                sendDocument.chatId = chatId
                sendDocument.document = chartPng

                execute(sendDocument)
                execute(createMessage(chatId, "Файл успешно загружен ${update.message.document.fileName}"))
            } else if (update.message.hasText()) {
                execute(createMessage(chatId, "Вы написали: *${update.message.text}*"))
            } else {
                execute(createMessage(chatId, "Я понимаю только текст!"))
            }
        }
    }

    private fun generateChartPng(chart: JFreeChart): InputFile {
        val fontName = "Lucida Sans"
        val theme: StandardChartTheme = StandardChartTheme.createJFreeTheme() as StandardChartTheme
        theme.largeFont = Font(fontName, Font.BOLD, 15) //axis-title
        theme.regularFont = Font(fontName, Font.PLAIN, 11)
        theme.extraLargeFont = Font(fontName, Font.PLAIN, 16) //title
        theme.axisOffset = RectangleInsets(0.0, 0.0, 0.0, 0.0)
        theme.barPainter = StandardBarPainter()
        theme.xyBarPainter = StandardXYBarPainter()

        theme.titlePaint = Color.decode("#4572a7")
        theme.rangeGridlinePaint = Color.decode("#C0C0C0")
        theme.axisLabelPaint = Color.decode("#666666")
        theme.plotBackgroundPaint = Color.white
        theme.chartBackgroundPaint = Color.white
        theme.gridBandPaint = Color.BLUE
        theme.apply(chart)

        val rend = chart.categoryPlot.renderer as BarRenderer
        rend.setShadowVisible(true)
        rend.shadowXOffset = 2.0
        rend.shadowYOffset = 0.0
        rend.maximumBarWidth = 0.1
        rend.shadowPaint = Color.decode("#C0C0C0")
        rend.setSeriesPaint(0, Color.BLUE)

        val png = ChartUtils.encodeAsPNG(chart.createBufferedImage(1000, 1000))
        val inputFile = InputFile(ByteArrayInputStream(png), "chart.png")
        return inputFile
    }

    fun createMessage(chatId: String, text: String) =
        SendMessage(chatId, text)
            .apply { enableMarkdown(true) }
            .apply { disableWebPagePreview() }
}