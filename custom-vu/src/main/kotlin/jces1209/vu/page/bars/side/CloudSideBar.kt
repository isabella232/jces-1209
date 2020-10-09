package jces1209.vu.page.bars.side

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.page.issuenavigator.CloudIssueNavigator
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.boards.view.ScrumBacklogPage
import jces1209.vu.page.boards.view.ScrumSprintPage
import jces1209.vu.page.boards.view.cloud.CloudKanbanBoardPage
import jces1209.vu.page.boards.view.cloud.CloudScrumBacklogPage
import jces1209.vu.page.boards.view.cloud.CloudScrumSprintPage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.net.URI
import java.time.Duration

class CloudSideBar(
    jira: WebJira
) : SideBar(jira) {
    val backlogLocator = getNavigatorItemLocator("Backlog")
    val sprintLocator = getNavigatorItemLocator("Active sprints")

    override fun isBacklogPresent(): Boolean {
        return driver
            .findElements(backlogLocator)
            .isNotEmpty()
    }

    override fun clickBacklog(): ScrumBacklogPage {
        return CloudScrumBacklogPage(driver, processBoardLink(driver.wait(elementToBeClickable(backlogLocator))))
    }

    override fun isSprintPresent(): Boolean {
        return driver
            .findElements(sprintLocator)
            .isNotEmpty()
    }

    override fun clickSprint(): ScrumSprintPage {
        return CloudScrumSprintPage(driver, processBoardLink(driver.wait(elementToBeClickable(sprintLocator))))
    }

    override fun isSelectBoardPresent(): Boolean {
        return getOtherBoardsList().isNotEmpty()
    }

    override fun selectOtherBoard(): KanbanBoardPage {
        return CloudKanbanBoardPage(driver, processBoardLink(getOtherBoardsList().first()))
    }

    override fun clickOpenIssues(): CloudIssueNavigator {
        return clickSearchNavigatorItem("Open issues")
    }

    override fun clickAllIssues(): CloudIssueNavigator {
        return clickSearchNavigatorItem("All issues")
    }

    private fun getOtherBoardsList(): List<WebElement> {
        driver
            .wait(visibilityOfElementLocated(By.cssSelector("[data-test-id='navigation-apps.scope-switcher-v2']")))
            .click()

        return driver
            .wait(visibilityOfAllElementsLocatedBy(By.cssSelector("a[id^='react-select']:not([id\$='-0'])")))
    }

    private fun processBoardLink(boardLink: WebElement): URI {
        val boardId = boardLink.getAttribute("href")

        boardLink.click()
        return URI(boardId);
    }

    private fun getNavigatorItemLocator(itemName: String): By {
        return By.xpath("//div[@data-testid='ContextualNavigation']//span[@data-item-title='true' and text() = '$itemName']")
    }

    private val loadingLocator = By.cssSelector(".details-layout > .loading")

    private fun clickSearchNavigatorItem(itemName: String): CloudIssueNavigator {
        val navigatorItem = driver
            .wait(elementToBeClickable(getNavigatorItemLocator(itemName)))

        val issues = driver.findElements(By.cssSelector("[data-issuekey]"))
        navigatorItem.click()

        driver
            .wait(
                Duration.ofSeconds(60),
                and(
                    invisibilityOfAllElements(issues),
                    invisibilityOfElementLocated(loadingLocator)
                )
            )
        return CloudIssueNavigator(jira)
    }
}
