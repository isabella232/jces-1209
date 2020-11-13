package jces1209.vu.utils.cleanup.cloud

import jces1209.vu.api.CloudSettings
import jces1209.vu.api.boards.CloudBoardApi
import jces1209.vu.api.boards.model.Boards
import jces1209.vu.api.sprint.CloudSprintApi
import jces1209.vu.api.sprint.model.Sprints
import java.net.URI


fun main(args: Array<String>) {
    val boardApi = CloudBoardApi(URI(CloudSettings.URL))
    val sprintApi = CloudSprintApi(URI(CloudSettings.URL))
    var boardsStartAt = 0
    var boardsBatch: Boards
    do {
        boardsBatch = boardApi.getScrumBoards(boardsStartAt)
        boardsBatch
            .values
            .parallelStream()
            .forEach { board ->
                var sprintsStartAt = 0
                var sprintsBatch: Sprints
                do {
                    sprintsBatch = boardApi.getSprints(board.id, sprintsStartAt)
                    if (null == sprintsBatch.values) {
                        return@forEach
                    } else {
                        sprintsBatch
                            .values!!
                            .parallelStream()
                            .forEach {
                                val sprintId = it.id
                                if (sprintId > CloudSettings.LAST_MIGRATED_SPRINT_ID) {
                                    repeat(3) {
                                        val response = sprintApi.deleteSprint(sprintId.toString())
                                        if (response.code() == 204) {
                                            return@repeat
                                        }
                                    }
                                }
                            }
                        sprintsStartAt += boardApi.batchSize
                    }
                } while (!sprintsBatch.isLast)
            }
        boardsStartAt += boardApi.batchSize
    } while (!boardsBatch.isLast)
}
