package jces1209.vu.api.dashboard

import jces1209.vu.api.DcApiClient
import java.net.URI

class DcDashboardApi(url: URI) : DcApiClient(url), DashboardApi  {

    override fun deleteDashboard(dashboardId: String) {
        TODO("Not yet implemented")
    }
}
