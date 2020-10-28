package jces1209.vu.api.dashboard

import jces1209.vu.api.CloudApiClient
import java.net.URI

class CloudDashboardApi(url: URI) : CloudApiClient(url), DashboardApi {
    private val uri = "/rest/api/3/dashboard/"

    override fun deleteDashboard(dashboardId: String) {
        delete(uri + dashboardId)
    }
}
