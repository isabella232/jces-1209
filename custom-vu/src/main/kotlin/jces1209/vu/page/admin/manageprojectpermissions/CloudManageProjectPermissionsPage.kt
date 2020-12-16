package jces1209.vu.page.admin.manageprojectpermissions

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

class CloudManageProjectPermissionsPage(
    private val jira: WebJira
) : ManageProjectPermissionsPage(jira) {

    override val falliblePage = FalliblePage.Builder(
        driver,
        and(
            visibilityOfElementLocated(By.className("aui-page-panel")),
            elementToBeClickable(actionsButton),
            numberOfElementsToBe(By.xpath("//table[contains(@id,'project-config-permissions')]"), 6)
        )
    )
        .cloudErrors()
        .timeout(Duration.ofSeconds(30))
        .build()
}
