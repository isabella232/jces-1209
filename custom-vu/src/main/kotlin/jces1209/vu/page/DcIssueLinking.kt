package jces1209.vu.page;

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions.*

class DcIssueLinking(
    private val driver: WebDriver
) : IssueLinking {

    private val linkIssueSearchDialog = By.id("link-issue-dialog")
    private val moreOperationsButtonLocator = By.cssSelector("[aria-controls='opsbar-operations_more_drop']")
    private val linkOperationLocator = By.id("link-issue")

    override fun openEditor() {
        driver
            .wait(visibilityOfElementLocated(moreOperationsButtonLocator))
            .click()

        driver
            .wait(visibilityOfElementLocated(linkOperationLocator))
            .click()

        driver
            .wait(visibilityOfElementLocated(linkIssueSearchDialog))
    }

    override fun searchAndChooseIssue(issuePrefix: String) {
        driver
            .wait(visibilityOfElementLocated(By.id("remote-jira-issue-search")))
            .click()

        val searchButton = driver
            .wait(visibilityOfElementLocated(By.id("simple-search-panel-button")))

        Actions(driver)
            .sendKeys(issuePrefix)
            .perform()

        searchButton.click()

        driver
            .wait(visibilityOfElementLocated(By.id("remote-jira-searchresult")))
            .findElement(By.cssSelector("input[type=checkbox]"))
            .click()

        driver
            .findElement(By.id("linkjiraissue-add-selected"))
            .click()

        driver
            .wait(visibilityOfElementLocated(linkIssueSearchDialog))
    }

    override fun submitIssue() {
        driver
            .wait(visibilityOfElementLocated(By.id("link-jira-issue")))
            .findElement(By.cssSelector("input[type='submit']"))
            .click()

        driver
            .wait(
                and(
                    invisibilityOfElementLocated(linkIssueSearchDialog),
                    visibilityOfElementLocated(By.id("aui-flag-container"))
                ))
    }

    override fun isLinkButtonPresent(): Boolean {
        val moreOperationsButton = driver
            .wait(visibilityOfElementLocated(moreOperationsButtonLocator))
        moreOperationsButton.click()

        val isLinkButtonPresent = driver
            .findElements(linkOperationLocator)
            .isNotEmpty()

        moreOperationsButton.click()

        return isLinkButtonPresent
    }
}
