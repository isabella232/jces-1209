package jces1209.vu.api.issue

import jces1209.vu.api.ApiClient

interface IssueApi : ApiClient {
    fun deleteIssue(issueKey: String)
}
