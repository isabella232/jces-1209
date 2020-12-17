package jces1209

import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import jces1209.vu.Firefox
import java.time.Duration
import java.util.*

class QuickAndDirty : BenchmarkQuality {

    override fun provide(trafficConfigObj: Properties?): VirtualUsersSource = LocalVus()

    override fun behave(
        scenario: Class<out Scenario>,
        trafficConfigObj: Properties?
    ): VirtualUserBehavior = VirtualUserBehavior.Builder(scenario)
        .browser(Firefox::class.java) // local Chrome is flaky around version 80, so let's use Firefox
        .load(
            VirtualUserLoad.Builder()
                .ramp(Duration.ZERO)
                .flat(Duration.ofMinutes(2))
                .virtualUsers(1)
                .build()
        )
        .skipSetup(true)
        .seed(12345L)
        .build()
}
