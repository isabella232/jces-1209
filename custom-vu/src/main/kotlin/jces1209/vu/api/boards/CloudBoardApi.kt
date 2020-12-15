package jces1209.vu.api.boards

import jces1209.vu.api.CloudApiClient
import jces1209.vu.api.boards.model.Boards
import jces1209.vu.api.sprint.model.Sprints
import java.net.URI

class CloudBoardApi(url: URI) : CloudApiClient(url), BoardApi {

    private val uri = "rest/agile/1.0/board/"

    override fun getScrumBoards(startAt: Int): Boards {
        return get("$uri?type=scrum&maxResults=$batchSize&startAt=$startAt", Boards::class.java)
    }

    override fun getSprints(boardId: Int, startAt: Int): Sprints {
        return get("$uri$boardId/sprint?maxResults=$batchSize&startAt=$startAt", Sprints::class.java)
    }
}
