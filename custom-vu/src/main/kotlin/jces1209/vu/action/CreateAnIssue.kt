package jces1209.vu.action;

import com.atlassian.performance.tools.jiraactions.api.CREATE_ISSUE
import com.atlassian.performance.tools.jiraactions.api.CREATE_ISSUE_SUBMIT
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.page.form.IssueForm
import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.MeasureType.Companion.CREATE_ISSUE_MODAL
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

class CreateAnIssue(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val createIssueButtons: List<By>
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        meter.measure(CREATE_ISSUE) {
            jira.goToDashboard().dismissAllPopups()
            openDialog().fillRequiredFields() // TODO: to be fair, we should pick a random project and random issue type
            meter.measure(CREATE_ISSUE_SUBMIT) {
                jira.driver.wait(
                    condition = elementToBeClickable(By.id("create-issue-submit")),
                    timeout = Duration.ofSeconds(50)
                ).click()
                jira.driver.wait(
                    condition = invisibilityOfElementLocated(By.className("aui-blanket")),
                    timeout = Duration.ofSeconds(30)
                )
            }
        }
    }

    private fun openDialog(): IssueForm {
        val driver = jira.driver
        meter.measure(CREATE_ISSUE_MODAL) {
        driver
            .wait(
                condition = or(*createIssueButtons.map { elementToBeClickable(it) }.toTypedArray()),
                timeout = Duration.ofSeconds(10)
            )

        createIssueButtons
            .flatMap { driver.findElements(it) }
            .filter { it.isDisplayed }
            .first()
            .click()
            
            driver.wait(
                condition = visibilityOfElementLocated(By.id("create-issue-dialog")),
                timeout = Duration.ofSeconds(30)
            )
            (driver as JavascriptExecutor).executeScript("window.onbeforeunload = null")
        }
        return IssueForm(By.cssSelector("form[name=jiraform]"), driver)
    }
}
