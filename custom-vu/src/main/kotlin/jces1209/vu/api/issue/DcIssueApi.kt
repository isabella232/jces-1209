package jces1209.vu.api.issue

import jces1209.vu.api.DcApiClient
import jces1209.vu.api.issue.model.Issues
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URI

class DcIssueApi(url: URI) : DcApiClient(url), IssueApi {

    override fun deleteIssue(issueKey: String): Response {
        TODO("Not yet implemented")
    }

    override fun getIssues(accountId: String, startAt: Int): Issues {
        TODO("Not yet implemented")
    }
}
