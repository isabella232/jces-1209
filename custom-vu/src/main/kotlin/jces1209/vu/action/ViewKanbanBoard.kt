package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.JiraTips
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.boards.view.cloud.CloudKanbanBoardPage
import jces1209.vu.page.boards.view.cloud.CloudScrumSprintPage
import jces1209.vu.page.contextoperation.ContextOperationBoard
import jces1209.vu.utils.boards.BoardsFrequencyManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.WebDriver

class ViewKanbanBoard(
    private val driver: WebDriver,
    private val measure: Measure,
    private val boardsMemory: SeededMemory<BoardPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float,
    private val changeIssueStatusProbability: Float
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

        if (!boardType.equals("Kanban")) {
            println("Kanban board wasn't found, skipping...")
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
                    contextOperation()
                }
            }

            jiraTips.closeTips()
            configureBoard(board)
            repeat(2) {
                changeIssueStatus(board)
            }
        }
    }

    private fun viewBoard(board: BoardPage): BoardContent? {
        return measure.measure(MeasureType.VIEW_KANBAN_BOARD,
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
        measure.measure(MeasureType.ISSUE_PREVIEW_KANBAN_BOARD) {
            measure.measure(MeasureType.ISSUE_PREVIEW_KANBAN_BOARD, isSilent = false) {
                board.previewIssue()
            }
        }
            ?.closePreviewIssue()
    }

    private fun contextOperation() {
        measure.measure(MeasureType.CONTEXT_OPERATION_KANBAN_BOARD, contextOperationProbability) {
            measure.measure(MeasureType.CONTEXT_OPERATION_KANBAN_BOARD, isSilent = false) {
                ContextOperationBoard(driver)
                    .open()
            }
        }
            ?.close()
    }

    private fun configureBoard(board: BoardPage) {
        measure.measure(MeasureType.CONFIGURE_KANBAN_BOARD, configureBoardProbability) {
            measure.measure(MeasureType.CONFIGURE_KANBAN_BOARD, isSilent = false) {
                board
                    .configureBoard()
                    .waitForLoadPage()
            }
        }
    }

    private fun changeIssueStatus(board: BoardPage) {
        if (board is KanbanBoardPage) {
            measure.measure(MeasureType.MOVE_ISSUE_STATUS_BOARD, changeIssueStatusProbability) {
                val movingIssue = board.movingIssue()

                val issue = movingIssue.moveIssueToOtherColumn()
                if (movingIssue.isModalWindowDisplayed()) {
                    if (movingIssue.isContinueButtonEnabled()) {
                        measure.measure(MeasureType.MOVE_ISSUE_STATUS_BOARD_SUBMIT_WINDOW, isSilent = false) {
                            movingIssue.clickContinueButton(issue)
                            if (movingIssue.isModalWindowDisplayed()) {
                                movingIssue.closeWindows()

                                val errorMessage = "Failed to submit issue transition [${issue.key}]"
                                logger.debug(errorMessage)
                                throw InterruptedException(errorMessage)
                            }
                        }
                    } else {
                        movingIssue.closeWindows()

                        val errorMessage = "Issue [${issue.key}] can't be moved to other column"
                        logger.debug(errorMessage)
                        throw InterruptedException(errorMessage)
                    }
                }
            }
        }
    }

    private fun getBoard(): BoardPage? {
        val currentUrl = driver.currentUrl
        val boardsManager = BoardsFrequencyManager()
        val boardObject = boardsManager.getBoardByFrequency()
        if (boardObject != null && currentUrl.contains("atlassian.net")) {
            val boardPage = if (boardObject.boardType.contains("kanban")) {
                CloudKanbanBoardPage(driver, boardObject.uri)
            } else {
                logger.debug("I cannot parse the board")
            }
            return boardPage  as BoardPage
        } else {
            return boardsMemory.recall()
        }
    }
}
