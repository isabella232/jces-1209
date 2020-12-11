package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import org.openqa.selenium.support.ui.ExpectedConditions

class ClassicCloudCommenting(
    private val driver: WebDriver
) : Commenting {

    private val commentButton = By.id("footer-comment-button")
    private val falliblePage = FalliblePage.Builder(
        expectedContent = listOf(commentButton),
        webDriver = driver
    )
        .cloudErrors()
        .build()

    override fun openEditor() {
        falliblePage.waitForPageToLoad()
        driver
            .wait(elementToBeClickable(commentButton))
            .click()
        waitForEditor()
    }

    private fun waitForEditor() {
        driver
            .wait(elementToBeClickable(By.id("comment")))
            .click()
    }

    override fun typeIn(comment: String) {
        Actions(driver)
            .sendKeys(comment)
            .perform()
    }

    override fun saveComment() {
        driver.findElement(By.id("issue-comment-add-submit")).click()
    }

    override fun waitForTheNewComment() {
        driver.wait(visibilityOfElementLocated(By.cssSelector(".activity-comment.focused")))
    }

    override fun mentionUser() {
        Actions(driver)
            .sendKeys("@assignee")
            .perform()
        driver
            .wait(ExpectedConditions.presenceOfElementLocated(By.id("mentionDropDown")))
        driver
            .wait(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@class = 'jira-mention-issue-roles']//*[. = 'assignee']")))
            .click()
    }
}
