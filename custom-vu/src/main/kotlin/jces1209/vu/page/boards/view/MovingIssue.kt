package jces1209.vu.page.boards.view

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.*

class MovingIssue(
    val driver: WebDriver
) {
    private val columnLocator = By.className("ghx-column")
    private val issueLocator = By.className("js-issue")
    private val modalDialogLocator = By.className("jira-dialog")
    private val transitionSubmitButtonLocator = By.id("issue-workflow-transition-submit")
    private val moveZoneLocator = By.className("ghx-zone-cell")

    fun moveIssueToOtherColumn(): Issue {
        val columns = driver.findElements(columnLocator)

        val columnFromIndex = columns.indexOfFirst { it.findElements(issueLocator).isNotEmpty() }
        val columnFrom = columns[columnFromIndex]

        val columnNext = columns[(columnFromIndex + 1) % columns.size]

        val issueElement = columnFrom.findElement(issueLocator)
        val issueGrabber = issueElement.findElement(By.className("ghx-grabber"))
        val issueKey = issueElement.getAttribute("data-issue-key")

        Actions(driver)
            .clickAndHold(issueGrabber)
            .moveToElement(columnNext)
            .perform()

        val moveZones = driver.wait(visibilityOfAllElementsLocatedBy(moveZoneLocator))

        if (moveZones.isEmpty()) {
            throw InterruptedException("There are no moving zones for issue [$issueKey]")
        }

        Actions(driver)
            .moveToElement(driver
                .findElements(moveZoneLocator)
                .first { it.isDisplayed })
            .perform()

        //adding delay for jira to recognize dragAndDrop action and react to it
        Thread.sleep(1000)
        Actions(driver)
            .release()
            .perform()

        val issue = Issue(columnFromIndex, issueKey)

        val conditionMessageVisibility = visibilityOfAllElementsLocatedBy(By.className("global-msg"))
        driver
            .wait(
                or(
                    visibilityOfElementLocated(modalDialogLocator),
                    movedIssueExpectedCondition(issue),
                    conditionMessageVisibility
                )
            )

        if (!movedIssueExpectedCondition(issue).apply(driver)!!) {
            val messages = conditionMessageVisibility.apply(driver)
            if (messages == null || messages.isEmpty()) {
                throw RuntimeException("Failed to move issue [${issue.key}]")
            } else {
                throw InterruptedException("The issue [${issue.key}] can't be moved. Error messages [$messages]")
            }
        }

        return issue
    }

    fun isModalWindowDisplayed(): Boolean {
        return driver.findElements(modalDialogLocator).isNotEmpty()
    }

    fun isContinueButtonEnabled(): Boolean {
        return driver.findElements(transitionSubmitButtonLocator).any { it.isEnabled }
    }

    private fun movedIssueExpectedCondition(issue: Issue): ExpectedCondition<Boolean> {
        val issueLocator = By.cssSelector(".js-issue[data-issue-key=${issue.key}]")
        return ExpectedCondition {
            val columnsElements = driver
                .findElements(columnLocator)
            columnsElements[issue.columnFromIndex]
                .findElements(issueLocator)
                .isEmpty()
                && columnsElements
                .filterIndexed { index, _ -> index != issue.columnFromIndex }
                .any {
                    it
                        .findElements(issueLocator)
                        .isNotEmpty()
                }
        }
    }

    fun clickContinueButton(issue: Issue) {
        driver
            .findElements(By.id("log-work-time-logged"))
            .forEach { it.sendKeys("2d") }

        driver
            .findElement(transitionSubmitButtonLocator)
            .click()

        driver
            .wait(
                or(
                    visibilityOfElementLocated(By.className("error")),
                    movedIssueExpectedCondition(issue)
                )
            )
    }

    fun closeWindows() {
        driver
            .findElements(By.cssSelector("#aui-dialog-close, #issue-workflow-transition-cancel"))
            .filter { it.isDisplayed }
            .forEach { it.click() }
    }

    class Issue(
        val columnFromIndex: Int,
        val key: String
    )
}
