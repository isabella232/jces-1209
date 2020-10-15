package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.*
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import jces1209.vu.ConfigProperties
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.MeasureType.Companion.ATTACH_SCREENSHOT
import jces1209.vu.MeasureType.Companion.CONTEXT_OPERATION_ISSUE
import jces1209.vu.MeasureType.Companion.ISSUE_EDIT_DESCRIPTION
import jces1209.vu.MeasureType.Companion.ISSUE_LINK
import jces1209.vu.MeasureType.Companion.ISSUE_LINK_LOAD_FORM
import jces1209.vu.MeasureType.Companion.ISSUE_LINK_SEARCH_CHOOSE
import jces1209.vu.MeasureType.Companion.ISSUE_LINK_SUBMIT
import jces1209.vu.MeasureType.Companion.OPEN_MEDIA_VIEWER
import jces1209.vu.TrafficDataParser
import jces1209.vu.page.AbstractIssuePage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Works for both Cloud and Data Center.
 */
class WorkOnIssue(
    private val issuePage: AbstractIssuePage,
    private val jira: WebJira,
    private val measure: Measure,
    private var issueKeyMemory: IssueKeyMemory,
    private val seededRandom: SeededRandom,
    private val editProbability: Float,
    private val commentProbability: Float,
    private val linkIssueProbability: Float,
    private val attachScreenShotProbability: Float,
    private val changeAssigneeProbability: Float,
    private val mentionUserProbability: Float,
    private val contextOperationProbability: Float,
    private val transitionProbability: Float
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {

        val issueKeyMemoryIdsList: List<String> = getIssueKeyMemoryIds()
        if(issueKeyMemoryIdsList.isNotEmpty()){
            issueKeyMemory = AdaptiveIssueKeyMemory(seededRandom)
            issueKeyMemory.remember(issueKeyMemoryIdsList)
        }

        val issueKey = issueKeyMemory.recall()
        if (issueKey == null) {
            logger.debug("I don't recall any issue keys. Maybe next time I will.")
            return
        }
        val loadedIssuePage = read(issueKey)
        if (null != loadedIssuePage) {
            editDescription(loadedIssuePage)
            linkIssue(loadedIssuePage, issueKey.substringBefore("-"))
            comment(loadedIssuePage)
            measure.roll(attachScreenShotProbability) {
                attachScreenShot(loadedIssuePage)
                openScreenShot()
            }
            changeAssignee(loadedIssuePage)
            mentionUser(loadedIssuePage)
            contextOperation(loadedIssuePage)
            transition(loadedIssuePage)
        }
    }

    private fun read(
        issueKey: String
    ): AbstractIssuePage? = measure.measure(VIEW_ISSUE) {
        jira.goToIssue(issueKey)
        issuePage.waitForSummary()
    }

    private fun editDescription(issuePage: AbstractIssuePage) {
        measure.measure(ISSUE_EDIT_DESCRIPTION, editProbability) {
            issuePage.editDescription("updated")
        }
        logger.debug("I want to edit the $issuePage")
    }

    /*
        issue must have other issues in a project to link them
     */
    private fun linkIssue(issuePage: AbstractIssuePage, issuePrefixSearch: String) {
        measure.roll(linkIssueProbability) {
            val issueLinking = issuePage.linkIssue()
            if (issueLinking.isLinkButtonPresent()) {
                measure.measure(ISSUE_LINK) {
                    measure.measure(ISSUE_LINK_LOAD_FORM, isSilent = false) {
                        issueLinking.openEditor()
                    }
                    measure.measure(ISSUE_LINK_SEARCH_CHOOSE, isSilent = false) {
                        issueLinking.searchAndChooseIssue(issuePrefixSearch)
                    }
                    measure.measure(ISSUE_LINK_SUBMIT, isSilent = false) {
                        issueLinking.submitIssue()
                    }
                }
            } else {
                logger.debug("Issue doesn't have link button")
            }
        }
    }

    private fun comment(issuePage: AbstractIssuePage) {
        val commenting = issuePage.comment()
        measure.measure(ADD_COMMENT, commentProbability) {
            commenting.openEditor()
            commenting.typeIn("abc def")
            measure.measure(ADD_COMMENT_SUBMIT, isSilent = false) {
                commenting.saveComment()
                commenting.waitForTheNewComment()
            }
        }
    }

    private fun attachScreenShot(issuePage: AbstractIssuePage) {
        val attachScreenShot = issuePage.addAttachment()
        attachScreenShot.makeScreenShot()
        measure.measure(ATTACH_SCREENSHOT) {
            attachScreenShot.pasteScreenShot()
        }
    }

    private fun openScreenShot() {
        measure.measure(OPEN_MEDIA_VIEWER) {
            issuePage.addAttachment().openScreenShot()
        }
            ?.closeMediaViewModal()
    }

    private fun mentionUser(issuePage: AbstractIssuePage) {
        val commenting = issuePage.comment()
        measure.measure(ActionType("Mention a user") { Unit }, mentionUserProbability) {
            commenting.openEditor()
            commenting.typeIn("abc def ")
            commenting.mentionUser()
            measure.measure(ADD_COMMENT_SUBMIT, isSilent = false) {
                commenting.saveComment()
                commenting.waitForTheNewComment()
            }
        }
    }

    private fun changeAssignee(issuePage: AbstractIssuePage) {
        measure.measure(ActionType("Change Assignee") { Unit }, changeAssigneeProbability) {
            issuePage.changeAssignee()
        }
    }

    private fun contextOperation(issuePage: AbstractIssuePage) {
        measure.measure(CONTEXT_OPERATION_ISSUE, contextOperationProbability) {
            issuePage
                .contextOperation()
                .open()
        }
            ?.close()
    }

    private fun transition(issuePage: AbstractIssuePage) {
        logger.info("transition start")
        measure.roll(transitionProbability) {
            issuePage.transition()
            //TODO("use ExpectedConditions.or() instead of isTimeSpentFormAppeared")
            val isTimeSpentFormAppeared = issuePage.isTimeSpentFormAppeared()
            if (isTimeSpentFormAppeared)
                issuePage.cancelTimeSpentForm()

            measure.measure(MeasureType.TRANSITION) {
                issuePage.transition()
                if (isTimeSpentFormAppeared) {
                    measure.measure(MeasureType.TRANSITION_FILL_IN_TIME_SPENT_FORM, isSilent = false) {
                        issuePage.fillInTimeSpentForm()
                    }
                }
            }
        }
    }

    private fun getIssueKeyMemoryIds() : List<String> {
        var issueKeyMemoryIdsList: List<String> = listOf()
        val readTrafficShapeConfig = System.getenv("readTrafficShapeConfig")

        if (!readTrafficShapeConfig.isNullOrEmpty()) {
            val resourceName = TrafficDataParser.parseData(jira.base.host, readTrafficShapeConfig)
            val properties = ConfigProperties.load(resourceName)

            val issueKeyMemoryIds: String = properties.getProperty("action.issueKeyMemoryIds")
            issueKeyMemoryIdsList = issueKeyMemoryIds.split(",").map { it.trim() }
        }
        return issueKeyMemoryIdsList
    }
}
