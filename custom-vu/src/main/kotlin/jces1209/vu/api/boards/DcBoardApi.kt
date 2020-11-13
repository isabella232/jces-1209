package jces1209.vu.api.boards

import jces1209.vu.api.DcApiClient
import jces1209.vu.api.boards.model.Board
import jces1209.vu.api.boards.model.Boards
import jces1209.vu.api.sprint.model.Sprints
import java.net.URI

class DcBoardApi(url: URI) : DcApiClient(url), BoardApi {

    override fun getScrumBoards(startAt: Int): Boards {
        TODO("Not yet implemented")
    }

    override fun getSprints(boardId: Int, startAt: Int): Sprints {
        TODO("Not yet implemented")
    }
}
