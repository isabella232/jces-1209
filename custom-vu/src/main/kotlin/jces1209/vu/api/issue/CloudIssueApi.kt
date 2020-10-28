package jces1209.vu.api.issue

import jces1209.vu.api.CloudApiClient
import java.net.URI

class CloudIssueApi(url: URI) : CloudApiClient(url), IssueApi {

    private val uri = "/rest/api/3/issue/"

    override fun deleteIssue(issueKey: String) {
        delete(uri + issueKey)
    }
}
