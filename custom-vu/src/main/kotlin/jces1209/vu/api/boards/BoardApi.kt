package jces1209.vu.api.boards

import jces1209.vu.api.ApiClient
import jces1209.vu.api.boards.model.Board
import jces1209.vu.api.boards.model.Boards
import jces1209.vu.api.sprint.model.Sprints

interface BoardApi : ApiClient {

    fun getScrumBoards(startAt: Int): Boards
    fun getSprints(boardId: Int, startAt: Int): Sprints
}
