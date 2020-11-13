package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
import jces1209.vu.Measure
import jces1209.vu.MeasureType.Companion.CREATE_DASHBOARD
import jces1209.vu.MeasureType.Companion.CREATE_GADGET
import jces1209.vu.MeasureType.Companion.VIEW_DASHBOARD
import jces1209.vu.MeasureType.Companion.VIEW_DASHBOARDS
import jces1209.vu.api.dashboard.DashboardApi
import jces1209.vu.page.dashboard.DashboardPage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class WorkOnDashboard(
    private val jira: WebJira,
    private val measure: Measure,
    private val dashboardApi: DashboardApi,
    private val projectKeyMemory: ProjectMemory,
    private val dashboardPage: DashboardPage,
    private val viewDashboardsProbability: Float,
    private val viewDashboardProbability: Float,
    private val createDashboardAndGadgetProbability: Float
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        viewDashboards()
        createDashboardAndGadget()
        openDashboard()
    }

    private fun viewDashboards() {
        measure
            .measure(
                key = VIEW_DASHBOARDS,
                probability = viewDashboardsProbability) {
                openDashboardsPage()
                    .waitForDashboards()
            }
    }

    private fun openDashboardsPage(): DashboardPage {
        jira.navigateTo("/secure/ConfigurePortalPages!default.jspa?name=")
        return dashboardPage
    }

    private fun createDashboardAndGadget() {
        measure.roll(createDashboardAndGadgetProbability) {
            try {
                openDashboardsPage()
                    .waitForDashboards()
                val dashboardName = measure.measure(
                    key = CREATE_DASHBOARD) {
                    dashboardPage.createDashboard()
                }
                if (dashboardName != null) {
                    val projectKey = projectKeyMemory.recall()
                    if (projectKey == null) {
                        logger.debug("I don't recall any project keys. Maybe next time I will.")
                    } else {
                        dashboardPage.selectDashboardIfPresent(dashboardName)
                        measure.measure(
                            key = CREATE_GADGET,
                            action = {
                                dashboardPage.createGadget(projectKey.name)
                            }
                        )
                    }
                }
            } finally {
                if (dashboardApi.isReady()) {
                    val matchResult = "selectPageId=(\\d+)?".toRegex().find(jira.driver.currentUrl)
                    matchResult?.groupValues?.get(1)?.let { id ->
                        repeat(3) {
                            val response = dashboardApi.deleteDashboard(id)
                            if (response.code() == 204) {
                                return@repeat
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createGadget(dashboardName: String) {
        val projectKey = projectKeyMemory.recall()
        if (projectKey == null) {
            logger.debug("I don't recall any project keys. Maybe next time I will.")
            return
        }
        dashboardPage.selectDashboardIfPresent(dashboardName)
        measure.measure(
            key = CREATE_GADGET,
            action = {
                dashboardPage.createGadget(projectKey.name)
            }
        )
    }

    private fun openDashboard() {
        measure.measure(
            key = VIEW_DASHBOARD,
            probability = viewDashboardProbability,
            preconditions = {
                openDashboardsPage()
                    .waitForDashboards()
                    .clickPopularIfPresent()
            },
            action = {
                dashboardPage
                    .openDashboard()
                    .waitForGadgetsLoad()
            }
        )
    }
}
