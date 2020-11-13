package jces1209.vu.utils.cleanup.cloud

import jces1209.vu.api.CloudSettings
import jces1209.vu.api.dashboard.CloudDashboardApi
import jces1209.vu.api.dashboard.model.Dashboards
import java.net.URI


fun main(args: Array<String>) {
    val dashboardApi = CloudDashboardApi(URI(CloudSettings.URL))
    var dashboards: Dashboards
    do {
        dashboards = dashboardApi.getDashboards(CloudSettings.ACCOUNT_ID)
        dashboards
            .values
            .parallelStream()
            .forEach {
                val dashboardId = it.id
                repeat(3) {
                    val response = dashboardApi.deleteDashboard(dashboardId)
                    if (response.code() == 204) {
                        return@repeat
                    }
                }
            }
    } while (!dashboards.isLast)
}
