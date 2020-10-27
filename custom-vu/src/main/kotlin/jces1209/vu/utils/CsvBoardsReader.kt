package jces1209.vu.utils

import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.cloud.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.WebDriver
import java.lang.IllegalStateException
import java.net.URI

class CsvBoardsReader (
    val driver: WebDriver,
    private val logger: Logger = LogManager.getLogger()
) {
    private val csvPropertyFile = "BoardUsageFrequency.csv"

    fun getBoardsListFromCsv(): MutableList<BoardPage>? {
        val boardsFromCsv = readFromCsv()
        if (boardsFromCsv.size > 0) {
            return generateBoardsFromCsv(boardsFromCsv)
        } else {
            logger.debug("I cannot parse boards list from csv")
            println("I cannot parse boards list from csv")
            return null
        }
    }

    private fun readFromCsv(): MutableList<CsvBoard> {
        val boardsFromCsv = mutableListOf<CsvBoard>()
        var csvContent = String()
        try {
            csvContent = this::class.java.getResource("/$csvPropertyFile").readText()
        } catch (exc: IllegalStateException) {
            logger.debug("The csv file was not found, skippping")
        }
        if (csvContent.isNotEmpty()) {
            readCsvContentByLine(csvContent, boardsFromCsv)
        }
        return boardsFromCsv
    }

    private fun readCsvContentByLine(csvContent: String, boardsList: MutableList<CsvBoard>)  {
        val csvLines = csvContent.split("\n").toTypedArray()
        for (line in csvLines) {
            if (line.isNotEmpty()) {
                val parsedList = line.split(",").toTypedArray()
                val id = parsedList.get(0)
                val frequency = parsedList.get(1).toFloat()
                val uri = URI(parsedList.get(2))
                val type = parsedList.get(3).toLowerCase()
                val csvBoard = CsvBoard(id, frequency, uri, type)
                boardsList.add(csvBoard)
            } else {
                logger.debug("I cannot parse the csv line, skipping...")
            }
        }
    }

    private fun generateBoardsFromCsv(boardsList: MutableList<CsvBoard>): MutableList<BoardPage> {
        val boardPages = mutableListOf<BoardPage>()
        for (csvBoard in boardsList) {
            with(csvBoard.boardType) {
                when {
                    contains("backlog") -> boardPages.add(CloudScrumBacklogPage(driver, csvBoard.uri))
                    contains("sprint") -> boardPages.add(CloudScrumSprintPage(driver, csvBoard.uri))
                    contains("kanban") -> boardPages.add(CloudKanbanBoardPage(driver, csvBoard.uri))
                    contains("next") -> boardPages.add(CloudNextGenBoardPage(driver, csvBoard.uri))
                    else -> logger.debug("I cannot parse the board")
                }
            }
        }
        return boardPages
    }
}
