package jces1209.vu.api.sprint

import jces1209.vu.api.DcApiClient
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URI

class DcSprintApi(url: URI) : DcApiClient(url), SprintApi {

    override fun deleteSprint(sprintId: String): Response {
        TODO("Not yet implemented")
    }
}
