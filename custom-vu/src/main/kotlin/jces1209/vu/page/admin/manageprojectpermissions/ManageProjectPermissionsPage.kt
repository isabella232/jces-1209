package jces1209.vu.page.admin.manageprojectpermissions

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.page.FalliblePage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

abstract class ManageProjectPermissionsPage(
    private val jira: WebJira
) {
    abstract val falliblePage: FalliblePage
    protected val driver = jira.driver
    protected val actionsButton = By.id("project-config-tab-actions")
    private val grantPermissionButton = By.xpath("//button[.='Grant permission']")
    private val editPermissionButton = By.xpath("//button[.='Edit']")
    private val grantPermissionDialog = By.id("grant-project-permission-popup")

    fun open(projectKey: String): ManageProjectPermissionsPage {
        jira.navigateTo("plugins/servlet/project-config/$projectKey/permissions")
        return this
    }

    fun waitForBeingLoaded(): ManageProjectPermissionsPage {
        falliblePage.waitForPageToLoad()
        return this
    }

    fun openEditPermissionsView() {
        driver.findElement(actionsButton).click()
        driver.wait(ExpectedConditions.elementToBeClickable(By.xpath("//a[.='Edit permissions']"))).click()
        driver.wait(
            condition = ExpectedConditions.and(
                ExpectedConditions.elementToBeClickable(grantPermissionButton),
                ExpectedConditions.numberOfElementsToBe(By.className("permissions-group"), 7),
                ExpectedConditions.numberOfElementsToBeMoreThan(editPermissionButton, 0)
            ),
            timeout = (Duration.ofSeconds(30))
        )
    }

    fun editPermission() {
        openEditPermissionDialog()
        submitEditedPermission()
    }

    private fun openEditPermissionDialog() {
        val editButtons = driver
            .findElements(editPermissionButton)
        editButtons.last().click()
        driver.wait(
            condition = ExpectedConditions.and(
                ExpectedConditions.visibilityOfElementLocated(grantPermissionDialog),
                ExpectedConditions.elementToBeClickable(By.xpath("//button[.='Show more']")),
                ExpectedConditions.numberOfElementsToBe(By.name("security-type"), 10)
            ),
            timeout = (Duration.ofSeconds(30))
        )
    }

    private fun submitEditedPermission() {
        driver.findElement(By.xpath("//label[.='Application access']")).click()
        driver.wait(ExpectedConditions.elementToBeClickable(By.id("grant-permission-dialog-grant-button"))).click()
        driver.wait(
            ExpectedConditions.and(
                ExpectedConditions.invisibilityOfElementLocated(grantPermissionDialog),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'success')]"))
            )
        )
    }
}
