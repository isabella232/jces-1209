package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.MeasureType.Companion.VIEW_SCRUM_BOARD
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.JiraTips
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.cloud.CloudScrumSprintPage
import jces1209.vu.page.contextoperation.ContextOperationBoard
import jces1209.vu.utils.boards.BoardsFrequencyManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.WebDriver

class ViewScrumBoard(
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

        if (!boardType.equals("Sprint")) {
            println("Scrum board wasn't found, skipping...")
            return
        }

        val boardContent = viewBoard(boardType, board)

        if (null != boardContent) {
            measure.roll(viewIssueProbability) {
                if (boardContent.getIssueKeys().isEmpty()) {
                    logger.debug("It requires some issues on board to test preview issue")
                } else {
                    repeat(2) {
                        previewIssue(boardType, board)
                    }
                    contextOperation(boardType)
                }
            }

            jiraTips.closeTips()
            configureBoard(boardType, board)
        }
    }

    private fun viewBoard(boardType: String, board: BoardPage): BoardContent? {
        return measure.measure(VIEW_SCRUM_BOARD,
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

    private fun previewIssue(boardType: String, board: BoardPage) {
        jiraTips.closeTips()
        measure.measure(MeasureType.ISSUE_PREVIEW_SCRUM_BOARD) {
            measure.measure(MeasureType.ISSUE_PREVIEW_SCRUM_BOARD, isSilent = false) {
                board.previewIssue()
            }
        }
            ?.closePreviewIssue()
    }

    private fun configureBoard(boardType: String, board: BoardPage) {
        measure.measure(MeasureType.CONFIGURE_SCRUM_BOARD, configureBoardProbability) {
            measure.measure(MeasureType.CONFIGURE_SCRUM_BOARD, isSilent = false) {
                board
                    .configureBoard()
                    .waitForLoadPage()
            }
        }
    }

    private fun contextOperation(boardType: String) {
        measure.measure(MeasureType.CONTEXT_OPERATION_SCRUM_BOARD, contextOperationProbability) {
            measure.measure(MeasureType.CONTEXT_OPERATION_SCRUM_BOARD, isSilent = false) {
                ContextOperationBoard(driver)
                    .open()
            }
        }
            ?.close()
    }

    private fun getBoard(): BoardPage? {
        val currentUrl = driver.currentUrl
        val boardsManager = BoardsFrequencyManager()
        val boardObject = boardsManager.getBoardByFrequency()
        if (boardObject != null && currentUrl.contains("atlassian.net")) {
            val boardPage = if (boardObject.boardType.contains("sprint")) {
                CloudScrumSprintPage(driver, boardObject.uri)
            } else {
                logger.debug("I cannot parse the board")
            }
            return boardPage  as BoardPage
        } else {
            return boardsMemory.recall()
        }
    }
}

