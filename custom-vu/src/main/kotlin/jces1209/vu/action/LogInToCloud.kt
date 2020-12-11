package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.User

/**
 * Logs in with Atlassian ID or Google, depending on the [user] email.
 */
class LogInToCloud(
    private val user: User,
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {

    override fun run() {
        when (user.domain()) {
            "gmail.com", "atlassian.com" -> LogInWithGoogle(user, jira, meter).run()
            else -> LogInWithAtlassianId(user, jira, meter).run()
        }
    }

    private fun User.domain() = name.substringAfter('@')
}
