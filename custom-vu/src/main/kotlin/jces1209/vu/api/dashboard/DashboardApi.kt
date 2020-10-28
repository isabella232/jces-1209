package jces1209.vu.api.dashboard

import jces1209.vu.api.ApiClient

interface DashboardApi : ApiClient {

    fun deleteDashboard(dashboardId: String)
}
