package jces1209.vu.utils.boards

import java.util.*

class BoardsFrequencyManager {

    fun getBoardByFrequency(): CsvBoard? {
        val boardsList = CsvBoardsReader.boardsList
        if (boardsList != null && boardsList.size > 0) {
            return defineFrequentBoard(boardsList)
        } else {
            return null
        }
    }

    private fun defineFrequentBoard(boardsList: MutableList<CsvBoard>): CsvBoard {
        //defining a default fallback board in case the random frequent board is not generated
        var resultBoard = boardsList[Random().nextInt(boardsList.size)]
        var totalWeight = 0.0
        for (board in boardsList) {
            totalWeight += board.frequency
        }
        val random = Random().nextFloat() * totalWeight
        var countWeight = 0.0
        for (board in boardsList) {
            countWeight += board.frequency
            if (countWeight >= random && countWeight > 0) {
                resultBoard = board
                break
            }
        }
        return resultBoard
    }
}
