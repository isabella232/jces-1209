package jces1209

import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import java.util.*

interface BenchmarkQuality {

    fun provide(trafficConfigObj: Properties?): VirtualUsersSource

    fun behave(
        scenario: Class<out Scenario>,
        trafficConfigObj: Properties?
    ): VirtualUserBehavior
}
