package jces1209.vu.api.issue

import jces1209.vu.api.CloudApiClient
import jces1209.vu.api.issue.model.Issues
import okhttp3.Response
import java.net.URI

class CloudIssueApi(url: URI) : CloudApiClient(url), IssueApi {

    private val uri = "rest/api/3/issue/"
    private val uriSearch = "rest/api/3/search"

    override fun deleteIssue(issueKey: String): Response {
        return delete(uri + issueKey).second
    }

    override fun getIssues(accountId: String, startAt: Int): Issues {
        return get("$uriSearch?jql=creator in ($accountId)&fields=key&startAt=$startAt", Issues::class.java)
    }
}
