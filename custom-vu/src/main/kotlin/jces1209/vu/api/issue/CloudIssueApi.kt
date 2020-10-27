package jces1209.vu.api.issue

import jces1209.vu.api.CloudApiClient
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URI

class CloudIssueApi(url: URI) : CloudApiClient(url), IssueApi {

    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val issuesUri = "/rest/api/3/issue/"

    override fun deleteIssue(issueKey: String) {
        val request = Request.Builder()
            .url("$url$issuesUri$issueKey")
            .delete()
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", Credentials.basic(getUser(), getPassword()))
            .build()
        val response = okHttpClient.newCall(request).execute()
        println("Delete issue. Response code [${response.code()}]. Body [${response.body()?.string()}], Request [$request]")
    }
}
