package jces1209.vu.page.admin.issuetypes

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.lang.Exception
import java.time.Duration

class CloudBrowseIssueTypesPage(
    private val jira: WebJira
) : BrowseIssueTypesPage(jira) {

    private val falliblePage = FalliblePage.Builder(
        jira.driver,
        and(
            loadingPageExpectedCondition,
            presenceOfAllElementsLocatedBy(By.xpath("//*[@data-testid = 'NavigationItem']")),
            presenceOfElementLocated(By.xpath("//*[. = 'Issue types']")),
            presenceOfElementLocated(By.className("search-entry"))
        )
    )
        .cloudErrors()
        .timeout(Duration.ofSeconds(30))
        .build()

    override fun waitForBeingLoaded(): CloudBrowseIssueTypesPage {
        falliblePage.waitForPageToLoad()
        return this
    }
}
