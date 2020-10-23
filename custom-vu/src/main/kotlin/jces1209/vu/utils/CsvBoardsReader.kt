package jces1209.vu.utils

import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.cloud.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.WebDriver
import java.io.File
import java.net.URI

class CsvBoardsReader (
    val driver: WebDriver,
    private val logger: Logger = LogManager.getLogger()
) {
    private val csvPath = "usage-frequency/BoardUsageFrequency.csv"

    public fun getBoardsListFromCsv(): MutableList<BoardPage>? {
        val boardsFromCsv = readFromCsv()
        if (boardsFromCsv.size > 0) {
            return generateBoardsFromCsv(boardsFromCsv)
        } else {
            logger.debug("I cannot parse boards list from csv")
            return null
        }
    }

    private fun readFromCsv(): MutableList<CsvBoard> {
        val boardsFromCsv = mutableListOf<CsvBoard>()
        readCsvByLine(csvPath, boardsFromCsv)
        return boardsFromCsv
    }

    private fun readCsvByLine(filename: String, boardsList: MutableList<CsvBoard>) = File(filename).forEachLine { it ->
        val parsedList = it.split(",").toTypedArray()
        val id = parsedList.get(0)
        val frequency = parsedList.get(1).toFloat()
        val uri = URI(parsedList.get(2))
        val type = parsedList.get(3).toLowerCase()
        val csvBoard = CsvBoard(id, frequency, uri, type)
        boardsList.add(csvBoard)
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
