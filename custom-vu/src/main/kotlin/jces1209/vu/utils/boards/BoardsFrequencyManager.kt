package jces1209.vu.utils.boards

import java.util.*

class BoardsFrequencyManager {

    private val boardsFromCsv = CsvBoardsReader.csvContent
    private val boardsTotalWeight = calculateTotalBoardWeight()

    fun getBoardByFrequency(): CsvBoard? {
        if (boardsFromCsv != null && boardsFromCsv.size > 0) {
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
        for (board in boardsFromCsv!!) {
            totalWeight += board.frequency
        }
        return totalWeight
    }
}
