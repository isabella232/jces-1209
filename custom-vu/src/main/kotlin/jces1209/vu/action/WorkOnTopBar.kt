package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import jces1209.vu.MeasureType.Companion.TOP_BAR_QUICK_SEARCH
import jces1209.vu.MeasureType.Companion.TOP_BAR_QUICK_SEARCH_SELECT_ITEM
import jces1209.vu.page.JiraCloudProjectList
import jces1209.vu.page.bars.topBar.TopBar
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Works for both Cloud and Data Center.
 */
class WorkOnTopBar(
    private val topBar: TopBar,
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val issueKeyMemory: IssueKeyMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val issueKey = issueKeyMemory.recall()
        if (issueKey == null) {
            logger.debug("I don't recall any issue keys. Maybe next time I will.")
            return
        }

        jira.navigateTo("projects")
        JiraCloudProjectList(jira.driver).lookForProjects()
        topBar.waitForTopBar()

        meter.measure(TOP_BAR_QUICK_SEARCH) {
            topBar.quickSearch(issueKey)
            meter.measure(TOP_BAR_QUICK_SEARCH_SELECT_ITEM) {
                topBar.selectItem(issueKey)
            }
        }
    }
}
