package jces1209.vu.page.customizecolumns

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

abstract class ColumnsEditor(
    private val driver: WebDriver
) {
    val columnCheckbox = By.xpath(".//*[@class='check-list-item']")

    fun selectView(view: Int) {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.xpath(
                ".//button[@id='layout-switcher-button']")),
            timeout = Duration.ofSeconds(15)
        ).click()
        if (view == 1) {
            driver.wait(
                condition = ExpectedConditions.elementToBeClickable(By.xpath(
                    ".//div[@class='aui-list']" +
                        "//*[@data-layout-key='list-view']")),
                timeout = Duration.ofSeconds(5)
            ).click()
        } else if (view == 0) {
            driver.wait(
                condition = ExpectedConditions.elementToBeClickable(By.xpath(
                    ".//div[@class='aui-list']" +
                        "//*[@data-layout-key='split-view']")),
                timeout = Duration.ofSeconds(5)
            ).click()
        }
    }

    fun openEditor() {
        openColumnsList()
        restoreDefaults()
        openColumnsList()
    }

    fun selectItems(itemsCount: Int) {
        driver.wait(
            condition = ExpectedConditions.numberOfElementsToBeMoreThan(columnCheckbox, 0),
            timeout = Duration.ofSeconds(15)
        )
        val items = driver.findElements(columnCheckbox)

        for (i: Int in 0 until itemsCount) {
            driver.wait(
                condition = ExpectedConditions.elementToBeClickable(items[i]),
                timeout = Duration.ofSeconds(15))
            items.get(i).click()
        }
    }

    fun submitSelection() {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.xpath(
                ".//*[@class='aui-button submit']")),
            timeout = Duration.ofSeconds(5)
        ).click()
    }

    private fun openColumnsList() {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.xpath(
                "//button[@title='Columns']")),
            timeout = Duration.ofSeconds(15)
        ).click()
    }

    private fun restoreDefaults() {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.xpath(
                ".//*[@class='aui-button aui-button-link restore-defaults']")),
            timeout = Duration.ofSeconds(5)
        ).click()
        waitForColumnsRestored()
    }

    //default columns are restored with an AJAX request so we
    //have to use thread.sleep() to wait for its execution
    private fun waitForColumnsRestored() {
        while (true) {
            val isRestoreRequestComplete = (driver as JavascriptExecutor).executeScript("return jQuery.active == 0") as Boolean
            if (isRestoreRequestComplete) {
                break
            }
            Thread.sleep(100)
        }
    }
}
