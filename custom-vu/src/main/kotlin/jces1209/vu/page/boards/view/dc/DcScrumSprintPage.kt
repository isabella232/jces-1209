package jces1209.vu.page.boards.view.dc

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.boards.configure.ConfigureBoard
import jces1209.vu.page.boards.configure.DcConfigureBoard
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.ScrumSprintPage

class DcScrumSprintPage(
    jira: WebJira,
    boardId: String
) : ScrumSprintPage(
    driver = jira.driver,
    uri = jira.base.resolve("secure/RapidBoard.jspa?rapidView=$boardId")
) {
    private val dcBoardPage = DcBoardPage(driver, issueSelector)

    override fun waitForBoardPageToLoad(): BoardContent {
        return dcBoardPage.waitForBoardPageToLoad()
    }

    override fun previewIssue(): DcScrumSprintPage {
        dcBoardPage.previewIssue()
        return this
    }

    override fun closePreviewIssue() {
        dcBoardPage.closePreviewIssue()
    }

    override fun configure(): ConfigureBoard {
        return DcConfigureBoard(driver)
    }
}
