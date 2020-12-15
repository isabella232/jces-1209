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

        val firstIssue = columns[columnIndex]
            .findElements(By.cssSelector(".ghx-card-footer, .ghx-grabber"))
            .first()
        val targetIssue = columns[columnIndex]
            .findElements(By.className("ghx-issuekey-number"))
            .elementAt(1)

        Actions(driver)
            .moveToElement(firstIssue)
            .perform()
        //adding delay to start dragAndDrop action
        Thread.sleep(200)

        Actions(driver)
            .clickAndHold()
            .perform()
        //adding delay to proceed with dragAndDrop
        Thread.sleep(150)

        Actions(driver)
            .moveToElement(targetIssue)
            .perform()
        //adding delay to complete dragAndDrop
        Thread.sleep(100)

        Actions(driver)
            .release()
            .perform()

        driver.wait(
            ExpectedCondition {
                columns()[columnIndex]
                    .findElements(issueSummaryLocator)
                    .elementAt(0)
                    .text != issueFirstText
            })
    }

    fun isCompleteButtonEnabled(): Boolean {
        val completeButton = driver.findElements(completeButtonLocator)
        if (completeButton.isNotEmpty()) {
            return completeButton[0].isEnabled
        }
        return false
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
