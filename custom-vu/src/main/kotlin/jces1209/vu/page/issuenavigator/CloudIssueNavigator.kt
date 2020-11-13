package jces1209.vu.page.issuenavigator

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage
import jces1209.vu.page.issuenavigator.bulkoperation.BulkOperationPage
import jces1209.vu.page.ViewSubscriptions.ViewSubscriptions
import jces1209.vu.page.ViewSubscriptions.cloud.CloudViewSubscriptions
import jces1209.vu.page.issuenavigator.bulkoperation.CloudBulkOperationPage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration
import java.util.*

class CloudIssueNavigator(
    jira: WebJira
) : IssueNavigator(jira) {
    private val meatballTriggerLocator = By.id("jira-meatball-trigger")
    private val bulkEditAllLocator = By.id("bulkedit_all")
    private val falliblePage = FalliblePage.Builder(
        driver,
        or(
            and(
                or(
                    presenceOfElementLocated(By.cssSelector("ol.issue-list")),
                    presenceOfElementLocated(By.id("issuetable")),
                    presenceOfElementLocated(By.id("issue-content")),
                    presenceOfElementLocated(filterDetailsLocator),
                    presenceOfElementLocated(By.xpath("//div[@role='grid']")),
                    presenceOfElementLocated(By.className("ghx-issue-content"))
                ),
                or(
                    presenceOfElementLocated(By.id("jira-issue-header")),
                    presenceOfElementLocated(By.id("key-val"))
                ),
                or(
                    presenceOfElementLocated(By.id("new-issue-body-container")),
                    presenceOfElementLocated(By.className("issue-body-content")),
                    presenceOfElementLocated(By.xpath("//div[@data-test-id='issue.views.issue-details.issue-layout.issue-layout']"))
                )
            ),
            and(
                presenceOfElementLocated(By.id("summary-val")),
                presenceOfElementLocated(By.id("status-val")),
                presenceOfElementLocated(By.className("action-details"))
            ),
            and(
                presenceOfElementLocated(By.cssSelector("[data-test-id='issue.views.issue-base.foundation.summary.heading']")),
                presenceOfElementLocated(By.cssSelector("[data-test-id='issue.views.issue-base.foundation.status.status-field-wrapper']")),
                presenceOfElementLocated(By.cssSelector("[data-test-id='issue.views.issue-details.issue-layout.footnote']"))
            ),
            and(
                visibilityOfElementLocated(By.id("issuetable")),
                visibilityOfElementLocated(By.id("layout-switcher-toggle")),
                presenceOfAllElementsLocatedBy(By.cssSelector("[data-issue-key]")),
                presenceOfAllElementsLocatedBy(By.cssSelector(".summary, .components, .assignee, .reporter, .priority, .status"))
            ),
            presenceOfElementLocated(By.className("no-results-hint")) // TODO is it too optimistic like in SearchServerFilter.waitForIssueNavigator ?
        )
    )
        .cloudErrors()
        .timeout(Duration.ofSeconds(120))
        .build()

    override val filterSubscriptionFalliblePage = FalliblePage.Builder(
        driver,
        and(
            presenceOfElementLocated(By.id("-dialog")),
            filterSubscriptionList
        )
    )
        .cloudErrors()
        .timeout(Duration.ofSeconds(30))
        .build()

    override val viewSubscriptions = CloudViewSubscriptions(jira)

    override fun waitForBeingLoaded(): CloudIssueNavigator {
        falliblePage.waitForPageToLoad()
        return this
    }

    override fun selectIssue() {
        val element = getIssueElementFromList()
        val title = element.getAttribute("title")
        element.click()
        driver.wait(
            and(
                presenceOfElementLocated(By.xpath("//*[@data-test-id = 'issue.views.issue-base.foundation.summary.heading' and contains(text(), '$title')]")),
                visibilityOfElementLocated(By.xpath("//*[@aria-label='Not watching']")),
                visibilityOfElementLocated(By.xpath("//*[@data-test-id='issue.activity.comment']")),
                visibilityOfElementLocated(By.xpath("//*[@aria-label='Add attachment']"))
            )
        )
    }

    override fun openBulkOperation(): BulkOperationPage {
        driver
            .wait(elementToBeClickable(meatballTriggerLocator))
            .click()
        driver
            .wait(visibilityOfElementLocated(bulkEditAllLocator))
        driver
            .wait(visibilityOfElementLocated(bulkEditAllLocator))
            .click()
        return CloudBulkOperationPage(jira)
            .waitForBeingLoaded()
    }

    private fun getIssueElementFromList(): WebElement {
        val elements = driver.wait(
            presenceOfAllElementsLocatedBy(By.xpath("//*[@class ='issue-list']/*[not(@class ='focused')]"))
        )
        val rndIndex = Random().nextInt(elements.size - 1)
        return elements[rndIndex]
    }
}
