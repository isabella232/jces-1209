package jces1209.vu.api.dashboard

import jces1209.vu.api.ApiClient
import jces1209.vu.api.dashboard.model.Dashboards
import okhttp3.Response

interface DashboardApi : ApiClient {

    fun deleteDashboard(dashboardId: String): Response
    fun getDashboards(accountId: String): Dashboards
}
