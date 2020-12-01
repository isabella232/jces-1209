package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.MeasureType.Companion.SPRINT_COMPLETE
import jces1209.vu.MeasureType.Companion.SPRINT_CREATE
import jces1209.vu.MeasureType.Companion.SPRINT_MOVE_ISSUE
import jces1209.vu.MeasureType.Companion.SPRINT_REORDER_ISSUE
import jces1209.vu.MeasureType.Companion.SPRINT_START_SPRINT
import jces1209.vu.api.sprint.SprintApi
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.JiraTips
import jces1209.vu.page.boards.view.ScrumBacklogPage
import jces1209.vu.page.boards.view.ScrumSprintPage

class WorkOnSprint(
    private val meter: ActionMeter,
    private val sprintApi: SprintApi,
    private val jiraTips: JiraTips,
    private val backlogsMemory: SeededMemory<ScrumBacklogPage>,
    private val sprintsMemory: SeededMemory<ScrumSprintPage>,
    private val measure: Measure,
    private val completeSprintProbability: Float,
    private val reorderIssueProbability: Float,
    private val moveIssueProbability: Float,
    private val startSprintProbability: Float,
    private val createSprintProbability: Float
) : Action {

    override fun run() {
        workOnBacklog()
        workOnSprintPage()
    }

    private fun workOnSprintPage() {
        val sprint = sprintsMemory.recall()
        if (sprint == null) {
            println("I cannot recall active sprint board, skipping...")
            return
        }

        sprint
            .goToBoard()
            .waitForBoardPageToLoad()

        reorderIssue(sprint)
        completeSprint(sprint)
    }

    private fun workOnBacklog() {
        val backlog = backlogsMemory.recall()
        if (backlog == null) {
            println("I cannot recall backlog, skipping...")
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
                startSprint(backlog)
            } else {
                println("Scrum backlog doesn't contain issues in backlog")
            }
        } finally {
            if (sprintApi.isReady()) {
                sprintId?.let { sprintApi.deleteSprint(it) }
            }
        }
    }

    private fun completeSprint(sprint: ScrumSprintPage) {
        measure.measure(SPRINT_COMPLETE, completeSprintProbability) {
            if (sprint.isCompleteButtonEnabled()) {
                sprint.completeSprint()
            } else {
                println("Scrum backlog doesn't contain sprint which is ready to be completed")
            }
        }
    }

    private fun reorderIssue(sprint: ScrumSprintPage) {
        measure.measure(SPRINT_REORDER_ISSUE, reorderIssueProbability) {
            if (sprint.maxColumnIssuesNumber() > 1) {
                sprint.reorderIssue()
            } else {
                println("Scrum backlog doesn't contain enough issues to make reorder")
            }
        }
    }

    private fun startSprint(backlog: ScrumBacklogPage) {
        measure.measure(SPRINT_START_SPRINT, startSprintProbability) {
            if (backlog.isStartSprintEnabled()) {
                meter.measure(MeasureType.SPRINT_START_SPRINT_EDITOR) {
                    backlog.openStartSprintPopUp()
                }
                meter.measure(MeasureType.SPRINT_START_SPRINT_SUBMIT) {
                    backlog.submitStartSprint()
                }
            } else {
                println("Can't start sprint - the option is disabled")
            }
        }
    }

    private fun moveIssuesToSprint(backlog: ScrumBacklogPage) {
        measure.measure(SPRINT_MOVE_ISSUE, moveIssueProbability) {
            // Move additional times for reordering
            repeat(10) {
                backlog.moveIssueToSprint()
            }
        }
    }

    private fun createSprint(backlog: ScrumBacklogPage): String? {
        jiraTips.closeTips()
        return measure.measure(SPRINT_CREATE, createSprintProbability) {
            backlog.createSprint()
        }
    }
}
