package jces1209.vu.action.boards

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.ScrumSprintPage
import org.openqa.selenium.WebDriver

class ViewScrumBoard(
    private val driver: WebDriver,
    private val measure: Measure,
    private val scrumBoardsMemory: SeededMemory<ScrumSprintPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float
) : ViewBoard(
    driver = driver,
    measure = measure,
    issueKeyMemory = issueKeyMemory,
    viewBoardMeasureType = MeasureType.VIEW_SCRUM_BOARD,
    issuePreviewMeasureType = MeasureType.ISSUE_PREVIEW_SCRUM_BOARD,
    contextOperationMeasureType = MeasureType.CONTEXT_OPERATION_SCRUM_BOARD,
    configureBoardMeasureType = MeasureType.CONFIGURE_SCRUM_BOARD,
    viewIssueProbability = viewIssueProbability,
    configureBoardProbability = configureBoardProbability,
    contextOperationProbability = contextOperationProbability), Action {

    override fun run() {
        val board = getBoard(scrumBoardsMemory as SeededMemory<BoardPage>)
        if (board == null) {
            logger.debug("I cannot recall a Scrum board, skipping...")
            return
        }

        viewBoardContent(board)
    }
}
