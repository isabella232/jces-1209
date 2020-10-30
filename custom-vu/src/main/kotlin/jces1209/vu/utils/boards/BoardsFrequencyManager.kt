package jces1209.vu.utils.boards

import java.util.*

/**
 * Randomizes the board page generation depending on the board usage frequency.
 */
class BoardsFrequencyManager {

    /**
     * Contains data on the boards usage frequency and is used to emulate actual boards usage.
     * The file should be stored in custom-vu/src/main/resources/.
     * The template for file population can be found at /custom-vu/src/main/resources/BoardUsageFrequencyTemplate.csv.
     */
    //TODO Move the csv file path to the tenant properties
    private val csvPropertyFile = "BoardUsageFrequency.csv"
    private val boardsFromCsv = CsvBoardsReader.readBoardsFromCsv(csvPropertyFile)
    private val boardsTotalWeight = calculateTotalBoardWeight()

    fun getBoardByFrequency(): CsvBoard? {
        if (boardsFromCsv.size > 0) {
            return defineFrequentBoard(boardsFromCsv, boardsTotalWeight)
        } else {
            return null
        }
    }

    private fun defineFrequentBoard(boardsList: MutableList<CsvBoard>?, totalWeight: Float): CsvBoard? {
        //defining a default fallback board from the csv list in case the random frequent board is not generated
        var resultBoard = boardsList?.get(Random().nextInt(boardsList.size))
        val random = Random().nextFloat() * totalWeight
        var countWeight = 0.0
        for (board in boardsList!!) {
            countWeight += board.frequency
            if (countWeight >= random && countWeight > 0) {
                resultBoard = board
                break
            }
        }
        return resultBoard
    }

    private fun calculateTotalBoardWeight(): Float {
        var totalWeight = 0F
        for (board in boardsFromCsv) {
            totalWeight += board.frequency
        }
        return totalWeight
    }
}
