package jces1209.vu.action.boards

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.ScrumBacklogPage
import org.openqa.selenium.WebDriver

class ViewBacklog(
    private val driver: WebDriver,
    private val measure: Measure,
    private val backlogBoardsMemory: SeededMemory<ScrumBacklogPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float
) : ViewBoard(
    driver = driver,
    measure = measure,
    issueKeyMemory = issueKeyMemory,
    viewBoardMeasureType = MeasureType.VIEW_BACKLOG_BOARD,
    issuePreviewMeasureType = MeasureType.ISSUE_PREVIEW_BACKLOG,
    contextOperationMeasureType = MeasureType.CONTEXT_OPERATION_BACKLOG,
    configureBoardMeasureType = MeasureType.CONFIGURE_BACKLOG,
    viewIssueProbability = viewIssueProbability,
    configureBoardProbability = configureBoardProbability,
    contextOperationProbability = contextOperationProbability), Action {

    override fun run() {
        val board = getBoard(backlogBoardsMemory as SeededMemory<BoardPage>)
        if (board == null) {
            logger.debug("I cannot recall Backlog, skipping...")
            return
        }

        viewBoardContent(board)
    }
}
