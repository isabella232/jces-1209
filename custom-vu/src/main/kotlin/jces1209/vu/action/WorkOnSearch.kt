package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.Memory
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.MeasureType.Companion.SWITCH_BETWEEN_ISSUES_IN_SEARCH_RESULTS
import jces1209.vu.page.customizecolumns.ColumnsEditor
import jces1209.vu.page.issuenavigator.IssueNavigator
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URI
import javax.json.JsonObject

class WorkOnSearch(
        private val issueNavigator: IssueNavigator,
        private val jira: WebJira,
        private val measure: Measure,
        private val columnsEditor: ColumnsEditor,
        private val filters: Memory<URI>,
        private val jqlMemory: JqlMemory,
        private val issueKeyMemory: String,
        private val searchFilterProbability: Float,
        private val searchJclProbability: Float,
        private val globalSearchProbability: Float,
        private val customizeColumnsProbability: Float,
        private val subscribeToFilterProbability: Float,
        private val switchBetweenIssuesProbability: Float
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        openGlobalIssueSearch()
        searchFilter()
        searchJql()
        customizeColumns()
        switchBetweenIssues()
        subscribeToFilter()
    }

    private fun switchBetweenIssues() {
        measure.roll(switchBetweenIssuesProbability) {
            issueNavigator
                .openNavigator()
                .waitForBeingLoaded()

            measure.measure(SWITCH_BETWEEN_ISSUES_IN_SEARCH_RESULTS) {
                issueNavigator.selectIssue()
            }
        }
    }

    private fun openGlobalIssueSearch() {

        val orderByCreatedProbability = 0.9f
        val lastViewedProbability = 0.02f
        val byProjectProbability = 0.02f
        val withAssigneeProbability = 0.02f
        val byStatusProbability = 0.02f
        val emptyProbability = 0.02f
        val orderByCreated = "/issues/?jql=order%20by%20created%20DESC"
        val lastViewed = "/issues/?jql=order%20by%20lastViewed%20DESC%2C%20key%20DESC"
        val byProject = "/issues/?jql=project%20is%20"
        val withAssignee = "/issues/?jql=Assignee%20"
        val byStatus = "/issues/?jql=status%20not%20in%20(%20Resolved)"
        val empty = "/issues/"
        val queryList = HashMap<String, Float>()
        queryList[orderByCreated] = orderByCreatedProbability
        queryList[lastViewed] = lastViewedProbability
        queryList[byProject] = byProjectProbability
        queryList[withAssignee] = withAssigneeProbability
        queryList[byStatus] = byStatusProbability
        queryList[empty] = emptyProbability

        queryList.forEach { k, v ->
            measure.measure(MeasureType.OPEN_GLOBAL_SEARCH, globalSearchProbability) {
                measure.roll(v) {
                    jira.navigateTo(k)
                    issueNavigator.waitForBeingLoaded()
                }
            }
        }
    }

    private fun customizeColumns() {
        measure.roll(customizeColumnsProbability) {
            jira.goToIssueNavigator("resolution = Unresolved ORDER BY priority DESC")
            measure.measure(MeasureType.CUSTOMIZE_COLUMNS) {
                columnsEditor.openEditor()
                columnsEditor.selectItems(2)
                columnsEditor.submitSelection()
            }
        }
    }

    private fun searchFilter() {
        measure.roll(searchFilterProbability) {
            val filter = filters.recall()
            if (null == filter) {
                logger.debug("I cannot recall filter, skipping...")
                return@roll
            }
            measure.measure(SEARCH_WITH_JQL) {
                jira.navigateTo(filter.toString())
                issueNavigator.waitForBeingLoaded()
            }
        }
    }

    private fun searchJql() {
        measure.roll(searchJclProbability) {
            val jqlQuery = jqlMemory.recall()
            if (null == jqlQuery) {
                logger.debug("I cannot recall jql query, skipping...")
                return@roll
            }
            measure.measure(
                SEARCH_WITH_JQL,
                observation = fun(navigator: IssueNavigatorPage): JsonObject {
                    val issueKeys = navigator.getIssueKeys()
                  //  issueKeyMemory.remember(issueKeys)
                    return SearchJqlObservation(
                        navigator.jql,
                        issueKeys.size,
                        -1 // work around https://ecosystem.atlassian.net/browse/JPERF-605
                    ).serialize()
                }
            ) {
                jira.goToIssueNavigator(jqlQuery)
            }
        }
    }

    private fun subscribeToFilter() {
        measure.roll(subscribeToFilterProbability) {
            val filter = filters.recall()
            if (null == filter) {
                logger.debug("I cannot recall filter, skipping...")
                return@roll
            }
            jira.navigateTo(filter.toString())
            issueNavigator.waitForBeingLoaded()
            measure.measure(MeasureType.SUBSCRIBE_TO_FILTER) {
                measure.measure(MeasureType.SUBSCRIBE_TO_FILTER_DETAILS_WDW) {
                    issueNavigator.clickDetails()
                }
                measure.measure(MeasureType.SUBSCRIBE_TO_FILTER_NEW_SUBSCR) {
                    issueNavigator.clickNewSubscription()
                }
                measure.measure(MeasureType.SUBSCRIBE_TO_FILTER_SUBSCR_SUBM) {
                    issueNavigator.subscribe()
                }
            }
            //JiraTips(jira.driver).closeTips()
            measure.measure(MeasureType.MANAGE_SUBSCR) {
                measure.measure(MeasureType.SUBSCRIBE_TO_FILTER_DETAILS_WDW) {
                    issueNavigator.clickDetails()
                }
                issueNavigator.manageSubscriptions()
            }
        }
    }
}
