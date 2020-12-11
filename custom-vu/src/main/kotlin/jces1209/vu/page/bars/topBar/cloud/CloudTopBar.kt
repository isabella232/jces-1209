package jces1209.vu.page.bars.topBar.cloud

import jces1209.vu.page.AbstractIssuePage
import jces1209.vu.page.CloudIssuePage
import jces1209.vu.page.bars.topBar.TopBar
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

class CloudTopBar(
    private val driver: WebDriver
) : TopBar {

    override fun waitForTopBar(): CloudTopBar {
        driver.wait(visibilityOfElementLocated(By.xpath("//*[@data-test-id='search-dialog-input']")))
        return this
    }

    override fun quickSearch(issueKey: String): CloudTopBar {
        Actions(driver)
            .sendKeys("/")
            .perform()

        Actions(driver)
            .sendKeys(issueKey)
            .perform()

        driver
            .wait(
                ExpectedConditions.and(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(generateIssueItemLocator(issueKey)),
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@data-test-id='search-dialog-dialog-wrapper']//*[contains(text(), 'Boards, Projects, Filters and Plans')]"))
                )
            )
        return this
    }

    override fun selectItem(issueKey: String): AbstractIssuePage {
        driver
            .wait(visibilityOfElementLocated(generateIssueItemLocator(issueKey)))
            .click()

        return CloudIssuePage(driver).waitForSummary()
    }

    private fun generateIssueItemLocator(issueKey: String) =
        By.xpath("//*[@data-test-id='search-dialog-dialog-wrapper']//span[@title and text()='$issueKey']")
}
