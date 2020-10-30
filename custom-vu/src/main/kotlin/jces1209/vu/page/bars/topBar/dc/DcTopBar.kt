package jces1209.vu.page.bars.topBar.dc

import jces1209.vu.page.AbstractIssuePage
import jces1209.vu.page.FalliblePage
import jces1209.vu.page.bars.topBar.TopBar
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions

class DcTopBar(
    private val driver: WebDriver
) : TopBar {

    private val page = FalliblePage.Builder(
        webDriver = driver,
        expectedContent = listOf(
            By.id("quickSearchInput"),
            By.id("filter-projects"),
            By.id("projects"),
            By.xpath("//*[@data-project-id]")
        )
    )
        .cloudErrors()
        .build()


    override fun waitForTopBar(): DcTopBar {
        page.waitForPageToLoad()
        return this
    }

    override fun quickSearch(issueKey: String): DcTopBar {
        Actions(driver)
            .sendKeys("/")
            .perform()

        driver
            .wait(
                ExpectedConditions.and(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@class='quick-search-section-heading' and contains(text(),'Issues')]")),
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@aria-tooltip='View all issues']")),
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@aria-tooltip='View all projects']"))
                )
            )
        return this
    }

    override fun selectItem(issueKey: String): AbstractIssuePage {
        TODO("Not yet implemented")
    }
}
