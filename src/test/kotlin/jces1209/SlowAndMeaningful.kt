package jces1209

import com.amazonaws.regions.Regions
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.TemporalRate
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.api.browsers.Browser
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import jces1209.vu.EagerChromeBrowser
import java.time.Duration
import java.util.Properties

class SlowAndMeaningful private constructor(
    private val browser: Class<out Browser>,
    private val region: Regions,
    private val duration: Duration
) : BenchmarkQuality {

    override fun provide(trafficConfigObj: Properties?): VirtualUsersSource = AwsVus(duration, region,
        System.getenv("vpcId"),
        System.getenv("subnetId"),
        trafficConfigObj
    )

    override fun behave(scenario: Class<out Scenario>, trafficConfigObj: Properties?): VirtualUserBehavior = VirtualUserBehavior.Builder(scenario)
        .browser(browser)
        .load(
            getVirtualUserLoad(trafficConfigObj)
        )
        .skipSetup(true)
        .seed(12345L)
        .build()

    private fun getVirtualUserLoad(trafficConfigObj: Properties?): VirtualUserLoad {
        var virtualUser = 72
        var ramp = 1L
        var maxOverallLoad = 15.0
        if (null != trafficConfigObj) {
            virtualUser = trafficConfigObj.getProperty("setting.virtualUsers")?.toInt() ?: 72
            ramp = trafficConfigObj.getProperty("setting.rampInMinute")?.toLong() ?: 1
            maxOverallLoad = trafficConfigObj.getProperty("setting.maxOverallLoad")?.toDouble() ?: 15.0
        }
        return VirtualUserLoad.Builder()
            .virtualUsers(virtualUser)
            .ramp(Duration.ofMinutes(ramp))
            .maxOverallLoad(TemporalRate(maxOverallLoad, Duration.ofSeconds(1)))
            .flat(duration)
            .build()
    }

    class Builder {
        private var browser: Class<out Browser> = EagerChromeBrowser::class.java
        private var region: Regions = Regions.US_EAST_1
        private var duration: Duration = System.getenv("duration")
            .takeUnless { it.isNullOrEmpty() }?.toLong()?.let { Duration.ofMinutes(it) } ?: Duration.ofMinutes(20)

        fun browser(browser: Class<out Browser>) = apply { this.browser = browser }
        fun region(region: Regions) = apply { this.region = region }
        fun duration(duration: Duration) = apply { this.duration = duration }

        fun build(): BenchmarkQuality {
            return SlowAndMeaningful(
                browser,
                region,
                duration
            )
        }
    }
}
