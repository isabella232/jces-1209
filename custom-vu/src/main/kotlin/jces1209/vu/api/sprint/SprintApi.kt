package jces1209.vu.api.sprint

import jces1209.vu.api.ApiClient

interface SprintApi : ApiClient {

    fun deleteSprint(sprintId: String)
}
