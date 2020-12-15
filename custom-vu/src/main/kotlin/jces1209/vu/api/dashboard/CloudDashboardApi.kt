package jces1209.vu.api.dashboard

import jces1209.vu.api.CloudApiClient
import jces1209.vu.api.dashboard.model.Dashboards
import okhttp3.Response
import java.net.URI

class CloudDashboardApi(url: URI) : CloudApiClient(url), DashboardApi {
    private val uri = "rest/api/3/dashboard/"
    private val uriSearch = "rest/api/3/dashboard/search"

    override fun deleteDashboard(dashboardId: String): Response {
        return delete(uri + dashboardId).second
    }

    override fun getDashboards(accountId: String): Dashboards {
        return get("$uriSearch?accountId=$accountId", Dashboards::class.java)
    }
}
