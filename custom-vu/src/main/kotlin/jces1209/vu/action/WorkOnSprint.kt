package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import jces1209.vu.MeasureType
import jces1209.vu.api.sprint.SprintApi
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.JiraTips
import jces1209.vu.page.boards.view.ScrumBacklogPage
import jces1209.vu.page.boards.view.ScrumSprintPage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.WebDriver

class WorkOnSprint(
    private val driver: WebDriver,
    private val meter: ActionMeter,
    private val sprintApi: SprintApi,
    private val jiraTips: JiraTips,
    private val backlogsMemory: SeededMemory<ScrumBacklogPage>,
    private val sprintsMemory: SeededMemory<ScrumSprintPage>
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        workOnBacklog()
        workOnSprintPage()
    }

    private fun workOnSprintPage() {
        val sprint = sprintsMemory.recall()
        if (sprint == null) {
            logger.debug("I cannot recall active sprint board, skipping...")
            return
        }

        sprint
            .goToBoard()
            .waitForBoardPageToLoad()

        if (sprint.maxColumnIssuesNumber() > 1) {
            reorderIssue(sprint)
        } else {
            logger.debug("Scrum backlog doesn't contain enough issues to make reorder")
        }

        if (sprint.isCompleteButtonPresent()) {
            completeSprint(sprint)
        } else {
            logger.debug("Scrum backlog doesn't contain sprint which is ready to be completed")
        }
    }

    private fun workOnBacklog() {
        val backlog = backlogsMemory.recall()
        if (backlog == null) {
            logger.debug("I cannot recall backlog, skipping...")
            return
        }

        backlog
            .goToBoard()
            .waitForBoardPageToLoad()
        var sprintId: String? = null
        try {
            sprintId = createSprint(backlog)

            jiraTips.closeTips()
            if (backlog.backlogIssuesNumber() > 0) {
                moveIssuesToSprint(backlog)

                if (backlog.isStartSprintEnabled()) {
                    startSprint(backlog)
                } else {
                    logger.debug("Can't start sprint - the option is disabled")
                }
            } else {
                logger.debug("Scrum backlog doesn't contain issues in backlog")
            }
        } finally {
            if (sprintApi.isReady()) {
                sprintId?.let { sprintApi.deleteSprint(it) }
            }
        }
    }

    private fun completeSprint(sprint: ScrumSprintPage) {
        meter.measure(MeasureType.SPRINT_COMPLETE) {
            sprint.completeSprint()
        }
    }

    private fun reorderIssue(sprint: ScrumSprintPage) {
        meter.measure(MeasureType.SPRINT_REORDER_ISSUE) {
            sprint.reorderIssue()
        }
    }

    private fun startSprint(backlog: ScrumBacklogPage) {
        meter.measure(MeasureType.SPRINT_START_SPRINT) {
            meter.measure(MeasureType.SPRINT_START_SPRINT_EDITOR) {
                backlog.openStartSprintPopUp()
            }
            meter.measure(MeasureType.SPRINT_START_SPRINT_SUBMIT) {
                backlog.submitStartSprint()
            }
        }
    }

    private fun moveIssuesToSprint(backlog: ScrumBacklogPage) {
        meter.measure(MeasureType.SPRINT_MOVE_ISSUE) {
            backlog.moveIssueToSprint()
        }

        // Move additional times for reordering
        repeat(10) {
            backlog.moveIssueToSprint()
        }
    }

    private fun createSprint(backlog: ScrumBacklogPage): String? {
        jiraTips.closeTips()
        return meter.measure(MeasureType.SPRINT_CREATE) {
            backlog.createSprint()
        }
    }
}
