package jces1209.vu.page
import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.page.contextoperation.ContextOperationIssue
import jces1209.vu.wait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfAllElements
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.*
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class CloudIssuePage(
    private val driver: WebDriver
) : AbstractIssuePage {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val bentoSummary = By.cssSelector("[data-test-id='issue.views.issue-base.foundation.summary.heading']")
    private val classicSummary = By.id("key-val")
    private val falliblePage = FalliblePage.Builder(
        expectedContent = listOf(bentoSummary, classicSummary),
        webDriver = driver
    )
        .cloudErrors()
        .build()

    override fun waitForSummary(): AbstractIssuePage {
        falliblePage.waitForPageToLoad()
        return this
    }

    override fun comment(): Commenting {
        return if (isCommentingClassic()) {
            ClassicCloudCommenting(driver)
        } else {
            BentoCommenting(driver)
        }
    }

    override fun editDescription(description: String): CloudIssuePage {
        driver
            .wait(elementToBeClickable(By.cssSelector("[data-test-id = 'issue.views.field.rich-text.description']")))
            .click();

        val descriptionForm = driver
            .wait(
                presenceOfElementLocated(By.cssSelector("[data-test-id='issue.views.field.rich-text.editor-container']"))
            )

        Actions(driver)
            .sendKeys(description)
            .perform()

        descriptionForm
            .findElement(By.cssSelector("[data-testid='comment-save-button']"))
            .click()

        driver.wait(
            invisibilityOfAllElements(descriptionForm)
        )
        return this;
    }

    override fun linkIssue(): CloudIssueLinking {
        return CloudIssueLinking(driver)
    }

    override fun addAttachment(): CloudAddScreenShot {
        return CloudAddScreenShot(driver)
    }

    override fun changeAssignee(): CloudIssuePage {
        return if (isBentoViewEnabled()) changeAssigneeBentoView() else changeAssigneeClassicView()
    }

    override fun contextOperation(): ContextOperationIssue {
        return ContextOperationIssue(driver)
    }

    override fun isTimeSpentFormAppeared(): Boolean {
        try {
            driver
                .wait(
                    ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.id("log-work-time-logged"))
                    )
                )
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun cancelTimeSpentForm(): AbstractIssuePage {
        val transitionCancelLocator = By.id("issue-workflow-transition-cancel")
        driver
            .wait(
                timeout = Duration.ofSeconds(5),
                condition = ExpectedConditions.presenceOfElementLocated(transitionCancelLocator)
            )
            .click()
        driver
            .wait(
                timeout = Duration.ofSeconds(7),
                condition = ExpectedConditions.invisibilityOfElementLocated(transitionCancelLocator)
            )
        return this
    }

    override fun fillInTimeSpentForm(): AbstractIssuePage {
        driver
            .wait(
                ExpectedConditions.presenceOfElementLocated(By.id("log-work-time-logged"))
            )
            .click()
        Actions(driver)
            .sendKeys("1h")
            .perform()
        driver
            .wait(
                ExpectedConditions.presenceOfElementLocated(By.id("comment"))
            )
            .click()
        Actions(driver)
            .sendKeys("comment")
            .perform()
        driver
            .wait(
                ExpectedConditions.presenceOfElementLocated(By.id("issue-workflow-transition-submit"))
            )
            .click()
        driver
            .wait(
                timeout = Duration.ofSeconds(7),
                condition = ExpectedConditions.invisibilityOfElementLocated(By.id("issue-workflow-transition-cancel"))
            )
        driver
            .wait(
                timeout = Duration.ofSeconds(3),
                condition = ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"helpPanelContainer\"]"))
            )
        return this
    }

    private fun waitForNotWatchingElement(): WebElement = driver.wait(
        timeout = Duration.ofSeconds(7),
        condition = ExpectedConditions.elementToBeClickable(By.xpath("//*[@aria-label = 'Not watching']"))
    )

    private fun waitForTransitionButton(): WebElement = driver.wait(
        timeout = Duration.ofSeconds(7),
        condition = ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-test-id = 'issue.views.issue-base.foundation.status.status-field-wrapper']//button"))
    )

    private fun clickOnElementUntilVisible(element: By, conditionOfSuccess: By) {
        WebDriverWait(driver, 7)
            .until {
                try {
                    driver.wait(ExpectedConditions.elementToBeClickable(element))
                        .click()
                    driver.wait(ExpectedConditions.presenceOfElementLocated(conditionOfSuccess))
                    true
                } catch (e: Exception) {
                    false
                }
            }
    }

    override fun transition(): CloudIssuePage {
        falliblePage.waitForPageToLoad()
        waitForNotWatchingElement()
        waitForTransitionButton()
        clickOnElementUntilVisible(By.xpath("//*[@data-test-id = 'issue.views.issue-base.foundation.status.status-field-wrapper']//button"),
            By.xpath("//*[@data-test-id = 'issue.views.issue-base.foundation.status.status-field-wrapper']//*[contains(@data-test-id,\"issue.fields.status.common.ui.status-lozenge\")]")
        )
        driver
            .wait(
                timeout = Duration.ofSeconds(7),
                condition = ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@data-test-id = 'issue.views.issue-base.foundation.status.status-field-wrapper']//*[contains(@data-test-id,\"issue.fields.status.common.ui.status-lozenge\")]"))
            )
            .click()
        return this
    }

    fun changeAssigneeBentoView(): CloudIssuePage {
        driver
            .findElement(By.cssSelector("[data-test-id = 'issue.views.field.user.assignee']"))
            .click();

        var userList = driver
            .wait(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[contains(@class,'fabric-user-picker__menu-list')]/*"))
            )

        if (userList.size == 1 && userList[0].text == "No options") {
            logger.debug("No user options for Assignee")
            throw InterruptedException("No options for Assignee")
        }

        val firstUser = driver
            .wait(presenceOfElementLocated(By.id("react-select-assignee-option-0")))

        var userName = firstUser.text.removeSuffix(" (Assign to me)")
        firstUser.click()
        driver.wait(presenceOfElementLocated(By.xpath("//*[@data-test-id='issue.views.field.user.assignee']//*[.='$userName']")))
        return this;
    }

    private fun changeAssigneeClassicView(): CloudIssuePage {
        driver
            .wait(elementToBeClickable(By.xpath("//span[@id='assignee-val' and not (@inactive)]")))
            .click()
        driver
            .wait(visibilityOfElementLocated(By.xpath("//span[@id='assignee-val'][contains(@class,'active')]")))
        driver
            .wait(visibilityOfElementLocated(By.xpath("//form[@id='assignee-form']//span[contains(@class, 'drop-menu')]")))
            .click()

        val userList = try {
            driver.wait(presenceOfAllElementsLocatedBy(By.xpath("//ul[@id='suggestions']//a")))
        } catch (exc: Exception) {
            logger.debug("No user options for Assignee")
            throw InterruptedException("No options for Assignee")
        }

        val firstUser = driver.findElement(By.xpath("//ul[@id='suggestions']/li[1]/a"))
        var firstUserName = firstUser.getAttribute("text")
        if (userList.size == 1 && firstUserName == "Automatic") {
            firstUserName = "Unassigned"
        }

        firstUser.click()
        driver
            .wait(elementToBeClickable(By.xpath("//button[@type='submit']")))
            .click()
        driver
            .wait(presenceOfElementLocated(By.xpath("//*[text()[contains(.,'$firstUserName')]]")))
        return this;
    }

    private fun isBentoViewEnabled(): Boolean {
        try {
            driver
                .wait(presenceOfElementLocated(bentoSummary))
        } catch (exc: Exception) {
            return false
        }
        return true
    }

    private fun isCommentingClassic(): Boolean = driver
        .findElements(By.id("footer-comment-button"))
        .isNotEmpty()
}
