package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions

class JiraTips(
    private val driver: WebDriver
) {

    fun closeTips() {
        driver
            .findElements(By.className("jira-help-tip"))
            .filter { it.isDisplayed }
            .forEach {
                it
                    .findElement(By.className("helptip-close"))
                    .click()
                driver.wait(ExpectedConditions.invisibilityOf(it))
            }
        driver
            .findElements(By.cssSelector("[role='dialog']"))
            .filter { it.isDisplayed }
            .forEach {
                it
                    .findElement(By.tagName("footer"))
                    .findElements(By.tagName("button"))
                    .last { button -> null == button.getAttribute("disabled") }
                    .click()
                driver.wait(ExpectedConditions.invisibilityOf(it))
            }
        driver
            .findElements(By.id("aui-flag-container"))
            .filter { it.isDisplayed }
            .forEach {
                it
                    .findElement(By.className("icon-close"))
                    .click()
                driver.wait(ExpectedConditions.invisibilityOf(it))
            }
        driver
            .findElements(By.className("aui-inline-dialog-contents"))
            .filter { it.isDisplayed }
            .forEach {
                it
                    .findElement(By.className("ee-action-dismiss"))
                    .click()
                driver.wait(ExpectedConditions.invisibilityOf(it))
            }
    }
}
