package jces1209.vu.page.dashboard

import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.page.FalliblePage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

abstract class DashboardPage(
    protected val driver: WebDriver
) {
    private val addGadgetLocator = By.id("add-gadget")

    abstract fun createDashboard(): String

    fun clickPopularIfPresent(): DashboardPage {
        val popularButtons = driver
            .findElements(By.id("popular-dash-tab"))
        if (popularButtons.isNotEmpty()) {
            popularButtons
                .first()
                .click()

            waitForDashboards()
        }
        return this;
    }

    fun waitForDashboards(): DashboardPage {
        driver
            .wait(
                or(
                    visibilityOfElementLocated(By.xpath("//a[contains(@href,'/secure/Dashboard.jspa?selectPageId')]")),
                    visibilityOfElementLocated(By.xpath("//*[@id='pp_favourite']/tbody/tr/td[text()='You have no favorite dashboards.']")),
                    visibilityOfElementLocated(By.xpath("//*[@id='pp_popular']/tbody/tr/td[text()='There are no shared dashboards.']"))
                )
            )
        return this;
    }

    fun selectDashboardIfPresent(dashboardName: String): DashboardPage {
        val dashboardLinks = driver
            .findElements(By.xpath("//*[@class='favourite-item']/a[text()='$dashboardName']"))
        if (dashboardLinks.isNotEmpty()) {
            dashboardLinks.first().click()
            waitForGadgetsLoad()
        }
        return this;
    }

    fun openDashboard(): DashboardPage {
        driver.wait(
            condition = visibilityOfAllElementsLocatedBy(
                By.xpath("(//a[contains(@href,'/secure/Dashboard.jspa?selectPageId') and not(@class)])")),
            timeout = Duration.ofSeconds(50)
        )
            .filter { it.text.trim() != "System Dashboard" }
            .shuffled().first().click()
        return this
    }

    fun waitForGadgetsLoad() {
        FalliblePage.Builder(
            expectedContent = or(
                elementToBeClickable(addGadgetLocator),
                visibilityOfElementLocated(By.className("dashboard-item-frame gadget-container")),
                and(
                    visibilityOfElementLocated(By.className("aui-page-header-main")),
                    visibilityOfElementLocated(By.id("tools-dropdown-icon")))
            ),
            webDriver = driver
        )
            .timeout(Duration.ofSeconds(30))
            .cloudErrors()
            .build()
            .waitForPageToLoad()

        driver
            .wait(
                timeout = Duration.ofSeconds(30),
                condition = invisibilityOfElementLocated(By.className("loading")))
    }

    fun createGadget(projectName: String) {
        val initialChartsCount = if (
            driver.wait(presenceOfAllElementsLocatedBy(By.className("add-gadget-link")))
                .size == 3
            ) 0 else {
            driver.wait(presenceOfAllElementsLocatedBy(By.className("bubble-chart-component-plot")))
                .size
        }

        driver
            .wait(elementToBeClickable(addGadgetLocator))
            .click()

        driver
            .wait(elementToBeClickable(
                By.cssSelector("button[data-item-id='com.atlassian.jira.gadgets:bubble-chart-dashboard-item']")))
            .click()
        driver
            .findElements(By.cssSelector(".button-close-gadgets-dialog, .aui-iconfont-close-dialog"))
            .first { it.isDisplayed }
            .click()
        driver
            .wait(invisibilityOfElementLocated(By.id("gadget-dialog")))

        driver
            .wait(elementToBeClickable(By.cssSelector("[id$='project-filter-picker']")))
            .sendKeys(projectName)
        driver
            .wait(visibilityOfElementLocated(By.className("aui-list-item-link")))
            .click()
        driver
            .wait(elementToBeClickable(By.cssSelector(".button.submit")))
            .click()
        driver
            .wait(numberOfElementsToBeMoreThan(By.cssSelector(".bubble-chart-component-plot, .aui-message-error"), initialChartsCount))
    }
}
