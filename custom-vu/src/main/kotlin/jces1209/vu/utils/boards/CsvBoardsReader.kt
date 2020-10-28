package jces1209.vu.utils.boards

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.lang.IllegalStateException
import java.net.URI

object CsvBoardsReader {

    var boardsList: MutableList<CsvBoard>?
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val csvPropertyFile = "BoardUsageFrequency.csv"

    init {
        this.boardsList = getBoardsFromCsv()
    }

    fun getBoardsFromCsv(): MutableList<CsvBoard>? {
        val csvBoardsList = readBoardsFromCsv()
        if (csvBoardsList.size > 0) {
            return csvBoardsList
        } else {
            logger.debug("I cannot find boards in the csv file.")
            return null
        }
    }

    private fun readBoardsFromCsv(): MutableList<CsvBoard> {
        println("Initiating Boards Reader")
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
}
