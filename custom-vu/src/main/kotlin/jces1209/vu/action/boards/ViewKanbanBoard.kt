package jces1209.vu.action.boards

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.KanbanBoardPage
import org.openqa.selenium.WebDriver

class ViewKanbanBoard(
    private val driver: WebDriver,
    private val measure: Measure,
    private val kanbanBoardsMemory: SeededMemory<KanbanBoardPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float,
    private val changeIssueStatusProbability: Float
) : ViewBoard(
    driver = driver,
    measure = measure,
    issueKeyMemory = issueKeyMemory,
    configureBoardProbability = configureBoardProbability,
    contextOperationProbability = contextOperationProbability), Action {

    override fun run() {
        val board = getBoard(kanbanBoardsMemory as SeededMemory<BoardPage>)
        if (board == null) {
            logger.debug("I cannot recall board, skipping...")
            return
        }

        val boardContent = viewBoard(MeasureType.VIEW_KANBAN_BOARD, board)

        if (null != boardContent) {
            measure.roll(viewIssueProbability) {
                if (boardContent.getIssueKeys().isEmpty()) {
                    logger.debug("It requires some issues on board to test preview issue")
                } else {
                    repeat(2) {
                        previewIssue(MeasureType.ISSUE_PREVIEW_KANBAN_BOARD, board)
                    }
                    contextOperation(MeasureType.CONTEXT_OPERATION_KANBAN_BOARD)
                }
            }

            jiraTips.closeTips()
            configureBoard(MeasureType.CONFIGURE_KANBAN_BOARD, board)
            repeat(2) {
                changeIssueStatus(board)
            }
        }
    }

    private fun changeIssueStatus(board: BoardPage) {
        board as KanbanBoardPage
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
