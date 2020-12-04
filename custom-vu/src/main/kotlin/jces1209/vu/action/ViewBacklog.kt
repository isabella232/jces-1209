package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.VIEW_BOARD
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.JiraTips
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.cloud.CloudScrumBacklogPage
import jces1209.vu.page.boards.view.cloud.CloudScrumSprintPage
import jces1209.vu.page.contextoperation.ContextOperationBoard
import jces1209.vu.utils.boards.BoardsFrequencyManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.WebDriver

class ViewBacklog(
    private val driver: WebDriver,
    private val measure: Measure,
    private val boardsMemory: SeededMemory<BoardPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val jiraTips = JiraTips(driver)

    override fun run() {
        val board = getBoard()
        if (board == null) {
            logger.debug("I cannot recall board, skipping...")
            return
        }
        val boardType = board.getTypeLabel()
        println(boardType)

        if (!boardType.equals("Backlog")) {
            println("Backlog wasn't found, skipping...")
            return
        }

        val boardContent = viewBoard(board)

        if (null != boardContent) {
            measure.roll(viewIssueProbability) {
                if (boardContent.getIssueKeys().isEmpty()) {
                    logger.debug("It requires some issues on board to test preview issue")
                } else {
                    repeat(2) {
                        previewIssue(board)
                    }
                    contextOperation(boardType)
                }
            }

            jiraTips.closeTips()
            configureBoard(boardType, board)
        }
    }

    private fun getBoard(): BoardPage? {
        val currentUrl = driver.currentUrl
        val boardsManager = BoardsFrequencyManager()
        val boardObject = boardsManager.getBoardByFrequency()
        if (boardObject != null && currentUrl.contains("atlassian.net")) {
            val boardPage = if (boardObject.boardType.contains("backlog")) {
                CloudScrumBacklogPage(driver, boardObject.uri)
            } else {
                logger.debug("I cannot parse the board")
            }
            return boardPage as BoardPage
        } else {
            return boardsMemory.recall()
        }
    }

    private fun viewBoard(board: BoardPage): BoardContent? {
        return measure.measure(MeasureType.VIEW_BACKLOG_BOARD,
            isSilent = false,
            observation = {
                issueKeyMemory.remember(it.getIssueKeys())
                IssuesOnBoard(it.getIssueCount()).serialize()
            }
        ) {
            board
                .goToBoard()
                .waitForBoardPageToLoad()
        }
    }

    private fun previewIssue(board: BoardPage) {
        jiraTips.closeTips()
        measure.measure(MeasureType.ISSUE_PREVIEW_BACKLOG) {
            measure.measure(MeasureType.ISSUE_PREVIEW_BACKLOG, isSilent = false) {
                board.previewIssue()
            }
        }
            ?.closePreviewIssue()
    }

    private fun contextOperation(boardType: String) {
        measure.measure(MeasureType.CONTEXT_OPERATION_BACKLOG, contextOperationProbability) {
            measure.measure(MeasureType.CONTEXT_OPERATION_BACKLOG, isSilent = false) {
                ContextOperationBoard(driver)
                    .open()
            }
        }
            ?.close()
    }

    private fun configureBoard(boardType: String, board: BoardPage) {
        measure.measure(MeasureType.CONFIGURE_BACKLOG, configureBoardProbability) {
            measure.measure(MeasureType.CONFIGURE_BACKLOG, isSilent = false) {
                board
                    .configureBoard()
                    .waitForLoadPage()
            }
        }
    }
}
