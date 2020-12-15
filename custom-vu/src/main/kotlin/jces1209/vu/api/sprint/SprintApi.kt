package jces1209.vu.api.sprint

import jces1209.vu.api.ApiClient
import okhttp3.Response

interface SprintApi : ApiClient {

    fun deleteSprint(sprintId: String): Response
}
