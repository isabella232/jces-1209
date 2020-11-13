package jces1209.vu.api.issue

import jces1209.vu.api.ApiClient
import jces1209.vu.api.dashboard.model.Dashboards
import jces1209.vu.api.issue.model.Issues
import okhttp3.Response

interface IssueApi : ApiClient {

    fun deleteIssue(issueKey: String): Response
    fun getIssues(accountId: String, startAt: Int): Issues
}
