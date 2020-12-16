package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.page.admin.manageprojectpermissions.ManageProjectPermissionsPage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ManageProjectPermissions(
    private val measure: Measure,
    private val projectKeyMemory: ProjectMemory,
    private val manageProjectPermissionsPage: ManageProjectPermissionsPage
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val projectKey = projectKeyMemory.recall()
        if (projectKey == null) {
            logger.debug("I don't recall any projects keys. Maybe next time I will.")
            return
        }

        measure.measure(MeasureType.MANAGE_PROJECT_PERMISSIONS) {
            val projectPermissionsPage = openProjectPermissionsPage(projectKey.key)
            projectPermissionsPage.openEditPermissionsView()
            projectPermissionsPage.editPermission()
        }
    }

    private fun openProjectPermissionsPage(projectKey: String): ManageProjectPermissionsPage {
        return manageProjectPermissionsPage
            .open(projectKey)
            .waitForBeingLoaded()
    }
}
