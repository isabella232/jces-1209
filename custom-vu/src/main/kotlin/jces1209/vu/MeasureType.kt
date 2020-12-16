package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.ActionType

class MeasureType {

    companion object {

        @JvmField
        val BROWSE_CUSTOM_FIELDS = ActionType("Browse Custom fields") { Unit }

        @JvmField
        val BROWSE_FIELD_SCREENS = ActionType("Browse Field Screens") { Unit }

        @JvmField
        val BROWSE_FIELD_CONFIGURATIONS = ActionType("Browse Field Configurations") { Unit }

        @JvmField
        var BROWSE_ISSUE_TYPES = ActionType("Browse Issue Types") { Unit }

        @JvmField
        var BROWSE_PROJECT_ROLES = ActionType("Browse Project Roles") { Unit }

        @JvmField
        val BROWSE_WORKFLOWS = ActionType("Browse Workflows") { Unit }

        @JvmField
        val VIEW_WORKFLOW = ActionType("View Workflow") { Unit }

        @JvmField
        val EDIT_WORKFLOW = ActionType("Edit Workflow") { Unit }

        @JvmField
        val CREATE_WORKFLOW = ActionType("Create Workflow") { Unit }

        @JvmField
        val BULK_EDIT = ActionType("Bulk Edit") { Unit }

        @JvmField
        val BULK_EDIT_OPEN_PAGE = ActionType("Bulk Edit (Open page)") { Unit }

        @JvmField
        val BULK_EDIT_SELECT_ISSUES = ActionType("Bulk Edit (Select issues)") { Unit }

        @JvmField
        val BULK_EDIT_SELECT_OPERATION = ActionType("Bulk Edit (Select Operation)") { Unit }

        @JvmField
        val BULK_EDIT_OPERATION_DETAILS = ActionType("Bulk Edit (Edit comment)") { Unit }

        @JvmField
        val BULK_EDIT_CONFIRMATION = ActionType("Bulk Edit (Confirm)") { Unit }

        @JvmField
        val BULK_EDIT_PROGRESS = ActionType("Bulk Edit (Wait for 100% progress)") { Unit }

        @JvmField
        val BULK_OPERATION_ACKNOWLEDGE = ActionType("Bulk Edit (Acknowledge)") { Unit }

        @JvmField
        val CONTEXT_OPERATION_BOARD = ActionType("Context operation (Board)") { Unit }

        @JvmField
        val CONTEXT_OPERATION_ISSUE = ActionType("Context operation (Issue)") { Unit }

        @JvmField
        val CUSTOMIZE_COLUMNS = ActionType("Customize columns") { Unit }

        @JvmField
        val INLINE_ISSUE_CREATE_BACKLOG = ActionType("Inline issue creation on Backlog") { Unit }

        @JvmField
        val ISSUE_PREVIEW_BOARD = ActionType("Preview issue (Board)") { Unit }

        @JvmField
        val ISSUE_EDIT_DESCRIPTION = ActionType("Edit Issue Description") { Unit }

        @JvmField
        val ISSUE_LINK = ActionType("Link Issue") { Unit }

        @JvmField
        val ISSUE_LINK_LOAD_FORM = ActionType("Link Issue(Load form)") { Unit }

        @JvmField
        val ISSUE_LINK_SEARCH_CHOOSE = ActionType("Link Issue(Search issue and choose)") { Unit }

        @JvmField
        val CREATE_DASHBOARD = ActionType("Create Dashboard") { Unit }

        @JvmField
        val CREATE_GADGET = ActionType("Create Gadget") { Unit }

        @JvmField
        val ISSUE_LINK_SUBMIT = ActionType("Link Issue(Submit)") { Unit }

        @JvmField
        val MANAGE_PROJECTS = ActionType("Manage Projects") { Unit }

        @JvmField
        val SPRINT_CREATE = ActionType("Sprint (Create)") { Unit }

        @JvmField
        val SPRINT_COMPLETE = ActionType("Sprint (Complete)") { Unit }

        @JvmField
        val SPRINT_MOVE_ISSUE = ActionType("Sprint (Move Issue In Sprint)") { Unit }

        @JvmField
        val SPRINT_REORDER_ISSUE = ActionType("Sprint (Reorder issue)") { Unit }

        @JvmField
        val SPRINT_START_SPRINT = ActionType("Sprint (Start sprint)") { Unit }

        @JvmField
        val SPRINT_START_SPRINT_EDITOR = ActionType("Sprint (Start sprint (open pop up))") { Unit }

        @JvmField
        val SPRINT_START_SPRINT_SUBMIT = ActionType("Sprint (Start sprint (submit))") { Unit }

        @JvmField
        val MOVE_ISSUE_STATUS_BOARD = ActionType("Move Issue Status on Board") { Unit }

        @JvmField
        val MOVE_ISSUE_STATUS_BOARD_SUBMIT_WINDOW = ActionType("Move Issue Status on Board (Submit modal window)") { Unit }

        @JvmField
        val SUBSCRIBE_TO_FILTER = ActionType("Subscribe To Filter") { Unit }

        @JvmField
        val SUBSCRIBE_TO_FILTER_DETAILS_WDW = ActionType("Subscribe To Filter (Show Details Window)") { Unit }

        @JvmField
        val SUBSCRIBE_TO_FILTER_NEW_SUBSCR = ActionType("Subscribe To Filter (New Subscription)") { Unit }

        @JvmField
        val SUBSCRIBE_TO_FILTER_SUBSCR_SUBM = ActionType("Subscribe To Filter (Submit Subscribe)") { Unit }

        @JvmField
        val MANAGE_SUBSCR = ActionType("Manage Subscriptions") { Unit }

        @JvmField
        val ATTACH_SCREENSHOT = ActionType("Attach screenshot") { Unit }

        @JvmField
        val CONFIGURE_BOARD = ActionType("Configure board") { Unit }

        @JvmField
        val CREATE_ISSUE_MODAL = ActionType("Create Issue Modal") { Unit }

        @JvmField
        val SWITCH_BETWEEN_ISSUES_IN_SEARCH_RESULTS = ActionType("Switch between issues in search results") { Unit }

        @JvmField
        var TOP_BAR_QUICK_SEARCH = ActionType("Quick search top bar") { Unit }

        @JvmField
        var TOP_BAR_QUICK_SEARCH_SELECT_ITEM = ActionType("Quick search top bar (Select Item)") { Unit }

        @JvmField
        val TRANSITION = ActionType("Transition") { Unit }

        @JvmField
        val TRANSITION_FILL_IN_TIME_SPENT_FORM = ActionType("Transition (Fill in Time Spent form)") { Unit }

        @JvmField
        val BROWSE_PROJECT_ISSUES = ActionType("Browse project issues") { Unit }

        @JvmField
        var OPEN_MEDIA_VIEWER = ActionType("Open media viewer") { Unit }

        @JvmField
        var OPEN_GLOBAL_SEARCH = ActionType("Global issue navigator") { Unit }

        @JvmField
        var VIEW_DASHBOARDS = ActionType("View Dasboards List") { Unit }

        @JvmField
        var VIEW_DASHBOARD = ActionType("View Dasboard") { Unit }

        @JvmField
        var TRANSITION_VIEW_BOARD = ActionType("View Board (Transition)") { Unit }

        @JvmField
        var TRANSITION_ISSUE_NAVIGATOR = ActionType("Issue Navigator (Transition)") { Unit }

        @JvmField
        var TRANSITION_ISSUE_NAVIGATOR_VIEW = ActionType("Issue Navigator View (Transition)") { Unit }

        @JvmField
        var MANAGE_PROJECT_PERMISSIONS = ActionType("Manage Project Permissions") {Unit}
    }
}
