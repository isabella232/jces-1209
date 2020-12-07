package jces1209.vu.action.boards

import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.VIEW_BOARD
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.JiraTips
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.cloud.CloudKanbanBoardPage
import jces1209.vu.page.boards.view.cloud.CloudNextGenBoardPage
import jces1209.vu.page.boards.view.cloud.CloudScrumBacklogPage
import jces1209.vu.page.boards.view.cloud.CloudScrumSprintPage
import jces1209.vu.page.contextoperation.ContextOperationBoard
import jces1209.vu.utils.boards.BoardsFrequencyManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.WebDriver

abstract class ViewBoard(
    private val driver: WebDriver,
    private val measure: Measure,
    private val boardsMemory: SeededMemory<BoardPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float
) {
    protected val logger: Logger = LogManager.getLogger(this::class.java)
    protected val jiraTips = JiraTips(driver)

    protected fun viewBoard(measureType: ActionType<Unit>, board: BoardPage): BoardContent? {
        return measure.measure(VIEW_BOARD) {
            measure.measure(measureType,
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
    }

    protected fun contextOperation(measureType: ActionType<Unit>) {
        measure.measure(MeasureType.CONTEXT_OPERATION_BOARD, contextOperationProbability) {
            measure.measure(measureType, isSilent = false) {
                ContextOperationBoard(driver)
                    .open()
            }
        }
            ?.close()
    }

    protected fun previewIssue(measureType: ActionType<Unit>, board: BoardPage) {
        jiraTips.closeTips()
        measure.measure(MeasureType.ISSUE_PREVIEW_BOARD) {
            measure.measure(measureType, isSilent = false) {
                board.previewIssue()
            }
        }
            ?.closePreviewIssue()
    }

    protected fun configureBoard(measureType: ActionType<Unit>, board: BoardPage) {
        measure.measure(MeasureType.CONFIGURE_BOARD, configureBoardProbability) {
            measure.measure(measureType, isSilent = false) {
                board
                    .configureBoard()
                    .waitForLoadPage()
            }
        }
    }

    protected fun validateBoardByType(board: BoardPage, expectedBoardType: String): String {
        val boardType = board.getTypeLabel()
        if (!boardType.equals(expectedBoardType)) {
            throw InterruptedException("$expectedBoardType board wasn't found, skipping...")
        }
        return boardType
    }

    protected fun getBoard(): BoardPage? {
        val currentUrl = driver.currentUrl
        val boardsManager = BoardsFrequencyManager()
        val boardObject = boardsManager.getBoardByFrequency()
        if (boardObject != null && currentUrl.contains("atlassian.net")) {
            val boardPage = with(boardObject.boardType) {
                when {
                   contains("backlog") -> CloudScrumBacklogPage(driver, boardObject.uri)
                    contains("sprint") -> CloudScrumSprintPage(driver, boardObject.uri)
                    contains("kanban") ->  CloudKanbanBoardPage(driver, boardObject.uri)
                    contains("next") ->  CloudNextGenBoardPage(driver, boardObject.uri)
                    else -> logger.debug("I cannot parse the board")
                }
            }
            return boardPage as BoardPage
        } else {
                return boardsMemory.recall()
            }
        }
    }
