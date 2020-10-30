package jces1209.vu.api.sprint

import jces1209.vu.api.CloudApiClient
import java.net.URI

class CloudSprintApi(url: URI) : CloudApiClient(url), SprintApi {

    private val uri = "/rest/agile/1.0/sprint/"

    override fun deleteSprint(sprintId: String) {
        delete(uri + sprintId)
    }
}
