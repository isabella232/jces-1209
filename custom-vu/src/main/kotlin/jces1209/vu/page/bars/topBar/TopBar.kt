package jces1209.vu.page.bars.topBar

import com.atlassian.performance.tools.jiraactions.api.page.IssuePage
import jces1209.vu.page.AbstractIssuePage

interface TopBar {
    fun waitForTopBar(): TopBar
    fun quickSearch(issueKey: String): TopBar
    fun selectItem(issueKey: String): AbstractIssuePage
}
