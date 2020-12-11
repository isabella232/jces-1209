package jces1209.vu.page.boards.view

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import java.net.URI

abstract class ScrumSprintPage(
    driver: WebDriver,
    uri: URI
) : BoardPage(
    driver = driver,
    uri = uri
) {
    private val issueLocator = By.className("ghx-issue")
    private val issueSummaryLocator = By.className("ghx-summary")
    private val completeButtonLocator = By.id("ghx-complete-sprint")

    override fun getTypeLabel(): String {
        return "Sprint"
    }

    private fun columns() = driver
        .findElements(By.cssSelector(".ghx-columns .ghx-column"))

    fun maxColumnIssuesNumber(): Int = columns()
        .map { it.findElements(issueLocator).size }
        .max() ?: 0

    fun reorderIssue() {
        val columns = columns()
        val columnIndex = columns
            .indexOfFirst { it.findElements(issueLocator).size > 2 }

        val issueFirstText = columns[columnIndex]
            .findElements(issueSummaryLocator)
            .first()
            .text

        val issues = columns[columnIndex]
            .findElements(By.cssSelector(".ghx-card-footer, .ghx-grabber"))
        Actions(driver)
            .dragAndDrop(issues.first(), issues.elementAt(1))
            .perform()

        driver.wait(
            ExpectedCondition {
                columns()[columnIndex]
                    .findElements(issueSummaryLocator)
                    .elementAt(1)
                    .text == issueFirstText
            })
    }

    fun isCompleteButtonPresent(): Boolean {
        return driver
            .findElements(completeButtonLocator)
            .isNotEmpty()
    }

    fun completeSprint() {
        driver
            .findElement(completeButtonLocator)
            .click()

        driver
            .wait(ExpectedConditions.visibilityOfElementLocated(By.className("ghx-complete-button")))
            .click()

        driver
            .wait(
                ExpectedConditions.and(
                    ExpectedConditions.visibilityOfElementLocated(By.className("aui-message-success")),
                    ExpectedConditions.visibilityOfElementLocated(By.id("subnav-trigger-report")
                    )))
    }
}
