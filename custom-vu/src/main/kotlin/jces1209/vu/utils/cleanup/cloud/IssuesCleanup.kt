package jces1209.vu.utils.cleanup.cloud

import jces1209.vu.api.CloudSettings
import jces1209.vu.api.issue.CloudIssueApi
import jces1209.vu.api.issue.model.Issues
import java.net.URI

fun main(args: Array<String>) {
    val issueApi = CloudIssueApi(URI(CloudSettings.URL))
    var issues: Issues
    do {
        issues = issueApi.getIssues(CloudSettings.ACCOUNT_ID, 0)
        issues
            .issues
            .parallelStream()
            .forEach {
                val issueKey = it.key
                repeat(3) {
                    val response = issueApi.deleteIssue(issueKey)
                    if (response.code() == 204) {
                        return@repeat
                    }
                }
            }
    } while (issues.total != 0)
}
