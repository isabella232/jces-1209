package jces1209.vu.api.dashboard

import jces1209.vu.api.DcApiClient
import jces1209.vu.api.dashboard.model.Dashboards
import okhttp3.Response
import java.net.URI

class DcDashboardApi(url: URI) : DcApiClient(url), DashboardApi {

    override fun deleteDashboard(dashboardId: String): Response {
        TODO("Not yet implemented")
    }

    override fun getDashboards(accountId: String): Dashboards {
        TODO("Not yet implemented")
    }
}
