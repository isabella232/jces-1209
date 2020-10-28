package jces1209.vu.utils.boards

//File for testing boards generation
fun main(args: Array<String>) {

    for (x in 1 .. 1000) {
        val generateBoard = BoardsFrequencyManager()
        val board = generateBoard.getBoardByFrequency()
        println("Board id is ${board?.id}")
    }



}
