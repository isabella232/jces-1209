package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.ProjectSummaryAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory
import jces1209.vu.action.*
import jces1209.vu.api.dashboard.DashboardApi
import jces1209.vu.api.sprint.SprintApi
import jces1209.vu.memory.BoardPagesMemory
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.AbstractIssuePage
import jces1209.vu.page.JiraTips
import jces1209.vu.page.admin.customfields.BrowseCustomFieldsPage
import jces1209.vu.page.admin.fieldconfigs.BrowseFieldConfigurationsPage
import jces1209.vu.page.admin.fieldscreen.BrowseFieldScreensPage
import jces1209.vu.page.admin.issuetypes.BrowseIssueTypesPage
import jces1209.vu.page.admin.manageprojectpermissions.ManageProjectPermissionsPage
import jces1209.vu.page.admin.manageprojects.ManageProjectsPage
import jces1209.vu.page.admin.projectroles.BrowseProjectRolesPage
import jces1209.vu.page.admin.workflow.browse.BrowseWorkflowsPage
import jces1209.vu.page.bars.side.SideBar
import jces1209.vu.page.bars.topBar.TopBar
import jces1209.vu.page.boards.browse.BrowseBoardsPage
import jces1209.vu.page.customizecolumns.ColumnsEditor
import jces1209.vu.page.dashboard.DashboardPage
import jces1209.vu.page.filters.FiltersPage
import jces1209.vu.page.issuenavigator.IssueNavigator
import jces1209.vu.page.project.ProjectNavigatorPage
import java.net.URI
import java.util.*

