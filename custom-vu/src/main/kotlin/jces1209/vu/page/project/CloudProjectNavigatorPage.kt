package jces1209.vu.page.project

import jces1209.vu.page.CloudIssueNavigator
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions


class CloudProjectNavigatorPage(
    private val driver: WebDriver
) : ProjectNavigatorPage {

    override fun openProject(projectKey: String) {
        driver.navigate().to("/projects/")
        driver
            .wait(ExpectedConditions
                .elementToBeClickable(
                    By.xpath(
                        "//*[@data-test-id='global-pages.directories.directory-base.content.table.container']" +
                            "//*[@href='/browse/$projectKey']")))
            .click()
        waitForNavigator(driver)
    }

    override fun waitForNavigator(driver: WebDriver) {
        CloudIssueNavigator(driver).waitForNavigator()
    }
}
