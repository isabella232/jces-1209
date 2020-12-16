package jces1209.vu.page.admin.manageprojectpermissions

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage

class DcManageProjectPermissionsPage(
    private val jira: WebJira
) : ManageProjectPermissionsPage(jira) {

    override val falliblePage: FalliblePage
        get() = TODO("Not yet implemented")
}