class ScenarioSimilarities(
    private val jira: WebJira,
    private val seededRandom: SeededRandom,
    private val meter: ActionMeter
) {
    val measure = Measure(meter, seededRandom)
    val jqlMemory = AdaptiveJqlMemory(seededRandom)
        .also { it.remember(listOf("order by created DESC")) } // work around https://ecosystem.atlassian.net/browse/JPERF-573
    val issueKeyMemory = AdaptiveIssueKeyMemory(seededRandom)
    val projectMemory = AdaptiveProjectMemory(seededRandom)
    val filtersMemory = SeededMemory<URI>(seededRandom)
    val boardsMemory = BoardPagesMemory(seededRandom)

    fun assembleScenario(
        issuePage: AbstractIssuePage,
        filtersPage: FiltersPage,
        browseWorkflowsPage: BrowseWorkflowsPage,
        browseFieldScreensPage: BrowseFieldScreensPage,
        browseCustomFieldsPage: BrowseCustomFieldsPage,
        browseBoardsPage: BrowseBoardsPage,
        dashboardPage: DashboardPage,
        dashboardApi: DashboardApi,
        sprintApi: SprintApi,
        createIssue: Action,
        browseProjects: Action,
        issueNavigator: IssueNavigator,
        columnsEditor: ColumnsEditor,
        manageProjectsPage: ManageProjectsPage,
        topBar: TopBar,
        sideBar: SideBar,
        projectNavigatorPage: ProjectNavigatorPage,
        browseIssueTypesPage: BrowseIssueTypesPage,
        browseProjectRolesPage: BrowseProjectRolesPage,
        manageProjectPermissionsPage: ManageProjectPermissionsPage
    ): List<Action> = assembleScenario(
        createIssue = createIssue,
        manageProjects = ManageProjects(
            measure = measure,
            manageProjectsPage = manageProjectsPage
        ),
        workOnDashboard = WorkOnDashboard(
            jira = jira,
            measure = measure,
            projectKeyMemory = projectMemory,
            dashboardPage = dashboardPage,
            dashboardApi = dashboardApi,
            viewDashboardsProbability = 1.00f,
            viewDashboardProbability = 1.00f,
            createDashboardAndGadgetProbability = 0.00f // 0.10f if we can mutate data
        ),
        workAnIssue = WorkOnIssue(
            issuePage = issuePage,
            jira = jira,
            measure = measure,
            issueKeyMemory = issueKeyMemory,
            editProbability = 0.00f, // 0.10f if we can mutate data
            commentProbability = 0.00f, // 0.04f if we can mutate data
            linkIssueProbability = 0.00f, // 0.10f if we can mutate data
            attachScreenShotProbability = 0.00f, // 0.04f if we can mutate data
            changeAssigneeProbability = 0.00f, // 0.04f if we can mutate data
            mentionUserProbability = 0.00f, // 0.04f if we can mutate data
            transitionProbability = 0.00f, // 0.04f if we can mutate data
            contextOperationProbability = 0.05f
        ),
        projectSummary = ProjectSummaryAction(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        ),
        browseProjects = browseProjects,
        browseFilters = BrowsePopularFilters(
            filters = filtersMemory,
            filtersPage = filtersPage,
            meter = meter
        ),
        browseBoards = BrowseBoards(
            jira = jira,
            browseBoardsPage = browseBoardsPage,
            meter = meter,
            boardsMemory = boardsMemory
        ),
        viewBoard = ViewBoard(
            driver = jira.driver,
            measure = measure,
            boardsMemory = boardsMemory.all,
            issueKeyMemory = issueKeyMemory,
            viewIssueProbability = 0.50f,
            configureBoardProbability = 0.05f,
            contextOperationProbability = 0.05f,
            changeIssueStatusProbability = 0.10f
        ),
        workOnSprint = WorkOnSprint(
            meter = meter,
            driver = jira.driver,
            sprintApi = sprintApi,
            backlogsMemory = boardsMemory.backlog,
            sprintsMemory = boardsMemory.sprint,
            jiraTips = JiraTips(jira.driver)
        ),
        browseProjectIssues = BrowseProjectIssues(
            meter = meter,
            projectKeyMemory = projectMemory,
            browseProjectPage = projectNavigatorPage
        ),
        workOnBacklog = WorkOnBacklog(
            measure = measure,
            backlogsMemory = boardsMemory.backlog
        ),
        workOnTopBar = WorkOnTopBar(
            topBar = topBar,
            jira = jira,
            meter = meter,
            issueKeyMemory = issueKeyMemory
        ),
        workOnSearch = WorkOnSearch(
            issueNavigator = issueNavigator,
            jira = jira,
            measure = measure,
            columnsEditor = columnsEditor,
            filters = filtersMemory,
            jqlMemory = jqlMemory,
            issueKeyMemory = issueKeyMemory,
            searchFilterProbability = 0.50f,
            searchJclProbability = 0.05f,
            globalSearchProbability = 0.50f,
            customizeColumnsProbability = 0.05f,
            switchBetweenIssuesProbability = 0.15f,
            subscribeToFilterProbability = 0.00f
        ),
        bulkEdit = BulkEdit(
            measure = measure,
            issueNavigator = issueNavigator
        ),
        workOnTransition = WorkOnTransition(
            measure = measure,
            boardsMemory = boardsMemory.sprint,
            sideBar = sideBar,
            issueNavigator = issueNavigator,
            switchViewsProbability = 0.75f
        ),
        browseIssueTypes = BrowseIssueTypes(
            measure = measure,
            browseIssueTypesPage = browseIssueTypesPage
        ),
        browseProjectRoles = BrowseProjectRoles(
            meter = meter,
            browseProjectRolesPage = browseProjectRolesPage
        ),
        workOnWorkflow = WorkOnWorkflow(
            measure = measure,
            browseWorkflowsPage = browseWorkflowsPage,
            browseWorkflowsProbability = 0.50f,
            viewWorkflowProbability = 0.50f,
            createWorkflowProbability = 0.00f, // 0.02 if we can mutate data
            editWorkflowProbability = 0.00f // 0.06 if we can mutate data
        ),
        browseFieldScreens = BrowseFieldScreens(
            measure = measure,
            browseFieldScreensPage = browseFieldScreensPage
        ),
        browseFieldConfigurations = BrowseFieldConfigurations(
            measure = measure,
            browseFieldConfigurationsPage = BrowseFieldConfigurationsPage(jira)
        ),
        browseCustomFields = BrowseCustomFields(
            measure = measure,
            browseCustomFieldsPage = browseCustomFieldsPage
        ),
        manageProjectPermissions = ManageProjectPermissions(
            measure = measure,
            projectKeyMemory = projectMemory,
            manageProjectPermissionsPage = manageProjectPermissionsPage
        )
    )

    private fun assembleScenario(
        createIssue: Action,
        manageProjects: Action,
        workAnIssue: Action,
        projectSummary: Action,
        browseProjects: Action,
        browseFilters: Action,
        browseBoards: Action,
        viewBoard: Action,
        workOnDashboard: Action,
        workOnSprint: Action,
        browseProjectIssues: Action,
        workOnBacklog: Action,
        workOnSearch: Action,
        workOnTopBar: Action,
        bulkEdit: Action,
        workOnTransition: Action,
        workOnWorkflow: Action,
        browseFieldScreens: Action,
        browseFieldConfigurations: Action,
        browseCustomFields: Action,
        browseIssueTypes: Action,
        browseProjectRoles: Action,
        manageProjectPermissions: Action
    ): List<Action> {
        val readTrafficShapeConfig = System.getenv("readTrafficShapeConfig")
        var properties = Properties()

        if (!readTrafficShapeConfig.isNullOrEmpty()) {
            val resourceName = TrafficDataParser.parseData(jira.base.host, readTrafficShapeConfig)
            properties = ConfigProperties.load(resourceName)
        }

        val exploreData = listOf(browseProjects, browseFilters, browseBoards)
        val spreadOut = mapOf(
            createIssue to ((properties.getProperty("action.createIssue")?.toInt()) ?: 0), // 5 if we can mutate data
            workAnIssue to ((properties.getProperty("action.workAnIssue")?.toInt()) ?: 55),
            manageProjects to ((properties.getProperty("action.manageProjects")?.toInt()) ?: 5),
            projectSummary to ((properties.getProperty("action.projectSummary")?.toInt()) ?: 5),
            browseProjects to ((properties.getProperty("action.browseProjects")?.toInt()) ?: 5),
            browseBoards to ((properties.getProperty("action.browseBoards")?.toInt()) ?: 5),
            viewBoard to ((properties.getProperty("action.viewBoard")?.toInt()) ?: 30),
            workOnDashboard to ((properties.getProperty("action.workOnDashboard")?.toInt()) ?: 5),
            workOnSprint to ((properties.getProperty("action.workOnSprint")?.toInt()) ?: 0), // 3 if we can mutate data
            browseProjectIssues to ((properties.getProperty("action.browseProjectIssues")?.toInt()) ?: 5),
            workOnBacklog to ((properties.getProperty("action.workOnBacklog")?.toInt()) ?: 0), // 3 if we can mutate data
            workOnSearch to ((properties.getProperty("action.workOnSearch")?.toInt()) ?: 5),
            workOnTopBar to ((properties.getProperty("action.workOnTopBar")?.toInt()) ?: 5),
            bulkEdit to ((properties.getProperty("action.bulkEdit")?.toInt()) ?: 0), // 5 if we can mutate data
            workOnTransition to ((properties.getProperty("action.workOnTransition")?.toInt()) ?: 5),
            workOnWorkflow to ((properties.getProperty("action.workOnWorkflow")?.toInt()) ?: 5),
            browseFieldScreens to ((properties.getProperty("action.browseFieldScreens")?.toInt()) ?: 5),
            browseFieldConfigurations to ((properties.getProperty("action.browseFieldConfigurations")?.toInt()) ?: 5),
            browseCustomFields to ((properties.getProperty("action.browseCustomFields")?.toInt()) ?: 5),
            browseIssueTypes to ((properties.getProperty("action.browseIssueTypes")?.toInt()) ?: 5),
            browseProjectRoles to ((properties.getProperty("action.browseProjectRoles")?.toInt()) ?: 5),
            manageProjectPermissions to ((properties.getProperty("action.manageProjectPermissions")?.toInt()) ?: 5)
        )
            .map { (action, proportion) -> Collections.nCopies(proportion, action) }
            .flatten()
            .shuffled(seededRandom.random)
        return exploreData + spreadOut
    }
}
