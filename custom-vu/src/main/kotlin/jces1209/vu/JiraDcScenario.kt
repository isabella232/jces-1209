package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.BrowseProjectsAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import jces1209.vu.action.CreateAnIssue
import jces1209.vu.action.LogInToDc
import jces1209.vu.api.dashboard.DcDashboardApi
import jces1209.vu.api.issue.DcIssueApi
import jces1209.vu.api.sprint.DcSprintApi
import jces1209.vu.page.DcIssuePage
import jces1209.vu.page.admin.customfields.DcBrowseCustomFieldsPage
import jces1209.vu.page.admin.fieldscreen.DcBrowseFieldScreensPage
import jces1209.vu.page.admin.issuetypes.DcBrowseIssueTypesPage
import jces1209.vu.page.admin.manageprojectpermissions.DcManageProjectPermissionsPage
import jces1209.vu.page.admin.manageprojects.DcManageProjectsPage
import jces1209.vu.page.admin.projectroles.DcBrowseProjectRolesPage
import jces1209.vu.page.admin.workflow.browse.DcBrowseWorkflowsPage
import jces1209.vu.page.bars.side.DcSideBar
import jces1209.vu.page.bars.topBar.dc.DcTopBar
import jces1209.vu.page.boards.browse.dc.DcBrowseBoardsPage
import jces1209.vu.page.customizecolumns.DcColumnsEditor
import jces1209.vu.page.dashboard.dc.DcDashboardPage
import jces1209.vu.page.filters.ServerFiltersPage
import jces1209.vu.page.issuenavigator.DcIssueNavigator
import jces1209.vu.page.project.DcProjectNavigatorPage
import org.openqa.selenium.By
import org.openqa.selenium.TakesScreenshot

class JiraDcScenario : Scenario {

    override fun getLogInAction(
        jira: WebJira,
        meter: ActionMeter,
        userMemory: UserMemory
    ): Action {
        return LogInToDc(jira, meter, userMemory)
    }

    override fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        actionMeter: ActionMeter
    ): List<Action> {
        val meter = ActionMeter.Builder(actionMeter)
            .appendPostMetricHook(
                TakeScreenshotHook.Builder(
                    jira.driver as TakesScreenshot
                ).build())
            .build()
        val similarities = ScenarioSimilarities(jira, seededRandom, meter)
        return similarities.assembleScenario(
            issuePage = DcIssuePage(jira.driver),
            filtersPage = ServerFiltersPage(jira, jira.driver),
            browseFieldScreensPage = DcBrowseFieldScreensPage(jira),
            browseWorkflowsPage = DcBrowseWorkflowsPage(jira),
            browseCustomFieldsPage = DcBrowseCustomFieldsPage(jira),
            browseBoardsPage = DcBrowseBoardsPage(jira),
            dashboardPage = DcDashboardPage(jira),
            dashboardApi = DcDashboardApi(jira.base),
            sprintApi = DcSprintApi(jira.base),
            manageProjectsPage = DcManageProjectsPage(jira),
            projectNavigatorPage = DcProjectNavigatorPage(jira),
            manageProjectPermissionsPage = DcManageProjectPermissionsPage(jira),
            createIssue = CreateAnIssue(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory,
                issueApi = DcIssueApi(jira.base),
                createIssueButtons = listOf(By.id("create_link"))
            ),
            browseProjects = BrowseProjectsAction(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory
            ),
            issueNavigator = DcIssueNavigator(jira),
            columnsEditor = DcColumnsEditor(jira.driver),
            topBar = DcTopBar(jira.driver),
            browseIssueTypesPage = DcBrowseIssueTypesPage(jira),
            browseProjectRolesPage = DcBrowseProjectRolesPage(jira),
            sideBar = DcSideBar(jira)
        )
    }
}
