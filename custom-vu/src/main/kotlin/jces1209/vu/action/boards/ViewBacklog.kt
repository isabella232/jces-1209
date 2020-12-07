package jces1209.vu.action.boards

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.boards.view.BoardPage
import org.openqa.selenium.WebDriver

class ViewBacklog(
    private val driver: WebDriver,
    private val measure: Measure,
    private val boardsMemory: SeededMemory<BoardPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float
) : ViewBoard(
    driver = driver,
    measure = measure,
    boardsMemory = boardsMemory,
    issueKeyMemory = issueKeyMemory,
    viewIssueProbability = viewIssueProbability,
    configureBoardProbability = configureBoardProbability,
    contextOperationProbability = contextOperationProbability), Action {

    override fun run() {
        val board = getBoard()
        if (board == null) {
            logger.debug("I cannot recall board, skipping...")
            return
        }
        validateBoardByType(board, "Backlog")

        val boardContent = viewBoard(MeasureType.VIEW_BACKLOG_BOARD, board)

        if (null != boardContent) {
            measure.roll(viewIssueProbability) {
                if (boardContent.getIssueKeys().isEmpty()) {
                    logger.debug("It requires some issues on board to test preview issue")
                } else {
                    repeat(2) {
                        previewIssue(MeasureType.ISSUE_PREVIEW_BACKLOG, board)
                    }
                    contextOperation(MeasureType.CONTEXT_OPERATION_BACKLOG)
                }
            }

            jiraTips.closeTips()
            configureBoard(MeasureType.CONFIGURE_BACKLOG, board)
        }
    }
}
