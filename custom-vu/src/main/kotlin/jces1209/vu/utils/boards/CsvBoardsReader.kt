package jces1209.vu.utils.boards

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.lang.IllegalStateException
import java.net.URI

object CsvBoardsReader {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    fun readBoardsFromCsv(csvPropertyFile: String): MutableList<CsvBoard> {
        val boardsFromCsv = mutableListOf<CsvBoard>()
        var csvContent = String()
        try {
            csvContent = this::class.java.getResource("/$csvPropertyFile").readText()
        } catch (exc: IllegalStateException) {
            logger.debug("The csv file was not found, skipping...")
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
                val id = parsedList[0]
                val frequency = parsedList[1].toFloat()
                val uri = URI(parsedList[2])
                val type = parsedList[3].toLowerCase()
                val csvBoard = CsvBoard(id, frequency, uri, type)
                boardsList.add(csvBoard)
            } else {
                logger.debug("The csv line is blank, skipping...")
            }
        }
    }
}
