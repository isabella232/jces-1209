package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*

class JiraCloudWelcome(
    private val driver: WebDriver
) {

    fun skipToJira() = apply {
        val questionSkip = By.xpath("//*[contains(text(), 'Skip question')]")
        driver.wait(
            or(
                presenceOfElementLocated(By.id("jira")),
                elementToBeClickable(questionSkip)
            )
        )
        repeat(2) {
            driver
                .findElements(questionSkip)
                .forEach { it.click() }
        }
    }
}
